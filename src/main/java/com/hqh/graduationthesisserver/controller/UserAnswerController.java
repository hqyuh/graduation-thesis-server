package com.hqh.graduationthesisserver.controller;

import com.hqh.graduationthesisserver.domain.HttpResponse;
import com.hqh.graduationthesisserver.dto.UserAnswerDto;
import com.hqh.graduationthesisserver.service.UserAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

import static com.hqh.graduationthesisserver.constant.MessageTypeConstant.SUCCESS;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = { "/user-answer" })
public class UserAnswerController {

    public static final String SUCCESSFUL_TEST_SUBMISSION = "Successful test submission";
    private final UserAnswerService userAnswerService;

    @Autowired
    public UserAnswerController(UserAnswerService userAnswerService) {
        this.userAnswerService = userAnswerService;
    }

    @PostMapping("/save-answer")
    public ResponseEntity<HttpResponse> saveAllUserAnswer(@RequestBody List<UserAnswerDto> userAnswerDto) {
        userAnswerService.saveAllUserAnswer(userAnswerDto);
        return response(OK, SUCCESS, SUCCESSFUL_TEST_SUBMISSION);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String type, String message){
        HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus, type.toUpperCase(),
                httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

}
