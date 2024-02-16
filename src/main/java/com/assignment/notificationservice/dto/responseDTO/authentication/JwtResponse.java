package com.assignment.notificationservice.dto.responseDTO.authentication;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class JwtResponse {
    private String jwtToken;
    private String username;
}
