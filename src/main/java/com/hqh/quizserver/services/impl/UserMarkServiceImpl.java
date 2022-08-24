package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.UserDTO;
import com.hqh.quizserver.dto.UserMarkDTO;
import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.entities.User;
import com.hqh.quizserver.entities.UserMark;
import com.hqh.quizserver.helper.quizz.ExcelHelper;
import com.hqh.quizserver.mapper.UserMarkMapper;
import com.hqh.quizserver.repositories.TestQuizzRepository;
import com.hqh.quizserver.repositories.UserAnswerRepository;
import com.hqh.quizserver.repositories.UserMarkRepository;
import com.hqh.quizserver.services.UserMarkHelperService;
import com.hqh.quizserver.services.UserMarkService;
import com.hqh.quizserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMarkServiceImpl implements UserMarkService, UserMarkHelperService {

    private final UserMarkRepository userMarkRepository;
    private final TestQuizzRepository quizzRepository;
    private final UserMarkMapper userMarkMapper;
    private final UserService userService;
    private final UserAnswerRepository userAnswerRepository;

    @Autowired
    public UserMarkServiceImpl(UserMarkRepository userMarkRepository,
                               TestQuizzRepository quizzRepository,
                               UserMarkMapper userMarkMapper,
                               UserService userService,
                               UserAnswerRepository userAnswerRepository) {
        this.userMarkRepository = userMarkRepository;
        this.quizzRepository = quizzRepository;
        this.userMarkMapper = userMarkMapper;
        this.userService = userService;
        this.userAnswerRepository = userAnswerRepository;
    }

    /***
     *
     * @param userMarkDto
     */
    @Override
    public void saveUserMark(UserMarkDTO userMarkDto) {
        TestQuizz quizzId = quizzRepository.findTestQuizzById(userMarkDto.getQuizzId());
        User userId = userService.getCurrentUser();
        UserMark userMark = userMarkMapper
                .map(userMarkDto, quizzId, userId);
        userMark.setCompletedDate(Instant.now());
        userMark.setMark(userAnswerRepository.totalMarkByQuizzId(userMarkDto.getQuizzId()));
        userMark.setPointLock(false);

        userMarkRepository.save(userMark);
    }

    @Override
    public List<UserMarkDTO> getAllUserByUsername(String username) {
        return userMarkRepository.findByAllUsername(username)
                                 .stream()
                                 .map(userMarkMapper::mapToDto)
                                 .collect(Collectors.toList());
    }

    @Override
    public List<UserMarkDTO> getAllUserByQuizzId(Long quizzId) {
        return userMarkRepository.findByTestQuizzId(quizzId)
                                 .stream()
                                 .map(userMarkMapper::mapToDto)
                                 .collect(Collectors.toList());
    }

    /***
     * get the top 3 users with the highest score according to quiz id
     *
     * @param quizzId
     * @return list
     */
    @Override
    public List<UserMarkDTO> getMarkTop3(Long quizzId) {
        return userMarkRepository.getMarkTop3(quizzId)
                                 .stream()
                                 .limit(3)
                                 .map(userMarkMapper::mapToDto)
                                 .collect(Collectors.toList());
    }

    /***
     * export mark by quiz id
     *
     * @param id
     * @return
     */
    @Override
    public ByteArrayInputStream loadUserMarkExcel(long id) {
        List<UserMark> userMark = userMarkRepository.findByTestQuizzId(id);
        return ExcelHelper.userMarkToExcel(userMark);
    }

    @Override
    @Transactional
    public void pointLock(Long userId, boolean isLock) {
        userMarkRepository.markLock(userId, isLock);
    }
}
