package com.hqh.quizserver.service;

import com.hqh.quizserver.dto.ReviewAnswerDto;
import com.hqh.quizserver.dto.UserAnswerDto;

import java.util.List;


public interface UserAnswerService {

    void saveAllUserAnswer(List<UserAnswerDto> userAnswerDto);

    List<ReviewAnswerDto> reviewAnswerUser(Long quizzId, Long userId);

}
