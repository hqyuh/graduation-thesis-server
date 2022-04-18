package com.hqh.graduationthesisserver.controller;

import com.hqh.graduationthesisserver.dto.UserMarkDto;
import com.hqh.graduationthesisserver.service.UserMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = { "/user-mark" })
public class UserMarkController {

    private final UserMarkService userMarkService;

    @Autowired
    public UserMarkController(UserMarkService userMarkService) {
        this.userMarkService = userMarkService;
    }

    @PostMapping
    public ResponseEntity<UserMarkDto> saveUserMark(@RequestBody UserMarkDto userMarkDto) {
        userMarkService.saveUserMark(userMarkDto);

        return new ResponseEntity<>(OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<UserMarkDto>> getAllMarkByUsername(@PathVariable("username") String username) {
        return ResponseEntity.status(OK)
                             .body(userMarkService.getAllUserByUsername(username));
    }

    @GetMapping("/quizz/{id}")
    public ResponseEntity<List<UserMarkDto>> getAllMarkByQuizzId(@PathVariable("id") Long id) {
        return ResponseEntity.status(OK)
                             .body(userMarkService.getAllUserByQuizzId(id));
    }

}
