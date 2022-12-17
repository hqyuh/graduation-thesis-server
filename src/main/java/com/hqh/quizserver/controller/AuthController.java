package com.hqh.quizserver.controller;

import com.hqh.quizserver.dto.UserLoginRequestDTO;
import com.hqh.quizserver.dto.UserLoginResponseDTO;
import com.hqh.quizserver.dto.UserRegisterRequestDTO;
import com.hqh.quizserver.entity.ApiResponse;
import com.hqh.quizserver.exceptions.domain.user.EmailExistException;
import com.hqh.quizserver.exceptions.domain.user.EmailNotFoundException;
import com.hqh.quizserver.exceptions.domain.user.UserNotFoundException;
import com.hqh.quizserver.exceptions.domain.user.UsernameExistException;
import com.hqh.quizserver.services.AuthService;
import com.hqh.quizserver.services.UserService;
import com.hqh.quizserver.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

import static com.hqh.quizserver.constant.DomainConstant.SIGN_UP_SUCCESS;
import static com.hqh.quizserver.constant.EmailConstant.EMAIL_SENT;
import static com.hqh.quizserver.constant.MessageTypeConstant.MESSAGE_SUCCESS;
import static com.hqh.quizserver.utils.ResponseUtils.response;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/auth"})
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService,
                          AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegisterRequestDTO userRegisterRequestDTO)
            throws UserNotFoundException, EmailExistException, UsernameExistException {
        userService.register(
                userRegisterRequestDTO.getFirstName(),
                userRegisterRequestDTO.getLastName(),
                userRegisterRequestDTO.getUsername(),
                userRegisterRequestDTO.getEmail(),
                userRegisterRequestDTO.getRoles(),
                userRegisterRequestDTO.getPassword()
        );
        return response(OK, MESSAGE_SUCCESS, SIGN_UP_SUCCESS);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<ApiResponse> resetPassword(@PathVariable("email") String email)
            throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(OK, MESSAGE_SUCCESS,EMAIL_SENT + email);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        return ResponseEntity.status(OK).body(authService.login(userLoginRequestDTO));
    }

}
