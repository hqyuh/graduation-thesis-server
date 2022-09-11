package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAnswerQuestionRequestDTO {
    private Long questionId;
    private String isSelected;
    private String shortAnswer;
}
