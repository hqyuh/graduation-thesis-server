package com.hqh.quizserver.controller;

import com.hqh.quizserver.domain.HttpResponse;
import com.hqh.quizserver.domain.User;
import com.hqh.quizserver.domain.UserPrincipal;
import com.hqh.quizserver.exception.domain.user.EmailExistException;
import com.hqh.quizserver.exception.domain.user.EmailNotFoundException;
import com.hqh.quizserver.exception.domain.user.UserNotFoundException;
import com.hqh.quizserver.exception.domain.user.UsernameExistException;
import com.hqh.quizserver.service.UserService;
import com.hqh.quizserver.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

import static com.hqh.quizserver.constant.DomainConstant.SIGN_UP_SUCCESS;
import static com.hqh.quizserver.constant.EmailConstant.EMAIL_SENT;
import static com.hqh.quizserver.constant.MessageTypeConstant.SUCCESS;
import static com.hqh.quizserver.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static com.hqh.quizserver.utils.ResponseUtils.response;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/auth"})
public class AuthController {

    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService,
                          JWTTokenProvider jwtTokenProvider,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@Valid @RequestBody User user)
            throws UserNotFoundException, EmailExistException, UsernameExistException {
        userService.register(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.getPassword()
        );
        return response(OK, SUCCESS, SIGN_UP_SUCCESS);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
            throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(OK, SUCCESS,EMAIL_SENT + email);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {

        // authenticate -> get username and password for authentication
        authenticate(user.getEmail(), user.getPassword());
        User loginUser = userService.findUserByEmail(user.getEmail());
        // provide user for UserPrincipal
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);

        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String email, String password) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

}
