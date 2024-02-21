package com.assignment.notificationservice.controller.smscontroller;

import com.assignment.notificationservice.dao.sql.SmsRepository;
import com.assignment.notificationservice.dto.requestDTO.sql.SendSmsResponseDTO;
import com.assignment.notificationservice.entity.sql.Request;
import com.assignment.notificationservice.dto.responseDTO.sql.SendSmsApiResponse;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.exception.*;
import com.assignment.notificationservice.service.kafka.KafkaProducer;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/v1/sms")
@Slf4j
public class SmsController {
    SmsRepository smsRepository;
    private final KafkaProducer kafkaProducer;
    private final ElasticSearchSmsService elasticSearchSmsService;

    public SmsController(SmsRepository smsRepository, KafkaProducer kafkaProducer, ElasticSearchSmsService elasticSearchSmsService) {
        this.smsRepository = smsRepository;
        this.kafkaProducer = kafkaProducer;
        this.elasticSearchSmsService = elasticSearchSmsService;
    }

    @PostMapping("/send")
    public ResponseEntity<SendSmsApiResponse> sendSmsGivenPhoneNumberAndMessage(@Valid @RequestBody SendSmsResponseDTO sendSmsResponseDTO) throws InternalServerErrorException {
        try {
            log.debug("Received request to send sms to phone number {} with message {}", sendSmsResponseDTO.getPhoneNumber(), sendSmsResponseDTO.getMessage());
            String phoneNumber = sendSmsResponseDTO.getPhoneNumber();
            String message = sendSmsResponseDTO.getMessage();
            Request newSms = new Request(phoneNumber, message, "Sending", null, "");
            Request result = smsRepository.save(newSms);
            if (!Objects.isNull(result)) {
                log.debug("Sms request updated successfully in the database!");
            } else {
                log.debug("Message couldn't be sent. Failed to update sms in database.");
                return ResponseEntity.status(400).body(new SendSmsApiResponse("400", "Failed to send sms."));
            }
            if(kafkaProducer.sendMessage(Integer.toString(result.getId())))
            return ResponseEntity.ok(new SendSmsApiResponse(phoneNumber, "Sms queued successfully!"));
            return ResponseEntity.ok(new SendSmsApiResponse(phoneNumber, "Failed to publish message in kafka. Try again later."));
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new InternalServerErrorException("Internal server error while sending message");
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Request> getSmsGivenRequestId(@PathVariable String requestId) throws Exception {
        try {
            log.debug("Fetching SMS with requestId: {}", requestId);
            Optional<Request> result = smsRepository.findById(Integer.parseInt(requestId));
            Request responseToBeSent = result.orElse(null);

            if (Objects.isNull(responseToBeSent)) {
                log.warn("SMS with requestId {} not found", requestId);
                return ResponseEntity.notFound().build();
            }

            log.debug("SMS with requestId {} fetched successfully", requestId);
            return ResponseEntity.ok(responseToBeSent);
        } catch (NumberFormatException e) {
            log.error("Error fetching SMS with requestId " + requestId, e);
            throw new BadRequestErrorException("Bad request from client");
        } catch (DataAccessException e) {
            log.error("Error fetching SMS with requestId " + requestId, e);
            throw new SqlInternalServerErrorException("Sql database Internal server error");
        }catch (Exception e) {
            log.error("Error fetching SMS with requestId " + requestId, e);
            throw new NotificationServiceException("Couldn't fetch sms");
        }
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllSms() throws SqlInternalServerErrorException {
        try {
            log.debug("Fetching all SMS");
            List<Request> responseToBeSent = smsRepository.findAll();
            log.debug("Fetched {} SMS successfully", responseToBeSent.size());
            return ResponseEntity.ok(responseToBeSent);
        } catch (Exception e) {
            log.error("Error fetching all SMS", e);
            throw new SqlInternalServerErrorException("Sql database Internal server error");
        }
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<SendSmsApiResponse> handleCustomException(CustomException ex) {
        return ResponseEntity.status(400).body(new SendSmsApiResponse("400", ex.getMessage()));
    }
}
