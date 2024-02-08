package com.assignment.notificationservice.kafka;

import com.assignment.notificationservice.dao.SmsRepository;
import com.assignment.notificationservice.entity.Request;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Service
public class KafkaConsumer {

    SmsRepository smsRepository;
    RedisTemplate redisTemplate;

    @Value("${REDIS_KEY}")
    private String KEY;
    ElasticSearchSmsService elasticSearchSmsService;
    public KafkaConsumer(SmsRepository smsRepository, RedisTemplate redisTemplate,ElasticSearchSmsService elasticSearchSmsService) {
        this.smsRepository = smsRepository;
        this.elasticSearchSmsService=elasticSearchSmsService;
        this.redisTemplate=redisTemplate;
    }

    private static final Logger LOGGER= LoggerFactory.getLogger(KafkaConsumer.class);

    public Boolean checkIfNumberIsBlacklistedViaRedis(Request smsDetails){
        return redisTemplate.opsForHash().hasKey(KEY,smsDetails.getPhoneNumber());
    }
    Request getSmsRequestDetails(String requestId){
        Optional<Request> result= smsRepository.findById(Integer.parseInt(requestId));
        Request request=result.get();
        return request;
    }
    @KafkaListener(topics = "notification.send_sms", groupId = "smsNotificationGroup")
    public void consumeMessage(String requestId) {
        LOGGER.info(String.format("Consumed request id : %s from kafka", requestId));
        LOGGER.info(String.format("Querying the database : sms_requests to get the details of the request id %s ", requestId));

         Request request=getSmsRequestDetails(requestId);
         if(request==null){
             LOGGER.error(String.format("Failed to get sms associated with request id %s. ",requestId));
             LOGGER.info(String.format("Couldn't send sms associated with request id %s",requestId));
             return;
         }
         if(checkIfNumberIsBlacklistedViaRedis(request)){
             LOGGER.info(String.format("Cannot send sms as the number %s associated with request id %s is blacklisted.",request.getPhoneNumber(),requestId));
             return;
         }
         LOGGER.info(String.format("Sending sms to phone number %s via 3rd party api.",request.getPhoneNumber()));
         //3rd party integration to send sms

        LOGGER.info(String.format("Successfully sent sms."));
        request.setStatus("sent");
        request.setFailureComments("none");
        request.setUpdatedAt(new Timestamp(new Date().getTime()));
        smsRepository.save(request);
        SmsRequestIndex smsRequestIndex = new SmsRequestIndex(Integer.toString(request.getId()), request.getPhoneNumber(), request.getMessage(), request.getStatus(), request.getFailureCode()==null?"0":Integer.toString(request.getFailureCode()), request.getFailureComments(), new Date(request.getCreatedAt().getTime()), new Date(request.getUpdatedAt().getTime()));
        if (elasticSearchSmsService.index(smsRequestIndex)) {
            LOGGER.info("successfully indexed sms request in elasticsearch");
        } else {
            LOGGER.info(String.format("Failed to index sms request in elasticsearch"));
        }
        elasticSearchSmsService.index(smsRequestIndex);
    }

}

