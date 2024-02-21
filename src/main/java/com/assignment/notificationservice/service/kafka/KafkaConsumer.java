package com.assignment.notificationservice.service.kafka;

import com.assignment.notificationservice.constants.imiconnect.ImiConnectConstants;
import com.assignment.notificationservice.dao.sql.SmsRepository;
import com.assignment.notificationservice.entity.imiconnect.*;
import com.assignment.notificationservice.entity.sql.Request;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
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


    public Boolean checkIfNumberIsBlacklistedViaRedis(Request smsDetails) {
        return redisTemplate.opsForHash().hasKey(KEY, smsDetails.getPhoneNumber());
    }

    Request getSmsRequestDetails(String requestId) {
        Optional<Request> result = smsRepository.findById(Integer.parseInt(requestId));
        Request request = result.get();
        return request;
    }

    public void sendMessageViaThirdParty(String phoneNumber, String message){
        try {
        log.info("sending imi");
        ImiConnectRequest imiConnectRequest = new ImiConnectRequest("sms",
                new Channels(new Sms(
                        message)
                ),
                List.of(
                        new Msisdn(List.of("+91"+phoneNumber), 1)
                )
        );
        log.debug("body of imirequest is : {}", imiConnectRequest);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("key", IMI_API_KEY);
        HttpEntity<ImiConnectRequest> httpEntity = new HttpEntity<ImiConnectRequest>(imiConnectRequest, headers);

        ResponseEntity<FinalResponse> responseEntity = restTemplate.exchange(ImiConnectConstants.host, HttpMethod.POST, httpEntity, FinalResponse.class);
        FinalResponse response = responseEntity.getBody();

            log.debug("imi connect Response is {}", oj.writeValueAsString(response));
        }
        catch (Exception e){
            log.error("Failed to send sms via ImiConnect");
        }
    }

    @KafkaListener(topics = "notification.send_sms", groupId = "smsNotificationGroup")
    public void consumeMessage(String requestId) throws Exception{
        log.info(String.format("Consumed request id : %s from kafka", requestId));
        log.debug(String.format("Querying the database : sms_requests to get the details of the request id %s ", requestId));

        Request request = getSmsRequestDetails(requestId);
        if (request == null) {
            log.error(String.format("Failed to get sms associated with request id %s. ", requestId));
            log.debug(String.format("Couldn't send sms associated with request id %s", requestId));
            return;
        }
        if (checkIfNumberIsBlacklistedViaRedis(request)) {
            log.debug(String.format("Cannot send sms as the number %s associated with request id %s is blacklisted.", request.getPhoneNumber(), requestId));
            request.setStatus("failed");
            request.setFailureComments("this number is blacklisted.");
            request.setUpdatedAt(new Timestamp(new Date().getTime()));
        }
        else {
            log.debug(String.format("Sending sms to phone number %s via 3rd party api.", request.getPhoneNumber()));
            //3rd party integration to send sms

            sendMessageViaThirdParty(request.getPhoneNumber(),request.getMessage());

            log.debug("Successfully sent sms.");
            request.setStatus("sent");
            request.setFailureComments("none");
            request.setFailureCode(400);
            request.setUpdatedAt(new Timestamp(new Date().getTime()));
        }
        smsRepository.save(request);
        SmsRequestIndex smsRequestIndex = new SmsRequestIndex(Integer.toString(request.getId()), request.getPhoneNumber(), request.getMessage(), request.getStatus(), request.getFailureCode() == null ? "0" : Integer.toString(request.getFailureCode()), request.getFailureComments(), new Date(request.getCreatedAt().getTime()), new Date(request.getUpdatedAt().getTime()));
        if (elasticSearchSmsService.index(smsRequestIndex)) {
            log.debug("successfully indexed sms request in elasticsearch");
        } else {
            log.debug("Failed to index sms request in elasticsearch");
        }
    }

}
