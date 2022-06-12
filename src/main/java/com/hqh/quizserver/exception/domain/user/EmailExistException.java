package com.hqh.quizserver.exception.domain.user;

public class EmailExistException extends Exception {
    public EmailExistException(String message) {
        super(message);
    }
}
