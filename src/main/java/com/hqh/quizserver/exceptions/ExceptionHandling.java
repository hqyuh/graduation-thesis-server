package com.hqh.quizserver.exceptions;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hqh.quizserver.entities.HttpResponse;
import com.hqh.quizserver.exceptions.domain.quizz.*;
import com.hqh.quizserver.exceptions.domain.topic.TopicExistException;
import com.hqh.quizserver.exceptions.domain.topic.TopicNotFoundException;
import com.hqh.quizserver.exceptions.domain.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.hqh.quizserver.constant.DomainConstant.*;
import static com.hqh.quizserver.constant.MessageTypeConstant.ERROR;
import static org.springframework.http.HttpStatus.*;

// exception api
@RestControllerAdvice
public class ExceptionHandling implements ErrorController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus,
                                                           String type,
                                                           String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),
                httpStatus,
                type.toUpperCase(),
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase()), httpStatus);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return createHttpResponse(BAD_REQUEST, ERROR, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST, ERROR, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, ERROR, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ERROR, ACCOUNT_LOCKER);
    }

    /***
     * token expires
     *
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return createHttpResponse(UNAUTHORIZED, ERROR, exception.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(EmailNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

    /***
     * introduce another method
     *
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods())
                                            .iterator()
                                            .next();
        return createHttpResponse(METHOD_NOT_ALLOWED, ERROR, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, ERROR, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<HttpResponse> methodNotSupportedException(NoHandlerFoundException exception) {
//        return createHttpResponse(BAD_REQUEST, "This page was not found");
//    }

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> notFound404(){
        return createHttpResponse(NOT_FOUND, ERROR, NO_MAPPING_FOR_URL);
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<HttpResponse> passwordException(PasswordException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

    @ExceptionHandler(TestQuizzNotFoundException.class)
    public ResponseEntity<HttpResponse> quizzNotFoundException(TestQuizzNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

    @ExceptionHandler(TestQuizzExistException.class)
    public ResponseEntity<HttpResponse> quizzExistException(TestQuizzExistException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(TopicNotFoundException.class)
    public ResponseEntity<HttpResponse> topicNotFoundException(TopicNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

    @ExceptionHandler(TopicExistException.class)
    public ResponseEntity<HttpResponse> topicExistException(TopicExistException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

    @ExceptionHandler(TestQuizzCreateTimeException.class)
    public ResponseEntity<HttpResponse> timeCreateQuizzException(TestQuizzCreateTimeException exception) {
        return createHttpResponse(BAD_REQUEST, ERROR, exception.getMessage());
    }

}
