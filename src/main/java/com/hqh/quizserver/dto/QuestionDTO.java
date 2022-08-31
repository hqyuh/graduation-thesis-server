package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO implements Serializable {

    private Long id;
    private String topicQuestion;
    private String questionImageUrl;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private float mark;
    private Long quizzId;

}
