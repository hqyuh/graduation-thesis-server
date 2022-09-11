package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.ReviewAnswerDto;
import com.hqh.quizserver.dto.UserAnswerDTO;
import com.hqh.quizserver.dto.UserAnswerRequestDTO;

import java.util.List;


public interface UserAnswerService {

    void saveAllUserAnswer(UserAnswerRequestDTO userAnswerRequestDTO);

    List<ReviewAnswerDto> reviewAnswerUser(Long quizzId, Long userId);

}
