package com.hqh.quizserver.exceptions.domain.user;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
