package com.assignment.notificationservice.kafka;

import com.assignment.notificationservice.dao.SmsRepository;
import com.assignment.notificationservice.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KafkaConsumer {

    SmsRepository smsRepository;

    public KafkaConsumer(SmsRepository smsRepository) {
        this.smsRepository = smsRepository;
    }

    private static final Logger LOGGER= LoggerFactory.getLogger(KafkaConsumer.class);

    public Boolean checkIfNumberIsBlacklistedViaRedis(Request smsDetails){
//        to do: implement redis for the same
        return true;
    }

    private String url="http://localhost:8080/";
    @KafkaListener(topics = "notification.send_sms", groupId = "smsNotificationGroup")
    public void consumeMessage(String requestId){
        LOGGER.info(String.format("Consumed request id : %s from kafka", requestId));
        LOGGER.info(String.format("Querying the database : sms_requests to get the details of the request id %s ",requestId));
        RestTemplate restTemplate= new RestTemplate();
        String queryUrl=url+"v1/sms/"+requestId;
        ResponseEntity<Request> responseEntity=restTemplate.getForEntity(queryUrl, Request.class);
        LOGGER.info(String.format("Recieved a response entity for the request id : %s",requestId));
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Request smsDetails = responseEntity.getBody();
//            System.out.println("Response: " + smsDetails);
            LOGGER.info("Checking if the number is blacklisted via redis");
            Boolean isNumberBlacklisted=checkIfNumberIsBlacklistedViaRedis(smsDetails);
            if(isNumberBlacklisted){
                LOGGER.info(String.format("The number %s associated with the request id %s is blacklisted, so can't send message.",smsDetails.getPhoneNumber(), requestId));

                LOGGER.info(String.format("Updating the status in the database..."));
                smsDetails.setStatus("failed");
                smsDetails.setFailureCode(400);
                smsDetails.setFailureComments("Number is blacklisted.");
                smsRepository.save(smsDetails);
                LOGGER.info(String.format("Status updated in the database!"));
            }
            else{
                LOGGER.info(String.format("The number %s associated with the request id %s is not blacklisted, so sending message...",smsDetails.getPhoneNumber(),requestId));
//               to do: send the message using 3rd party api.
                LOGGER.info(String.format("Message sent successfully! Updating info in the database..."));
                smsDetails.setStatus("sent");
                smsRepository.save(smsDetails);
                LOGGER.info(String.format("Status updated in the database!"));
            }
        }
        else if (responseEntity.getStatusCode().is4xxClientError()) {
            System.out.println("Request not found");
        }
        else {
            System.out.println("Unexpected response status: " + responseEntity.getStatusCode());
        }
    }
}
