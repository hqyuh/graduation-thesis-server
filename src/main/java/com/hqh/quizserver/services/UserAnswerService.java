package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.ReviewAnswerDTO;
import com.hqh.quizserver.dto.UserAnswerRequestDTO;

import java.util.List;


public interface UserAnswerService {

    void saveAllUserAnswer(UserAnswerRequestDTO userAnswerRequestDTO);

    List<ReviewAnswerDTO> reviewAnswerUser(Long quizzId, Long userId);

}
