package com.assignment.notificationservice.controller;

import com.assignment.notificationservice.dao.SmsRepository;
import com.assignment.notificationservice.dto.SendSmsResponseDTO;
import com.assignment.notificationservice.entity.Request;
import com.assignment.notificationservice.entity.SuccessfullySentSmsApiResponse;
import com.assignment.notificationservice.kafka.KafkaProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/sms")
public class SmsController {
    SmsRepository smsRepository;
    private KafkaProducer kafkaProducer;

    public SmsController(SmsRepository smsRepository, KafkaProducer kafkaProducer) {
        this.smsRepository = smsRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/send")
    public SuccessfullySentSmsApiResponse sendSmsGivenPhoneNumberAndMessage(@Valid @RequestBody SendSmsResponseDTO sendSmsResponseDTO){
        var phoneNumber= sendSmsResponseDTO.getPhoneNumber();
        var message= sendSmsResponseDTO.getMessage();
        var newSms=new Request(phoneNumber,message,"Sending",null,"");
        var result=smsRepository.save(newSms);
//        System.out.println("this is what i recieved after saving"+ result);
//        System.out.println("this is the id bro : "+result.getId());
        kafkaProducer.sendMessage(Integer.toString(result.getId()));
        return new SuccessfullySentSmsApiResponse(phoneNumber,"Successfully sent");
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

}
