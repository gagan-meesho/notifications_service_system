package com.assignment.notificationservice.entity.authentication;


import lombok.*;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
@Component
public class JwtRequest {
    private String email;
    private String password;
}
