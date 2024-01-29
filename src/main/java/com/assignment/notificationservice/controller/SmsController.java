package com.assignment.notificationservice.controller;

import com.assignment.notificationservice.dao.SmsRepository;
import com.assignment.notificationservice.dto.SendSmsResponseDTO;
import com.assignment.notificationservice.entity.Request;
import com.assignment.notificationservice.entity.SuccessfullySentSmsApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/sms")
public class SmsController {
    SmsRepository smsRepository;

    @Autowired
    public SmsController(SmsRepository smsRepository){
        this.smsRepository = smsRepository;
    }

    @PostMapping("/send")
    public SuccessfullySentSmsApiResponse sendSmsGivenPhoneNumberAndMessage(@Valid @RequestBody SendSmsResponseDTO sendSmsResponseDTO){
        var phoneNumber= sendSmsResponseDTO.getPhoneNumber();
        var message= sendSmsResponseDTO.getMessage();
        var newSms=new Request(phoneNumber,message,"Sending",null,"");
        smsRepository.save(newSms);
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
