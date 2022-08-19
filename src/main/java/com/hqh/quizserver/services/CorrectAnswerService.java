package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.CorrectAnswerDTO;

public interface CorrectAnswerService {

    CorrectAnswerDTO getTotalNumberOfCorrectAnswers(Long id);

}
