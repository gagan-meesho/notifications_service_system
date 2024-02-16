package com.assignment.notificationservice.controller.sql;

import com.assignment.notificationservice.dao.sql.SmsRepository;
import com.assignment.notificationservice.dto.requestDTO.sql.SendSmsResponseDTO;
import com.assignment.notificationservice.entity.sql.Request;
import com.assignment.notificationservice.dto.responseDTO.sql.SendSmsApiResponse;
import com.assignment.notificationservice.entity.elasticsearch.SmsRequestIndex;
import com.assignment.notificationservice.exception.CustomException;
import com.assignment.notificationservice.service.kafka.KafkaProducer;
import com.assignment.notificationservice.service.elasticsearch.ElasticSearchSmsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/sms")
public class SmsController {
    SmsRepository smsRepository;
    private final KafkaProducer kafkaProducer;
    private final ElasticSearchSmsService elasticSearchSmsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsController.class);

    public SmsController(SmsRepository smsRepository, KafkaProducer kafkaProducer, ElasticSearchSmsService elasticSearchSmsService) {
        this.smsRepository = smsRepository;
        this.kafkaProducer = kafkaProducer;
        this.elasticSearchSmsService = elasticSearchSmsService;
    }

    @PostMapping("/send")
    public ResponseEntity<SendSmsApiResponse> sendSmsGivenPhoneNumberAndMessage(@Valid @RequestBody SendSmsResponseDTO sendSmsResponseDTO) {
        try {
            LOGGER.info("Received request to send sms to phone number {} with message {}", sendSmsResponseDTO.getPhoneNumber(), sendSmsResponseDTO.getMessage());
            var phoneNumber = sendSmsResponseDTO.getPhoneNumber();
            var message = sendSmsResponseDTO.getMessage();
            var newSms = new Request(phoneNumber, message, "Sending", null, "");
            var result = smsRepository.save(newSms);
            if (result != null) {
                LOGGER.info("Sms request updated successfully in the database!");
            } else {
                LOGGER.info("Message couldn't be sent. Failed to update sms in database.");
                return ResponseEntity.status(400).body(new SendSmsApiResponse("400", "Failed to send sms."));
            }
            kafkaProducer.sendMessage(Integer.toString(result.getId()));

            SmsRequestIndex smsRequestIndex = new SmsRequestIndex(Integer.toString(result.getId()), result.getPhoneNumber(), result.getMessage(), result.getStatus(), result.getFailureCode() == null ? "0" : Integer.toString(result.getFailureCode()), result.getFailureComments(), result.getCreatedAt(), result.getUpdatedAt());

            if (elasticSearchSmsService.index(smsRequestIndex)) {
                LOGGER.info("successfully indexed sms request in elasticsearch");
            } else {
                LOGGER.info("Failed to index sms request in elasticsearch");
            }
            return ResponseEntity.ok(new SendSmsApiResponse(phoneNumber, "Sms request sent successfully!"));
        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
            return handleCustomException(new CustomException("Failed to send sms."));
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Request> getSmsGivenRequestId(@PathVariable String requestId) {
        try {
            LOGGER.info("Fetching SMS with requestId: {}", requestId);
            Optional<Request> result = smsRepository.findById(Integer.parseInt(requestId));
            Request responseToBeSent = result.orElse(null);

            if (responseToBeSent == null) {
                LOGGER.warn("SMS with requestId {} not found", requestId);
                return ResponseEntity.notFound().build();
            }

            LOGGER.info("SMS with requestId {} fetched successfully", requestId);
            return ResponseEntity.ok(responseToBeSent);
        } catch (Exception e) {
            LOGGER.error("Error fetching SMS with requestId " + requestId, e);
            throw new CustomException("Couldn't fetch sms");
        }
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllSms() {
        try {
            LOGGER.info("Fetching all SMS"); // Log before fetching
            List<Request> responseToBeSent = smsRepository.findAll();
            LOGGER.info("Fetched {} SMS successfully", responseToBeSent.size()); // Log after fetching
            return ResponseEntity.ok(responseToBeSent);
        } catch (Exception e) {
            LOGGER.error("Error fetching all SMS", e); // Log exception
            throw new CustomException("Couldn't fetch SMS");
        }
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<SendSmsApiResponse> handleCustomException(CustomException ex) {
        return ResponseEntity.status(400).body(new SendSmsApiResponse("400", ex.getMessage()));
    }
}
