package com.hqh.quizserver.service.impl;

import com.hqh.quizserver.domain.*;
import com.hqh.quizserver.dto.ReviewAnswerDto;
import com.hqh.quizserver.dto.UserAnswerDto;
import com.hqh.quizserver.mapper.UserAnswerMapper;
import com.hqh.quizserver.repository.QuestionRepository;
import com.hqh.quizserver.repository.TestQuizzRepository;
import com.hqh.quizserver.repository.UserAnswerRepository;
import com.hqh.quizserver.service.UserAnswerService;
import com.hqh.quizserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserAnswerServiceImpl implements UserAnswerService {

    private final UserAnswerMapper userAnswerMapper;
    private final TestQuizzRepository quizzRepository;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserService userService;

    @Autowired
    public UserAnswerServiceImpl(UserAnswerMapper userAnswerMapper,
                                 TestQuizzRepository quizzRepository,
                                 QuestionRepository questionRepository,
                                 UserAnswerRepository userAnswerRepository,
                                 UserService userService) {
        this.userAnswerMapper = userAnswerMapper;
        this.quizzRepository = quizzRepository;
        this.questionRepository = questionRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.userService = userService;
    }

    /***
     *
     * @param userAnswerDto
     */
    @Override
    public void saveAllUserAnswer(List<UserAnswerDto> userAnswerDto) {
        List<UserAnswer> userAnswerList = new ArrayList<>();
        for (UserAnswerDto userAnswerTemp : userAnswerDto) {
            TestQuizz quizzId = quizzRepository.findTestQuizzById(userAnswerTemp.getQuizzId());
            Question questionId = questionRepository.findQuestionById(userAnswerTemp.getQuestionId());
            User userId = userService.getCurrentUser();
            UserAnswer userAnswer = userAnswerMapper
                    .map(userAnswerTemp, quizzId, questionId, userId);
            userAnswer.setIsSelected(userAnswerTemp.getIsSelected());
            userAnswer.setShortAnswer(userAnswerTemp.getShortAnswer());
            userAnswerList.add(userAnswer);

        }
        userAnswerRepository.saveAll(userAnswerList);
    }

    @Override
    public List<ReviewAnswerDto> reviewAnswerUser(Long quizzId, Long userId) {
        return userAnswerRepository.reviewAnswerUser(quizzId, userId);
    }
}
