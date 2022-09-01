package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestQuizzRequestDTO {
    private String currentTestName;
    private String testName;
    private String examTime;
    private String isStart;
    private String isEnd;
    private String topicId;
}
