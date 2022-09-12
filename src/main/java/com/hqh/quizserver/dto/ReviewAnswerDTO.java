package com.hqh.quizserver.dto;

import lombok.*;


import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReviewAnswerDTO implements Serializable {
    private String topicQuestion;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;

    private String isSelected;
    private String correctResult;

    private String shortAnswer;
    private String correctEssay;
}
