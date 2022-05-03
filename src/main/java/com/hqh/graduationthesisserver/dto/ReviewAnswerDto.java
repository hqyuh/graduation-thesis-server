package com.hqh.graduationthesisserver.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReviewAnswerDto implements Serializable {

    private String topicQuestion;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String correctResult;
    private String isSelected;

}
