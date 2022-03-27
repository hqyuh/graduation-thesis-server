package com.hqh.graduationthesisserver.service;

import com.hqh.graduationthesisserver.domain.User;
import com.hqh.graduationthesisserver.exception.domain.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email, String password)
            throws UserNotFoundException, EmailExistException, UsernameExistException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    void resetPassword(String email) throws EmailNotFoundException, MessagingException;

    User addNewUser(String firstName, String lastName, String username, String email, String role,
                    boolean isNonLocked, boolean isActive, MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException, MessagingException;

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
                    String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    void deleteUser(Long id);

    User updateProfileImage(String username, MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException;

    User findUserById(Long id);

    void changePassword(String email, String oldPassword, String newPassword) throws PasswordException;

    void accountLock(Long id, boolean isNotLocked);

}
