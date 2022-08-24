package com.hqh.quizserver.dto;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private Timestamp dateOfBirth;

}
