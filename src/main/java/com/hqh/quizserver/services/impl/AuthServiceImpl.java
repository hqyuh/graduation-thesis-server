package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.UserLoginRequestDTO;
import com.hqh.quizserver.dto.UserLoginResponseDTO;
import com.hqh.quizserver.entity.User;
import com.hqh.quizserver.entity.UserPrincipal;
import com.hqh.quizserver.services.AuthService;
import com.hqh.quizserver.services.UserService;
import com.hqh.quizserver.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.hqh.quizserver.constant.SecurityConstant.EXPIRATION_TIME;
import static com.hqh.quizserver.constant.SecurityConstant.TOKEN_PREFIX;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(UserService userService,
                           JWTTokenProvider jwtTokenProvider,
                           AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO) {
        String email = userLoginRequestDTO.getEmail();
        String password = userLoginRequestDTO.getPassword();
        // authenticate -> get username and password for authentication
        authenticate(email, password);

        User loginUser = userService.findUserByEmail(userLoginRequestDTO.getEmail());
        // provide user for UserPrincipal
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        // create jwt
        String token = jwtTokenProvider.generateJwtToken(userPrincipal);

        return UserLoginResponseDTO
                .builder()
                .tokenType(TOKEN_PREFIX)
                .accessToken(token)
                .expireAt(Instant.now().plusMillis(EXPIRATION_TIME))
                .build();
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

}
