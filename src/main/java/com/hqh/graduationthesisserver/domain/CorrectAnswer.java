package com.hqh.graduationthesisserver.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrectAnswer {

    private float totalNumberOfCorrectAnswers;
    private float totalNumberOfAnswers;
    private float totalMark;

}
