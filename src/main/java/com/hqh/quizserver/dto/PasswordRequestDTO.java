package com.hqh.quizserver.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordRequestDTO {

    private String email;
    private String oldPassword;
    private String newPassword;

}
