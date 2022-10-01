package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String roles;
    private String password;
}
