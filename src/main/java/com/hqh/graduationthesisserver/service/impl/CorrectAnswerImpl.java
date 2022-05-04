package com.hqh.graduationthesisserver.service.impl;

import com.hqh.graduationthesisserver.dto.CorrectAnswerDto;
import com.hqh.graduationthesisserver.repository.UserAnswerRepository;
import com.hqh.graduationthesisserver.service.CorrectAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorrectAnswerImpl implements CorrectAnswerService {

    private final UserAnswerRepository userAnswerRepository;

    @Autowired
    public CorrectAnswerImpl(UserAnswerRepository userAnswerRepository) {
        this.userAnswerRepository = userAnswerRepository;
    }

    /***
     * get total correct answer and score
     *
     * @param id
     * @return
     */
    @Override
    public CorrectAnswerDto getTotalNumberOfCorrectAnswers(Long id) {

        CorrectAnswerDto correctAnswer = new CorrectAnswerDto();
        correctAnswer.setTotalNumberOfAnswers(userAnswerRepository.totalNumberOfAnswersByQuizzId(id));
        correctAnswer.setTotalNumberOfCorrectAnswers(userAnswerRepository.totalNumberOfCorrectAnswersByQuizzId(id));
        correctAnswer.setTotalMark(userAnswerRepository.totalMarkByQuizzId(id));

        return correctAnswer;

    }

}
