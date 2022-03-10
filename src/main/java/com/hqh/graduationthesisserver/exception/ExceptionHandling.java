package com.hqh.graduationthesisserver.exception;

import com.hqh.graduationthesisserver.domain.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.hqh.graduationthesisserver.constant.DomainConstant.*;
import static org.springframework.http.HttpStatus.*;

// exception api
@RestControllerAdvice
public class ExceptionHandling {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus,
                                                           String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),
                                        httpStatus,
                                        httpStatus.getReasonPhrase().toUpperCase(),
                                        message.toUpperCase()), httpStatus);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException() {
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

}
