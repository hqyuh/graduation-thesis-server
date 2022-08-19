package com.hqh.quizserver.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CorrectAnswerDTO {

    private float totalNumberOfCorrectAnswers;
    private float totalNumberOfAnswers;
    private float totalMark;

}
