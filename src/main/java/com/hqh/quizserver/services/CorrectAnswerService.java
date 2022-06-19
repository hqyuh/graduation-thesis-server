package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.CorrectAnswerDto;

public interface CorrectAnswerService {

    CorrectAnswerDto getTotalNumberOfCorrectAnswers(Long id);

}
