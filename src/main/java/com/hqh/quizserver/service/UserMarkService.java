package com.hqh.quizserver.service;

import com.hqh.quizserver.dto.UserMarkDto;

import java.util.List;

public interface UserMarkService {

    void saveUserMark(UserMarkDto userMarkDto);

    List<UserMarkDto> getAllUserByUsername(String username);

    List<UserMarkDto> getAllUserByQuizzId(Long quizzId);

    List<UserMarkDto> getMarkTop3(Long quizzId);

    void pointLock(Long userId, boolean isLock);

}
