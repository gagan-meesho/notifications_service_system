package com.assignment.notificationservice.controller.smscontroller;

import com.assignment.notificationservice.dao.sql.SmsRepository;
import com.assignment.notificationservice.dto.requestDTO.sql.SendSmsResponseDTO;
import com.assignment.notificationservice.dto.responseDTO.sql.SendSmsApiResponse;
import com.assignment.notificationservice.dto.responseDTO.sql.SendSmsFailure;
import com.assignment.notificationservice.entity.sql.Sms;
import com.assignment.notificationservice.exception.BadRequestErrorException;
import com.assignment.notificationservice.exception.InternalServerErrorException;
import com.assignment.notificationservice.exception.SqlInternalServerErrorException;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import com.assignment.notificationservice.service.kafka.KafkaProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/v1/sms")
@Slf4j
@EnableWebMvc
public class SmsController {
    SmsRepository smsRepository;
    private final KafkaProducer kafkaProducer;
    private final ElasticSearchSmsService elasticSearchSmsService;
    private String regex = "^[0-9]+$";

    public SmsController(SmsRepository smsRepository, KafkaProducer kafkaProducer, ElasticSearchSmsService elasticSearchSmsService) {
        this.smsRepository = smsRepository;
        this.kafkaProducer = kafkaProducer;
        this.elasticSearchSmsService = elasticSearchSmsService;
    }

    @PostMapping("/send")
    public ResponseEntity<SendSmsApiResponse> sendSmsGivenPhoneNumberAndMessage(@Valid @RequestBody SendSmsResponseDTO sendSmsResponseDTO, BindingResult bindingResult) throws Exception {
        try {
            if (bindingResult.hasErrors()) {
                SendSmsFailure sendSmsFailure=new SendSmsFailure("Please enter a valid phone number");
                sendSmsFailure.setMessage(sendSmsResponseDTO.getMessage());
                sendSmsFailure.setPhoneNumber(sendSmsResponseDTO.getPhoneNumber());
                return ResponseEntity.badRequest().body(sendSmsFailure);
            }
            String phoneNumber = sendSmsResponseDTO.getPhoneNumber();
            String message = sendSmsResponseDTO.getMessage();
            Sms newSms = new Sms(phoneNumber, message, "Sending", null, "");
            Sms result = smsRepository.save(newSms);
            if (!Objects.isNull(result)) {
                log.debug("Sms request updated successfully in the database!");
            } else {
                return ResponseEntity.status(400).body(new SendSmsApiResponse("400", "Failed to send sms."));
            }
            if(kafkaProducer.sendMessage(Integer.toString(result.getId())))
            return ResponseEntity.ok(new SendSmsApiResponse(phoneNumber, "Sms queued successfully!"));
            return ResponseEntity.ok(new SendSmsApiResponse(phoneNumber, "Failed to publish message in kafka. Try again later."));
        } catch (Exception e) {
            log.error("Internal server error while sending message");
            throw new InternalServerErrorException("Internal server error while sending message");
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Sms> getSmsGivenRequestId(@PathVariable String requestId)  throws BadRequestErrorException {
        if(!Pattern.compile(regex).matcher(requestId).matches()){
                throw new BadRequestErrorException("request id passed in is invalid.");
            }
                Optional<Sms> result = smsRepository.findById(Integer.parseInt(requestId));
                Sms resultResponse = result.orElse(null);

                if (Objects.isNull(resultResponse)) {
                    return ResponseEntity.notFound().build();
                }
                log.debug("SMS with requestId {} fetched successfully", requestId);
                return ResponseEntity.ok(resultResponse);
    }

    @GetMapping
    public ResponseEntity<List<Sms>> getAllSms() throws SqlInternalServerErrorException {
        try {
            List<Sms> resultResponse = smsRepository.findAll();
            log.debug("Fetched {} SMS successfully", resultResponse.size());
            return ResponseEntity.ok(resultResponse);
        } catch (Exception e) {
            log.error("Error fetching all SMS");
            throw new SqlInternalServerErrorException("Sql database Internal server error");
        }
    }



}
