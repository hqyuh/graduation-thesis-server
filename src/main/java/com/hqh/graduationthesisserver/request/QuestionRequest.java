package com.hqh.graduationthesisserver.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionRequest {
    private Long id;
    private String topicQuestion;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String correctResult;
    private String correctEssay;
    private String type;
    private float mark;
    private Long quizzId;
}
