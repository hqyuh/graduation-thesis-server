package com.hqh.quizserver.controller;

import com.hqh.quizserver.dto.UserAnswerRequestDTO;
import com.hqh.quizserver.dto.UserTestQuizzDTO;
import com.hqh.quizserver.entity.ApiResponse;
import com.hqh.quizserver.services.UserAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static com.hqh.quizserver.constant.MessageTypeConstant.MESSAGE_SUCCESS;
import static com.hqh.quizserver.utils.ResponseUtils.response;
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
    public ResponseEntity<ApiResponse> saveAllUserAnswer(@RequestBody(required = false) UserAnswerRequestDTO userAnswerRequestDTO) {
        userAnswerService.saveAllUserAnswer(userAnswerRequestDTO);
        return response(OK, MESSAGE_SUCCESS, SUCCESSFUL_TEST_SUBMISSION);
    }

    @GetMapping("/review-answer/quizz")
    public ResponseEntity<UserTestQuizzDTO> reviewAnswerUser(@RequestParam Long quizzId,
                                                             @RequestParam Long userId) {
        return ResponseEntity.status(OK).body(userAnswerService.reviewAnswerUser(quizzId, userId));
    }


}
