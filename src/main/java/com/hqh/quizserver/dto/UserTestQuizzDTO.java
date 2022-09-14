package com.hqh.quizserver.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserTestQuizzDTO {
    private String username;
    private double mark;
    private List<IReviewAnswerResponse> reviewAnswerResponseDTOList;
}
