package com.assignment.notificationservice.controller;

import com.assignment.notificationservice.dao.SmsRepository;
import com.assignment.notificationservice.dto.requestbodydto.SendSmsResponseDTO;
import com.assignment.notificationservice.entity.Request;
import com.assignment.notificationservice.dto.responsedto.SuccessfullySentSmsApiResponse;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.kafka.KafkaProducer;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/sms")
public class SmsController {
    SmsRepository smsRepository;
    private KafkaProducer kafkaProducer;
    private ElasticSearchSmsService elasticSearchSmsService;
    private static final Logger LOGGER= LoggerFactory.getLogger(SmsController.class);

    public SmsController(SmsRepository smsRepository, KafkaProducer kafkaProducer,ElasticSearchSmsService elasticSearchSmsService) {
        this.smsRepository = smsRepository;
        this.kafkaProducer = kafkaProducer;
        this.elasticSearchSmsService = elasticSearchSmsService;
    }

    @PostMapping("/send")
    public ResponseEntity<SuccessfullySentSmsApiResponse> sendSmsGivenPhoneNumberAndMessage(@Valid @RequestBody SendSmsResponseDTO sendSmsResponseDTO){
        try {
            LOGGER.info("Received request to send sms to phone number {} with message {}",sendSmsResponseDTO.getPhoneNumber(),sendSmsResponseDTO.getMessage());
            var phoneNumber = sendSmsResponseDTO.getPhoneNumber();
            var message = sendSmsResponseDTO.getMessage();
            var newSms = new Request(phoneNumber, message, "Sending", null, "");
            var result = smsRepository.save(newSms);
            if (result != null) {
                LOGGER.info(String.format("Sms request updated successfully in the database!"));
            } else {
                LOGGER.info(String.format("Failed to updated the sms request in the database. Please try again!"));
                return ResponseEntity.status(400).body(new SuccessfullySentSmsApiResponse("400","Failed to send sms request"));
            }
//        System.out.println("this is what i recieved after saving"+ result);
//        System.out.println("this is the id bro : "+result.getId());
            kafkaProducer.sendMessage(Integer.toString(result.getId()));

            SmsRequestIndex smsRequestIndex = new SmsRequestIndex(Integer.toString(result.getId()), result.getPhoneNumber(), result.getMessage(), result.getStatus(), result.getFailureCode()==null?"0":Integer.toString(result.getFailureCode()), result.getFailureComments(), new Date(result.getCreatedAt().getTime()), new Date(result.getUpdatedAt().getTime()));
            if (elasticSearchSmsService.index(smsRequestIndex)) {
                LOGGER.info("successfully indexed sms request in elasticsearch");
            } else {
                LOGGER.info(String.format("Failed to index sms request in elasticsearch"));
            }
            return ResponseEntity.ok(new SuccessfullySentSmsApiResponse(phoneNumber,"Sms request sent successfully!"));
        }catch (Exception e){
            LOGGER.error(String.valueOf(e));
            return ResponseEntity.status(400).body(new SuccessfullySentSmsApiResponse("400","Failed to send sms request"));

        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Request> getSmsGivenRequestId(@PathVariable String requestId){
        Optional<Request> result= smsRepository.findById(Integer.parseInt(requestId));
        Request responseToBeSent=result.get();
        if(responseToBeSent==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responseToBeSent);
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllSms(){
        List<Request> responseToBeSent= smsRepository.findAll();
        return ResponseEntity.ok(responseToBeSent);
    }

}
