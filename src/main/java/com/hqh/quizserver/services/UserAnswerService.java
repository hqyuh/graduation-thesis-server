package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.ReviewAnswerDTO;
import com.hqh.quizserver.dto.UserAnswerDTO;

import java.util.List;


public interface UserAnswerService {

    void saveAllUserAnswer(List<UserAnswerDTO> userAnswerDto);

    List<ReviewAnswerDTO> reviewAnswerUser(Long quizzId, Long userId);

}
