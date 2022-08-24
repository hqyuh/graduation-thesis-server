package com.hqh.quizserver.controller;

import com.hqh.quizserver.dto.UserDTO;
import com.hqh.quizserver.entities.*;
import com.hqh.quizserver.exceptions.ExceptionHandling;
import com.hqh.quizserver.exceptions.domain.user.*;
import com.hqh.quizserver.services.UserHelperService;
import com.hqh.quizserver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.hqh.quizserver.constant.DomainConstant.USER_DELETED_SUCCESSFULLY;
import static com.hqh.quizserver.constant.FileConstant.*;
import static com.hqh.quizserver.constant.MessageTypeConstant.*;
import static com.hqh.quizserver.constant.PasswordConstant.CHANGE_PASSWORD_SUCCESSFULLY;
import static com.hqh.quizserver.constant.UserImplConstant.*;
import static com.hqh.quizserver.utils.ResponseUtils.response;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserController extends ExceptionHandling {

    private final UserService userService;
    private final UserHelperService helperService;

    @Autowired
    public UserController(UserService userService,
                          UserHelperService helperService) {
        this.userService = userService;
        this.helperService = helperService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> addNewUser(@Valid
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("roles") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, UsernameExistException, IOException,
            NotAnImageFileException, MessagingException {

        User newUser = userService.addNewUser(
                firstName,
                lastName,
                username,
                email,
                role,
                Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive),
                profileImage
        );

        return new ResponseEntity<>(newUser, CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@Valid
                                           @RequestParam("currentUsername") String currentUsername,
                                           @RequestParam("firstName") String firstName,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String username,
                                           @RequestParam("email") String email,
                                           @RequestParam("roles") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException,
            NotAnImageFileException {
        User updateUser = userService.updateUser(currentUsername, firstName, lastName, username, email, role,
                Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);

        return new ResponseEntity<>(updateUser, OK);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = userService.findUserById(id);

        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getUsers();

        return new ResponseEntity<>(users, OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);

        return response(OK, SUCCESS, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/update-profile-image")
    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username,
                                                   @RequestParam(value = "profileImage") MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, IOException,
            UsernameExistException, NotAnImageFileException {
        User user = userService.updateProfileImage(username, profileImage);

        return new ResponseEntity<>(user, OK);
    }

    @GetMapping(path = "/{username}/{fileName}", produces = { IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE })
    public byte[] getProfileImage(@PathVariable("username") String username,
                                  @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/profile/{username}", produces = { IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE })
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username + DOT + PNG_EXTENSION);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()){
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0){
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody Password password)
            throws PasswordException {
        userService.changePassword(
                password.getEmail(),
                password.getOldPassword(),
                password.getNewPassword()
        );
        return response(OK, SUCCESS, CHANGE_PASSWORD_SUCCESSFULLY);
    }

    @GetMapping("/{id}/locked/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> accountLock(@PathVariable("id") Long id,
                                         @PathVariable("status") String status) {
        userService.accountLock(id, Boolean.parseBoolean(status));
        String message = Boolean.parseBoolean(status) ? ACCOUNT_UNLOCK_SUCCESSFUL : ACCOUNT_LOCK_SUCCESSFUL;

        return response(OK, SUCCESS, message);
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Resource> exportCSV() {
        String fileName = "users" + DOT + CSV_EXTENSION;
        InputStreamResource file = new InputStreamResource(helperService.loadCSV());
        return ResponseEntity
                .ok()
                .header(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName)
                .contentType(MediaType.parseMediaType(APPLICATION_CSV))
                .body(file);
    }

//    @GetMapping("/my")
//    public ResponseEntity<UserDTO> currentUsername() {
//        return ResponseEntity
//                .status(OK)
//                .body(userService.getCurrentUser());
//    }

    @GetMapping("/statistic")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserStatistics> userStatistics() {
        return ResponseEntity
                .status(OK)
                .body(userService.userStatistics());
    }

}
