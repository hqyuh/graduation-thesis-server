package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrectAnswerDto {

    private float totalNumberOfCorrectAnswers;
    private float totalNumberOfAnswers;
    private float totalMark;

}
