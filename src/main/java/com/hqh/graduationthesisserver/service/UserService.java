package com.hqh.graduationthesisserver.service;

import com.hqh.graduationthesisserver.domain.User;
import com.hqh.graduationthesisserver.exception.domain.EmailExistException;
import com.hqh.graduationthesisserver.exception.domain.UserNotFoundException;
import com.hqh.graduationthesisserver.exception.domain.UsernameExistException;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email, String password)
            throws UserNotFoundException, EmailExistException, UsernameExistException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

}
