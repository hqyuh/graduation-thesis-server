package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.UserMarkDTO;
import com.hqh.quizserver.entity.UserMark;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMarkServiceImpl implements UserMarkService, UserMarkHelperService {

    private final UserMarkRepository userMarkRepository;
    private final UserMarkMapper userMarkMapper;

    @Autowired
    public UserMarkServiceImpl(UserMarkRepository userMarkRepository,
                               TestQuizzRepository quizzRepository,
                               UserMarkMapper userMarkMapper,
                               UserService userService,
                               UserAnswerRepository userAnswerRepository) {
        this.userMarkRepository = userMarkRepository;
        this.userMarkMapper = userMarkMapper;
    }

    @Override
    public List<UserMarkDTO> getAllUserByUsername(String username) {
        return userMarkRepository.findByAllUsername(username)
                                 .stream()
                                 .map(userMarkMapper::convertUserMarkToDTO)
                                 .collect(Collectors.toList());
    }

    @Override
    public List<UserMarkDTO> getAllUserByQuizzId(Long quizzId) {
        return userMarkRepository.findByTestQuizzId(quizzId)
                                 .stream()
                                 .map(userMarkMapper::convertUserMarkToDTO)
                                 .collect(Collectors.toList());
    }

    @Override
    public List<UserMarkDTO> getMarkTop3(Long quizzId) {
        return userMarkRepository.getMarkTop3(quizzId)
                                 .stream()
                                 .map(userMarkMapper::convertUserMarkToDTO)
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
