package com.hqh.graduationthesisserver.service.impl;

import com.hqh.graduationthesisserver.domain.User;
import com.hqh.graduationthesisserver.domain.UserPrincipal;
import com.hqh.graduationthesisserver.exception.domain.EmailExistException;
import com.hqh.graduationthesisserver.exception.domain.UserNotFoundException;
import com.hqh.graduationthesisserver.exception.domain.UsernameExistException;
import com.hqh.graduationthesisserver.repository.UserRepository;
import com.hqh.graduationthesisserver.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static com.hqh.graduationthesisserver.constant.Authority.USER_AUTHORITIES;
import static com.hqh.graduationthesisserver.constant.UserImplConstant.*;
import static com.hqh.graduationthesisserver.enumeration.Role.ROLE_USER;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserDetailsService, UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * This function will check the user is in the database,
     * if successful it will return the userPrincipal to Spring Security
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if(user == null) {
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            //
            user.setLastLoginDateDisplay(user.getLastLoginDateDisplay());
            user.setLastLogin(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + username);

            return userPrincipal;
        }
    }

    /***
     *
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param password
     * @return
     * @throws UserNotFoundException
     * @throws EmailExistException
     * @throws UsernameExistException
     */
    @Override
    public User register(String firstName,
                         String lastName,
                         String username,
                         String email,
                         String password)
            throws UserNotFoundException, EmailExistException, UsernameExistException {


        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);

        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRoles(ROLE_USER.name());
        user.setAuthorities(USER_AUTHORITIES);
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        userRepository.save(user);

        return user;
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                          .path(DEFAULT_USER_IMAGE_PATH)
                                          .toUriString();
    }

    /***
     *
     * check if username or email exists
     *
     * @param currentUsername
     * @param newUsername
     * @param newEmail
     * @throws UsernameExistException
     * @throws EmailExistException
     */
    private void validateNewUsernameAndEmail(String currentUsername,
                                             String newUsername,
                                             String newEmail)
            throws UsernameExistException, EmailExistException, UserNotFoundException {

        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);

        // check if not null
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);

            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            // if the user's new name is not null and exists in the database
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            // if the user's email is not empty and exists in the database
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
