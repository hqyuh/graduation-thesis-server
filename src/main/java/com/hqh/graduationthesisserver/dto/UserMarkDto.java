package com.hqh.graduationthesisserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserMarkDto {

    private Long id;
    private float mark;
    private Instant completedDate;
    private String quizzName;
    private String username;
    private Long quizzId;

}
