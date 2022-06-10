package com.hqh.graduationthesisserver.controller;

import com.hqh.graduationthesisserver.domain.HttpResponse;
import com.hqh.graduationthesisserver.dto.ReviewAnswerDto;
import com.hqh.graduationthesisserver.dto.UserAnswerDto;
import com.hqh.graduationthesisserver.service.UserAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static com.hqh.graduationthesisserver.constant.MessageTypeConstant.SUCCESS;
import static com.hqh.graduationthesisserver.utils.ResponseUtils.response;
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

    @GetMapping("/review-answer/quiz/{quizzId}/user/{userId}")
    public ResponseEntity<List<ReviewAnswerDto>> reviewAnswerUser(@PathVariable("quizzId") Long quizzId,
                                                                  @PathVariable("userId") Long userId) {
        return ResponseEntity
                .status(OK)
                .body(userAnswerService.reviewAnswerUser(quizzId, userId));
    }


}
