package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.ReviewAnswerResponseDTO;
import com.hqh.quizserver.dto.UserAnswerQuestionRequestDTO;
import com.hqh.quizserver.dto.UserAnswerRequestDTO;
import com.hqh.quizserver.dto.UserTestQuizzDTO;
import com.hqh.quizserver.entities.*;
import com.hqh.quizserver.mapper.UserAnswerMapper;
import com.hqh.quizserver.repositories.*;
import com.hqh.quizserver.services.UserAnswerService;
import com.hqh.quizserver.services.UserService;
import com.hqh.quizserver.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class UserAnswerServiceImpl implements UserAnswerService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final UserAnswerMapper userAnswerMapper;
    private final TestQuizzRepository quizzRepository;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserAnswerServiceImpl(UserAnswerMapper userAnswerMapper,
                                 TestQuizzRepository quizzRepository,
                                 QuestionRepository questionRepository,
                                 UserAnswerRepository userAnswerRepository,
                                 UserService userService,
                                 UserRepository userRepository) {
        this.userAnswerMapper = userAnswerMapper;
        this.quizzRepository = quizzRepository;
        this.questionRepository = questionRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.userService = userService;
        this.userRepository = userRepository;
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

    private void handleResult(Long quizzId, Long userId) {
        log.info("Handling the result ::");
        List<Question> questionList = questionRepository.findAllByQuizzId(quizzId);
        for (Question question : questionList) {
            UserAnswer userAnswer = userAnswerRepository.getUserAnswerByQuestionIdAndUserId(question.getId(), userId);
            if (userAnswer == null)
                break;

            String answerCorrect = userAnswer.getIsSelected();
            if (question.getCorrectResult().equals(answerCorrect)) {
                userAnswer.setCorrect(true);
                userAnswerRepository.save(userAnswer);
            }
        }
        log.info("End handle the result ::");
    }

    @Override
    public UserTestQuizzDTO reviewAnswerUser(Long quizzId, Long userId) {
        handleResult(quizzId, userId);

        User user = userRepository.findUserById(userId);
        List<ReviewAnswerResponseDTO> reviewAnswerUser = userAnswerRepository.reviewAnswerUser(quizzId, userId);

        return UserTestQuizzDTO
                .builder()
                .username(user.getUsername())
                .mark(0)
                .reviewAnswerResponseDTOList(reviewAnswerUser)
                .build();
    }
}
