package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReportDto {

    private Long id;
    private String type;
    private String comment;
    private Long userId;

}
