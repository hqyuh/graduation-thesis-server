package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.UserAnswerRequestDTO;
import com.hqh.quizserver.dto.UserTestQuizzDTO;



public interface UserAnswerService {

    void saveAllUserAnswer(UserAnswerRequestDTO userAnswerRequestDTO);
    UserTestQuizzDTO reviewAnswerUser(Long quizzId, Long userId);

}
