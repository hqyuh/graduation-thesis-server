package com.hqh.graduationthesisserver.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Password {

    private String email;
    private String oldPassword;
    private String newPassword;

}
