package com.hqh.quizserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestQuizzDTO implements Serializable {

    private Long id;
    private String testName;
    private Integer examTime;
    private Instant dateCreated;
    private Timestamp isStart;
    private Timestamp isEnd;
    private String activationCode;
    private List<QuestionDTO> questionDTOList;

}
