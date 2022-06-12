package com.hqh.quizserver.service.impl;

import com.hqh.quizserver.domain.User;
import com.hqh.quizserver.domain.UserPrincipal;
import com.hqh.quizserver.domain.UserStatistics;
import com.hqh.quizserver.enumeration.Role;
import com.hqh.quizserver.exception.domain.user.*;
import com.hqh.quizserver.helper.user.CSVHelper;
import com.hqh.quizserver.repository.UserRepository;
import com.hqh.quizserver.service.UserHelperService;
import com.hqh.quizserver.service.EmailService2;
import com.hqh.quizserver.service.LoginAttemptService;
import com.hqh.quizserver.service.UserService;
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

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final EmailService2 emailService2;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           LoginAttemptService loginAttemptService,
                           EmailService2 emailService2) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService2 = emailService2;
    }

    /**
     * This function will check the user is in the database,
     * if successful it will return the userPrincipal to Spring Security
     * */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if(user == null) {
            LOGGER.error(NO_USER_FOUND_BY_EMAIL + email);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        } else {
            //
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLogin());
            user.setLastLogin(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_EMAIL + email);

            return userPrincipal;
        }
    }

    /***
     * isNotLocked() -> function that checks if there is a lock
     * If the account is not locked, check the number of logins
     *
     * @param user
     */
    private void validateLoginAttempt(User user) {
        if(user.isNotLocked()) {
            user.setNotLocked(!loginAttemptService.hasExceededMaxAttempts(user.getEmail()));
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
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
                         String role,
                         String password)
            throws UserNotFoundException, EmailExistException, UsernameExistException {

        validateNewUsernameAndEmail(EMPTY, username, email);

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
        userRepository.updateUserStatistics();
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

    /***
     *
     * check if username or email exists
     *
     * @param currentUsername
     * @param newUsername
     * @param newEmail
     * @throws UsernameExistException
     * @throws EmailExistException
     * @return
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
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
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
        LOGGER.info(RESET_PASSWORD + password);
        userRepository.save(user);
        String name = user.getFirstName();
        emailService2.sendNewPasswordEmail(name, password, email, EMAIL_SUBJECT_RESET);
        LOGGER.info(EMAIL_SENT + email);
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
    public User addNewUser(String firstName,
                           String lastName,
                           String username,
                           String email,
                           String role,
                           boolean isNonLocked,
                           boolean isActive,
                           MultipartFile multipartFile)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException,
            NotAnImageFileException {
        validateNewUsernameAndEmail(EMPTY, username, email);
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
        saveProfileImage(user, multipartFile);
        System.out.println(password);
        // String name = user.getFirstName();
        // emailService2.sendNewPasswordEmail(name, password, email, EMAIL_SUBJECT_NEW_USER);
        userRepository.updateUserStatistics();
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
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }

            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));

            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
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
    public User updateUser(String currentUsername,
                           String newFirstName,
                           String newLastName,
                           String newUsername,
                           String newEmail,
                           String role,
                           boolean isNonLocked,
                           boolean isActive,
                           MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException,
            IOException, NotAnImageFileException {
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
            userRepository.save(currentUser);
            saveProfileImage(currentUser, profileImage);
        }

        return currentUser;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateProfileImage(String username,
                                   MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException,
            IOException, NotAnImageFileException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    /***
     * check if the old password is correct
     *
     * @param user
     * @param oldPassword
     * @return <code>true</code>  if the password is correct
     *         <code>false</code> otherwise.
     */
    public boolean checkIfValidOldPassword(User user,
                                           String oldPassword) {
        return bCryptPasswordEncoder.matches(oldPassword, user.getPassword());
    }

    /***
     *
     * @param email
     * @param oldPassword
     * @param newPassword
     * @throws PasswordException
     */
    public void changePassword(String email,
                               String oldPassword,
                               String newPassword) throws PasswordException {
        User user = userRepository.findUserByEmail(email);

        if(!checkIfValidOldPassword(user, oldPassword)) {
            throw new PasswordException(CURRENT_PASSWORD_IS_INCORRECT);
        }
        user.setPassword(encodePassword(newPassword));
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
