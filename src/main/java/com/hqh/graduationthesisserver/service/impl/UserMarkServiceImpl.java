package com.hqh.graduationthesisserver.service.impl;

import com.hqh.graduationthesisserver.domain.TestQuizz;
import com.hqh.graduationthesisserver.domain.User;
import com.hqh.graduationthesisserver.domain.UserMark;
import com.hqh.graduationthesisserver.dto.UserMarkDto;
import com.hqh.graduationthesisserver.mapper.UserMarkMapper;
import com.hqh.graduationthesisserver.repository.TestQuizzRepository;
import com.hqh.graduationthesisserver.repository.UserAnswerRepository;
import com.hqh.graduationthesisserver.repository.UserMarkRepository;
import com.hqh.graduationthesisserver.service.UserMarkService;
import com.hqh.graduationthesisserver.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMarkServiceImpl implements UserMarkService {

    private final UserMarkRepository userMarkRepository;
    private final TestQuizzRepository quizzRepository;
    private final UserMarkMapper userMarkMapper;
    private final UserService userService;
    private final UserAnswerRepository userAnswerRepository;

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

    @Override
    public void saveUserMark(UserMarkDto userMarkDto) {
        TestQuizz quizzId = quizzRepository.findTestQuizzById(userMarkDto.getQuizzId());
        User userId = userService.getCurrentUser();
        UserMark userMark = userMarkMapper
                .map(userMarkDto, quizzId, userId);
        userMark.setCompletedDate(Instant.now());
        userMark.setMark(userAnswerRepository.totalMarkByQuizzId(userMarkDto.getQuizzId()));

        userMarkRepository.save(userMark);
    }

    @Override
    public List<UserMarkDto> getAllUserByUsername(String username) {
        return userMarkRepository.findByAllUsername(username)
                                 .stream()
                                 .map(userMarkMapper::mapToDto)
                                 .collect(Collectors.toList());
    }

    @Override
    public List<UserMarkDto> getAllUserByQuizzId(Long quizzId) {
        return userMarkRepository.findByTestQuizzId(quizzId)
                                 .stream()
                                 .map(userMarkMapper::mapToDto)
                                 .collect(Collectors.toList());
    }

}
