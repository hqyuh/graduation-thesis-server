package com.hqh.quizserver.dto;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserLoginResponseDTO {
    private String tokenType;
    private String accessToken;
    private Instant expireAt;
}
