package com.hqh.graduationthesisserver.service.impl;

import com.hqh.graduationthesisserver.domain.*;
import com.hqh.graduationthesisserver.dto.ReviewAnswerDto;
import com.hqh.graduationthesisserver.dto.UserAnswerDto;
import com.hqh.graduationthesisserver.mapper.UserAnswerMapper;
import com.hqh.graduationthesisserver.repository.QuestionRepository;
import com.hqh.graduationthesisserver.repository.TestQuizzRepository;
import com.hqh.graduationthesisserver.repository.UserAnswerRepository;
import com.hqh.graduationthesisserver.service.UserAnswerService;
import com.hqh.graduationthesisserver.service.UserService;
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
            userAnswerList.add(userAnswer);

        }

        userAnswerRepository.saveAll(userAnswerList);
    }

    @Override
    public List<ReviewAnswerDto> reviewAnswerUser(Long quizzId) {
        User userId = userService.getCurrentUser();
        return userAnswerRepository.reviewAnswerUser(quizzId, userId.getId());
    }
}
