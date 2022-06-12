package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestQuizzDto implements Serializable {

    private Long id;
    private String testName;
    private Integer examTime;
    private Instant dateCreated;
    private Timestamp isStart;
    private Timestamp isEnd;
    private String activationCode;
    private Long topicId;

}
