package com.hqh.quizserver.utils;

import com.hqh.quizserver.entity.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {

    /***
     *
     * @param httpStatus
     * @param type
     * @param message
     * @return response
     */
    public static ResponseEntity<ApiResponse> response(HttpStatus httpStatus,
                                                       String type,
                                                       String message) {
        ApiResponse body = new ApiResponse(httpStatus.value(), httpStatus,
                type.toUpperCase(),
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

}
