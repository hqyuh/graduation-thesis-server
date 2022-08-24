package com.hqh.quizserver.utils;

import com.hqh.quizserver.entities.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class ResponseUtils {

    /***
     *
     * @param httpStatus
     * @param type
     * @param message
     * @return response
     */
    public static ResponseEntity<HttpResponse> response(HttpStatus httpStatus,
                                                         String type,
                                                         String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus,
                type.toUpperCase(),
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

}
