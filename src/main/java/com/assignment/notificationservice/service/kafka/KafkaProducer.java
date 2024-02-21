package com.assignment.notificationservice.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class KafkaProducer {
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Boolean sendMessage(String message) {
        log.debug("Publishing the request id {} to Kafka", message);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("notification.send_sms", message);
        try {
            future.get();
            return true; // If message is successfully sent
        } catch (Exception e) {
            log.error("Failed to send message to Kafka");
            return false; // If there is an exception while sending the message
        }
    }

}
