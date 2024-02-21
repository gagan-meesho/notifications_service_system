package com.assignment.notificationservice;

import com.assignment.notificationservice.config.resttemplate.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

//@EnableRetry
@SpringBootApplication
@Import(AppConfig.class) // Import your configuration class
public class NotificationserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationserviceApplication.class, args);
    }

}
