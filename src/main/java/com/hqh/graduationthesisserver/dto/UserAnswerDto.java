package com.hqh.graduationthesisserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswerDto implements Serializable {

    private Long id;
    private Long quizzId;
    private Long questionId;
    private Long userId;
    private String isSelected;
    private String shortAnswer;

}
