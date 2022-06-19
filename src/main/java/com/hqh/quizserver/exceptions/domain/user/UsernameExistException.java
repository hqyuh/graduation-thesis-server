package com.hqh.quizserver.exceptions.domain.user;

public class UsernameExistException extends Exception {
    public UsernameExistException(String message) {
        super(message);
    }
}
