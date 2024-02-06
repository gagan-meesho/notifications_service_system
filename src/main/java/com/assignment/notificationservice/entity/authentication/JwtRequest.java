package com.assignment.notificationservice.entity.authentication;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class JwtRequest {
    private String email;
    private String password;
}
