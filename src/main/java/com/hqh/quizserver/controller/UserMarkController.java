package com.hqh.quizserver.controller;

import com.hqh.quizserver.dto.UserMarkDTO;
import com.hqh.quizserver.entity.ApiResponse;
import com.hqh.quizserver.services.UserMarkHelperService;
import com.hqh.quizserver.services.UserMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hqh.quizserver.constant.FileConstant.*;
import static com.hqh.quizserver.constant.FileConstant.APPLICATION_EXCEL;
import static com.hqh.quizserver.constant.MessageTypeConstant.MESSAGE_SUCCESS;
import static com.hqh.quizserver.constant.UserMarkImplConstant.SUCCESSFUL_LOCKED_POINTS;
import static com.hqh.quizserver.constant.UserMarkImplConstant.UNLOCK_SUCCESS_POINTS;
import static com.hqh.quizserver.utils.ResponseUtils.response;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = { "/user-mark" })
public class UserMarkController {

    private final UserMarkService userMarkService;
    private final UserMarkHelperService helperService;

    @Autowired
    public UserMarkController(UserMarkService userMarkService,
                              UserMarkHelperService helperService) {
        this.userMarkService = userMarkService;
        this.helperService = helperService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserMarkDTO>> getAllMarkByUsername(@RequestParam("username") String username) {
        return ResponseEntity.status(OK).body(userMarkService.getAllUserByUsername(username));
    }

    @GetMapping("/quizz")
    public ResponseEntity<List<UserMarkDTO>> getAllMarkByQuizzId(@RequestParam("quizzId") Long quizzId) {
        return ResponseEntity.status(OK).body(userMarkService.getAllUserByQuizzId(quizzId));
    }

    @GetMapping("/quizz/top")
    public ResponseEntity<List<UserMarkDTO>> getMarkTop3(@RequestParam("quizzId") Long quizzId) {
        return ResponseEntity.status(OK).body(userMarkService.getMarkTop3(quizzId));
    }

    @GetMapping("/export/excel/{id}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long id) {
        String fileName = "mark" + DOT + XLSX_EXTENSION;
        InputStreamResource file = new InputStreamResource(helperService.loadUserMarkExcel(id));
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName)
                             .contentType(MediaType.parseMediaType(APPLICATION_EXCEL))
                             .body(file);
    }

    @GetMapping("/{id}/locked/{isLock}")
    public ResponseEntity<ApiResponse> userPointLock(@PathVariable("id") Long id,
                                                     @PathVariable("isLock") String isLock) {
        boolean isStatus = Boolean.parseBoolean(isLock);
        userMarkService.pointLock(id, isStatus);
        String message = isStatus ? SUCCESSFUL_LOCKED_POINTS : UNLOCK_SUCCESS_POINTS;
        return response(OK, MESSAGE_SUCCESS, message);
    }

}
