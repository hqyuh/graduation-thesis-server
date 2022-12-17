package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.UserLoginRequestDTO;
import com.hqh.quizserver.dto.UserLoginResponseDTO;

public interface AuthService {
    UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO);
}
