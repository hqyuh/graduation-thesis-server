package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.UserAnswerQuestionRequestDTO;
import com.hqh.quizserver.dto.UserAnswerRequestDTO;
import com.hqh.quizserver.entities.*;
import com.hqh.quizserver.dto.ReviewAnswerDTO;
import com.hqh.quizserver.mapper.UserAnswerMapper;
import com.hqh.quizserver.repositories.QuestionRepository;
import com.hqh.quizserver.repositories.TestQuizzRepository;
import com.hqh.quizserver.repositories.UserAnswerRepository;
import com.hqh.quizserver.services.UserAnswerService;
import com.hqh.quizserver.services.UserService;
import com.hqh.quizserver.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
    public void saveAllUserAnswer(UserAnswerRequestDTO userAnswerRequestDTO) {

        List<UserAnswer> userAnswerList = new ArrayList<>();
        Long quizzId = userAnswerRequestDTO.getQuizzId();
        TestQuizz testQuizz = quizzRepository.findTestQuizzById(quizzId);
        User user = userService.getCurrentUser();

        for (UserAnswerQuestionRequestDTO value : userAnswerRequestDTO.getListAnswer()) {
            Question question = questionRepository.findQuestionById(value.getQuestionId());
            UserAnswer userAnswer = userAnswerMapper.convertUserAnswerDTOToUserAnswerEntity(value, testQuizz, question, user);
            userAnswer.setIsSelected(CommonUtils.isNull(value.getIsSelected()) ? null : value.getIsSelected());
            userAnswer.setShortAnswer(CommonUtils.isNull(value.getShortAnswer()) ? null : value.getShortAnswer());
            userAnswer.setCreatedAt(new Date());
            userAnswer.setCreatedBy(user.getUsername());
            userAnswer.setUpdatedAt(new Date());
            userAnswer.setUpdatedBy(user.getUsername());

            userAnswerList.add(userAnswer);
        }
        userAnswerRepository.saveAll(userAnswerList);
    }

    @Override
    public List<ReviewAnswerDTO> reviewAnswerUser(Long quizzId, Long userId) {

        List<ReviewAnswerDTO> reviewAnswerDTOS = userAnswerRepository.reviewAnswerUser(quizzId, userId);

        return userAnswerRepository.reviewAnswerUser(quizzId, userId);
    }
}
