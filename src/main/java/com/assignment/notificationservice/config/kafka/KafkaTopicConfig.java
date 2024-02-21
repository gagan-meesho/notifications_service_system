package com.assignment.notificationservice.config.kafka;

import com.assignment.notificationservice.constants.kafka.KafkaConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic createNewTopic() {
        return TopicBuilder.name(KafkaConstants.KAFKA_TOPIC_NAME).build();
    }
}
