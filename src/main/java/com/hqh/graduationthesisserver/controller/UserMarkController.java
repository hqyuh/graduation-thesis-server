package com.hqh.graduationthesisserver.controller;

import com.hqh.graduationthesisserver.domain.HttpResponse;
import com.hqh.graduationthesisserver.dto.UserMarkDto;
import com.hqh.graduationthesisserver.service.UserMarkHelperService;
import com.hqh.graduationthesisserver.service.UserMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hqh.graduationthesisserver.constant.FileConstant.*;
import static com.hqh.graduationthesisserver.constant.FileConstant.APPLICATION_EXCEL;
import static com.hqh.graduationthesisserver.constant.MessageTypeConstant.SUCCESS;
import static com.hqh.graduationthesisserver.constant.UserMarkImplConstant.SUCCESSFUL_LOCKED_POINTS;
import static com.hqh.graduationthesisserver.constant.UserMarkImplConstant.UNLOCK_SUCCESS_POINTS;
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

    @PostMapping("/save")
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

    @GetMapping("/quizz/top/{id}")
    public ResponseEntity<List<UserMarkDto>> getMarkTop3(@PathVariable("id") Long id) {
        return ResponseEntity.status(OK)
                             .body(userMarkService.getMarkTop3(id));
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

    @GetMapping("/{id}/lock/{isLock}")
    public ResponseEntity<HttpResponse> userPointLock(@PathVariable("id") Long id,
                                                      @PathVariable("isLock") String isLock) {
        boolean isStatus = Boolean.parseBoolean(isLock);
        userMarkService.pointLock(id, isStatus);
        String message = isStatus ? SUCCESSFUL_LOCKED_POINTS : UNLOCK_SUCCESS_POINTS;
        return response(OK, SUCCESS, message);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String type, String message){
        HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus, type.toUpperCase(),
                httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

}
