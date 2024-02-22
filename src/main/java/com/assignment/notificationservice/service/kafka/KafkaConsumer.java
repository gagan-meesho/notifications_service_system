package com.assignment.notificationservice.service.kafka;

import com.assignment.notificationservice.constants.imiconnect.ImiConnectConstants;
import com.assignment.notificationservice.dao.sql.SmsRepository;
import com.assignment.notificationservice.entity.imiconnect.*;
import com.assignment.notificationservice.entity.sql.Sms;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class KafkaConsumer {

    SmsRepository smsRepository;
    RedisTemplate redisTemplate;

    @Value("${REDIS_KEY}")
    private String KEY;

    ObjectMapper oj = new ObjectMapper();

    @Value("${IMI_API_KEY}")
    private String IMI_API_KEY;
    ElasticSearchSmsService elasticSearchSmsService;

    public KafkaConsumer(SmsRepository smsRepository, RedisTemplate redisTemplate, ElasticSearchSmsService elasticSearchSmsService) {
        this.smsRepository = smsRepository;
        this.elasticSearchSmsService = elasticSearchSmsService;
        this.redisTemplate = redisTemplate;
    }


    public Boolean checkIfNumberIsBlacklistedViaRedis(Sms smsDetails) {
        return redisTemplate.opsForHash().hasKey(KEY, smsDetails.getPhoneNumber());
    }

    com.assignment.notificationservice.entity.sql.Sms getSmsRequestDetails(String requestId) {
        Optional<Sms> result = smsRepository.findById(Integer.parseInt(requestId));
        return result.orElse(null);
    }

    public Boolean sendMessageViaThirdParty(String phoneNumber, String message)throws Exception{
        try {
        ImiConnectRequest imiConnectRequest = new ImiConnectRequest("sms",
                new Channels(new com.assignment.notificationservice.entity.imiconnect.Sms(
                        message)
                ),
                List.of(
                        new Msisdn(List.of("+91"+phoneNumber), 1)
                )
        );
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("key", IMI_API_KEY);
        HttpEntity<ImiConnectRequest> httpEntity = new HttpEntity<ImiConnectRequest>(imiConnectRequest, headers);

        ResponseEntity<FinalResponse> responseEntity = restTemplate.exchange(ImiConnectConstants.host, HttpMethod.POST, httpEntity, FinalResponse.class);
        FinalResponse response = responseEntity.getBody();

            log.debug("imi connect Response is {}", oj.writeValueAsString(response));
            return true;
        }
        catch (Exception e){
            log.error("Failed to send sms via ImiConnect");
            return false;
        }
    }

    @KafkaListener(topics = "notification.send_sms", groupId = "smsNotificationGroup")
    public void consumeMessage(String requestId) throws Exception {
        try {

            Sms sms = getSmsRequestDetails(requestId);
            if (sms == null) {
                log.error(String.format("Failed to get sms associated with request id %s. ", requestId));
                return;
            }
            if (checkIfNumberIsBlacklistedViaRedis(sms)) {

                sms.setStatus("failed");
                sms.setFailureComments("this number is blacklisted.");
                sms.setFailureCode(400);
                sms.setUpdatedAt(new Timestamp(new Date().getTime()));
            } else {
                if (sendMessageViaThirdParty(sms.getPhoneNumber(), sms.getMessage())) {
                    sms.setStatus("sent");
                    sms.setFailureComments("none");
                    sms.setUpdatedAt(new Timestamp(new Date().getTime()));
                } else {
                    sms.setStatus("Imi connect failed");
                    sms.setFailureComments("imi connect didn't send message :(");
                    sms.setUpdatedAt(new Timestamp(new Date().getTime()));
                }
            }

            smsRepository.save(sms);
            SmsRequestIndex smsRequestIndex = new SmsRequestIndex(Integer.toString(sms.getId()), sms.getPhoneNumber(), sms.getMessage(), sms.getStatus(), sms.getFailureCode() == null ? "0" : Integer.toString(sms.getFailureCode()), sms.getFailureComments(), new Date(sms.getCreatedAt().getTime()), new Date(sms.getUpdatedAt().getTime()));
            if (elasticSearchSmsService.index(smsRequestIndex)) {
                log.debug("successfully indexed sms request in elasticsearch");
            } else {
                log.error("Failed to index sms request in elasticsearch");
            }

        }
        catch (Exception e){
            log.error("some exception occured",e);
        }
    }


}
