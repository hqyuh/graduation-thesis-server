package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAnswerRequestDTO {
    private Long quizzId;
    private Set<UserAnswerQuestionRequestDTO> listAnswer;
}
