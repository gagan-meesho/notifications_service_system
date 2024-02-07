package com.assignment.notificationservice.kafka;

import com.assignment.notificationservice.dao.SmsRepository;
import com.assignment.notificationservice.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class KafkaConsumer {

    SmsRepository smsRepository;
    RedisTemplate redisTemplate;

    @Value("${REDIS_KEY}")
    private String KEY;

    public KafkaConsumer(SmsRepository smsRepository, RedisTemplate redisTemplate) {
        this.smsRepository = smsRepository;
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

    }

}

