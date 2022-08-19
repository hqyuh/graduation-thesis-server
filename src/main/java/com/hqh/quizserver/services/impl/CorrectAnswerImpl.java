package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.CorrectAnswerDTO;
import com.hqh.quizserver.repositories.UserAnswerRepository;
import com.hqh.quizserver.services.CorrectAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorrectAnswerImpl implements CorrectAnswerService {

    private final UserAnswerRepository userAnswerRepository;

    @Autowired
    public CorrectAnswerImpl(UserAnswerRepository userAnswerRepository) {
        this.userAnswerRepository = userAnswerRepository;
    }


    @Override
    public CorrectAnswerDTO getTotalNumberOfCorrectAnswers(Long id) {
        return CorrectAnswerDTO
                .builder()
                .totalNumberOfCorrectAnswers(userAnswerRepository.totalNumberOfCorrectAnswersByQuizzId(id))
                .totalNumberOfCorrectAnswers(userAnswerRepository.totalNumberOfCorrectAnswersByQuizzId(id))
                .totalMark(userAnswerRepository.totalMarkByQuizzId(id))
                .build();
    }

}
