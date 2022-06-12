package com.hqh.quizserver.service;

import com.hqh.quizserver.dto.CorrectAnswerDto;

public interface CorrectAnswerService {

    CorrectAnswerDto getTotalNumberOfCorrectAnswers(Long id);

}
