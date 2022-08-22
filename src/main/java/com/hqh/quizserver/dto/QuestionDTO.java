package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionDTO implements Serializable {

    private Long id;
    private String topicQuestion;
    private String questionImageUrl;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String correctResult;
    private String correctEssay;
    private float mark;
    private int milestones;
    private Long quizzId;

}
