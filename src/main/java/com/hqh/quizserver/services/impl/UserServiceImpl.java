package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.UserDTO;
import com.hqh.quizserver.entities.User;
import com.hqh.quizserver.entities.UserPrincipal;
import com.hqh.quizserver.entities.UserStatistics;
import com.hqh.quizserver.enumeration.Role;
import com.hqh.quizserver.exceptions.domain.user.*;
import com.hqh.quizserver.helper.user.CSVHelper;
import com.hqh.quizserver.mapper.UserMapper;
import com.hqh.quizserver.repositories.UserRepository;
import com.hqh.quizserver.services.UserHelperService;
import com.hqh.quizserver.services.EmailService2;
import com.hqh.quizserver.services.LoginAttemptService;
import com.hqh.quizserver.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqh.quizserver.constant.EmailConstant.*;
import static com.hqh.quizserver.constant.FileConstant.*;
import static com.hqh.quizserver.constant.PasswordConstant.CURRENT_PASSWORD_IS_INCORRECT;
import static com.hqh.quizserver.constant.UserImplConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserDetailsService, UserService, UserHelperService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final EmailService2 emailService2;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService2 emailService2,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService2 = emailService2;
        this.userMapper = userMapper;
    }

    /**
     * This function will check the user is in the database,
     * if successful it will return the userPrincipal to Spring Security
     * */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if(user == null) {
            log.error("No user found by email: {}", email);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        } else {
            //
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLogin());
            user.setLastLogin(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);

            log.info("Returning found user by email: {}", email);

            return userPrincipal;
        }
    }

    /***
     * isNotLocked() -> function that checks if there is a lock
     * If the account is not locked, check the number of logins
     *
     * @param user object user
     */
    private void validateLoginAttempt(User user) {
        if(user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getEmail()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
        }
    }



    /**
     * <h2>It takes in a bunch of parameters, validates them, and then saves the user to the database</h2>
     *
     * @param firstName The first name of the user.
     * @param lastName "Doe"
     * @param username The username of the user.
     * @param email The email address of the user.
     * @param role The role of the user.
     * @param password The password that the user will use to login.
     * @return User
     */
    @Override
    public User register(String firstName, String lastName, String username, String email, String role, String password)
            throws UserNotFoundException, EmailExistException, UsernameExistException {

        log.info("Begin validating email and username :::");
        validateNewUsernameAndEmail(EMPTY, username, email);
        log.info("End validating email and username :::");
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRoles(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setCreatedBy("Self-registered users");
        user.setUpdatedBy(null);
        userRepository.save(user);

        return user;
    }

    private String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String getTemporaryProfileImageUrl(String username) {
        // http://localhost:8081
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                          .path(DEFAULT_USER_IMAGE_PATH + username)
                                          .toUriString();
    }


    /**
     * <h2>It checks if the new username and email are valid and if they are, it returns the current user</h2>
     *
     * @param currentUsername the username of the user who is currently logged in.
     * @param newUsername the new username that the user wants to change to
     * @param newEmail the new email address that the user wants to change to
     * @return The current user is being returned.
     */
    private User validateNewUsernameAndEmail(String currentUsername,
                                             String newUsername,
                                             String newEmail)
            throws UsernameExistException, EmailExistException, UserNotFoundException {

        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);

        // check if not null
        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findUserByUsername(currentUsername);

            if (currentUser == null) {
                log.error("No user found by username {}", currentUsername);
                throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            // if the user's new name is not null and exists in the database
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                log.error("Username already exists");
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            // if the user's email is not empty and exists in the database
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                log.error("Email already exists");
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                log.error("Username already exists");
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                log.error("Email already exists");
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<UserDTO> getUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::userMapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    /***
     * create Random password with 8 character
     *
     * @return password
     */
    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = userRepository.findUserByEmail(email);

        if(user == null) {
            throw new EmailNotFoundException(NO_USER_FOUND_BY_USERNAME + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        log.info("Reset password {}", password);
        userRepository.save(user);
        String name = user.getFirstName();
        emailService2.sendNewPasswordEmail(name, password, email, EMAIL_SUBJECT_RESET);
        log.info("An email with a new password was sent to: {}", email);
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    /***
     *
     * @param firstName
     * @param lastName
     * @param username
     * @param email
     * @param role
     * @param isNonLocked
     * @param isActive
     * @param multipartFile
     * @return new user
     * @throws UserNotFoundException
     * @throws EmailExistException
     * @throws UsernameExistException
     * @throws IOException
     * @throws NotAnImageFileException
     * @throws MessagingException
     */
    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role,
                           boolean isNonLocked, boolean isActive, MultipartFile multipartFile)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException,
            NotAnImageFileException {

        log.info("Begin validating email and username :::");
        validateNewUsernameAndEmail(EMPTY, username, email);
        log.info("End validating email and username :::");

        // new user
        User user = new User();
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRoles(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setCreatedBy(getCurrentUser().getUsername());
        user.setUpdatedBy(getCurrentUser().getUsername());
        saveProfileImage(user, multipartFile);
        // String name = user.getFirstName();
        // log.info("Password has been sent to email: {}", email);
        // emailService2.sendNewPasswordEmail(name, password, email, EMAIL_SUBJECT_NEW_USER);
        userRepository.save(user);

        return user;
    }

    /***
     *
     * @param user
     * @param profileImage
     * @throws IOException
     * @throws NotAnImageFileException
     */
    private void saveProfileImage(User user, MultipartFile profileImage)
            throws IOException, NotAnImageFileException {

        if(!profileImage.isEmpty()) {

            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_GIF_VALUE, IMAGE_PNG_VALUE)
                      .contains(profileImage.getContentType())) {
                throw new NotAnImageFileException(profileImage.getOriginalFilename() + PLEASE_UPLOAD_AN_IMAGE);
            }

            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info("Created directory for: {}", userFolder);
            }

            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));

            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            log.info("Saved file in file system by name: {}", profileImage.getOriginalFilename());
        }

    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION)
                .toUriString();
    }

    /***
     *
     * @param currentUsername
     * @param newFirstName
     * @param newLastName
     * @param newUsername
     * @param newEmail
     * @param role
     * @param isNonLocked
     * @param isActive
     * @param profileImage
     * @return
     * @throws UserNotFoundException
     * @throws EmailExistException
     * @throws UsernameExistException
     * @throws IOException
     * @throws NotAnImageFileException
     */
    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
                           String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {

        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        if (currentUser != null) {
            currentUser.setFirstName(newFirstName);
            currentUser.setLastName(newLastName);
            currentUser.setEmail(newEmail);
            currentUser.setUsername(newUsername);
            currentUser.setActive(isActive);
            currentUser.setNotLocked(isNonLocked);
            currentUser.setRoles(getRoleEnumName(role).name());
            currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
            currentUser.setCreatedAt(new Date());
            currentUser.setUpdatedAt(new Date());
            currentUser.setCreatedBy(getCurrentUser().getUsername());
            currentUser.setUpdatedBy(getCurrentUser().getUsername());
            userRepository.save(currentUser);
            saveProfileImage(currentUser, profileImage);
            log.info("Update user successfully");
        }
        return currentUser;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }


    /**
     * It takes a user and an old password, and returns true if the old password matches the user's password
     *
     * @param user The user object that you want to check the password for.
     * @param oldPassword The password that the user entered in the form
     * @return A boolean value.
     */
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return bCryptPasswordEncoder.matches(oldPassword, user.getPassword());
    }

    /**
     * @param email The email address of the user who wants to change their password.
     * @param oldPassword The password that the user entered in the form.
     * @param newPassword The new password that the user wants to change to.
     */
    public void changePassword(String email, String oldPassword, String newPassword) throws PasswordException {
        User user = userRepository.findUserByEmail(email);

        if(!checkIfValidOldPassword(user, oldPassword)) {
            log.error("Current password is incorrect.");
            throw new PasswordException(CURRENT_PASSWORD_IS_INCORRECT);
        }
        user.setPassword(encodePassword(newPassword));
        log.info("Change password successfully.");
    }

    @Override
    public void accountLock(Long id, boolean isNotLocked) {
        userRepository.accountLock(id, isNotLocked);
    }

    @Override
    public ByteArrayInputStream loadCSV() {
        List<User> users = userRepository.findAll();

        return CSVHelper.userToCsv(users);
    }

    @Override
    public User getCurrentUser() {
        String userPrincipal = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserByUsername(userPrincipal);
    }

    @Override
    public UserStatistics userStatistics() {
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setNumberOfUsersInUse(userRepository.userStatistics());
        return userStatistics;
    }
}
