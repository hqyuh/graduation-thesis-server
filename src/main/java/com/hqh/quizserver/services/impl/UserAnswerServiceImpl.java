package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.*;
import com.hqh.quizserver.entity.*;
import com.hqh.quizserver.mapper.UserAnswerMapper;
import com.hqh.quizserver.mapper.UserMarkMapper;
import com.hqh.quizserver.repositories.*;
import com.hqh.quizserver.repository.*;
import com.hqh.quizserver.services.UserAnswerService;
import com.hqh.quizserver.services.UserService;
import com.hqh.quizserver.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private final UserMarkMapper userMarkMapper;
    private final UserMarkRepository userMarkRepository;

    @Autowired
    public UserAnswerServiceImpl(UserAnswerMapper userAnswerMapper,
                                 TestQuizzRepository quizzRepository,
                                 QuestionRepository questionRepository,
                                 UserAnswerRepository userAnswerRepository,
                                 UserService userService,
                                 UserRepository userRepository,
                                 UserMarkMapper userMarkMapper,
                                 UserMarkRepository userMarkRepository) {
        this.userAnswerMapper = userAnswerMapper;
        this.quizzRepository = quizzRepository;
        this.questionRepository = questionRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMarkMapper = userMarkMapper;
        this.userMarkRepository = userMarkRepository;
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

        // handle result
        this.handleResult(quizzId, user.getId());

        // handle score
        this.handleScoreProcessing(testQuizz, user);
    }

    private void handleScoreProcessing(TestQuizz testQuizz, User user) {
        String username = user.getUsername();
        Long quizzId = testQuizz.getId();
        UserMarkDTO userMarkDTO = new UserMarkDTO();
        UserMark userMark = userMarkMapper.convertDTOToUserMark(userMarkDTO, testQuizz, user);
        userMark.setMark(userAnswerRepository.totalMarkByQuizzId(quizzId, user.getId()));
        userMark.setCompletedDate(Instant.now());
        userMark.setPointLock(false);
        userMark.setCreatedAt(new Date());
        userMark.setCreatedBy(username);
        userMark.setUpdatedAt(new Date());
        userMark.setUpdatedBy(username);

        userMarkRepository.save(userMark);
    }

    private void handleResult(Long quizzId, Long userId) {
        log.info("Handling the result ::");
        List<Question> questionList = questionRepository.findAllByQuizzId(quizzId);
        for (Question question : questionList) {
            List<UserAnswer> userAnsweredList = userAnswerRepository.getAllUserAnswerByQuestionIdAndUserId(question.getId(), userId);
            for (UserAnswer userAnswered : userAnsweredList) {
                if (!userAnswered.isUsed() && question.getCorrectResult().equals(userAnswered.getIsSelected())) {
                    userAnswered.setCorrect(true);
                    userAnswerRepository.save(userAnswered);
                }
            }

        }
        log.info("End handle the result ::");
    }

    @Override
    public UserTestQuizzDTO reviewAnswerUser(Long quizzId, Long userId) {

        User user = userRepository.findUserById(userId);
        List<IReviewAnswerResponse> reviewAnswerUser = userAnswerRepository.reviewAnswerUser(quizzId, userId);

        return UserTestQuizzDTO.builder()
                .username(user.getUsername())
                .mark(0)
                .reviewAnswerResponseDTOList(reviewAnswerUser)
                .build();
    }
}
