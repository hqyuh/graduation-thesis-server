package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.UserMarkDTO;

import java.util.List;

public interface UserMarkService {

    void saveUserMark(UserMarkDTO userMarkDto);

    List<UserMarkDTO> getAllUserByUsername(String username);

    List<UserMarkDTO> getAllUserByQuizzId(Long quizzId);

    List<UserMarkDTO> getMarkTop3(Long quizzId);

    void pointLock(Long userId, boolean isLock);

}
