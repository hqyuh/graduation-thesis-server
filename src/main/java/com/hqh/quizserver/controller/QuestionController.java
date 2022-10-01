package com.hqh.quizserver.controller;

import com.hqh.quizserver.entity.ApiResponse;
import com.hqh.quizserver.dto.QuestionDTO;
import com.hqh.quizserver.exceptions.domain.user.NotAnImageFileException;
import com.hqh.quizserver.helper.quizz.ExcelHelper;
import com.hqh.quizserver.services.QuestionHelperService;
import com.hqh.quizserver.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.hqh.quizserver.constant.FileConstant.*;
import static com.hqh.quizserver.constant.MessageTypeConstant.MESSAGE_ERROR;
import static com.hqh.quizserver.constant.MessageTypeConstant.MESSAGE_SUCCESS;
import static com.hqh.quizserver.constant.QuestionImplConstant.*;
import static com.hqh.quizserver.utils.ResponseUtils.response;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = { "/question" })
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionHelperService helperService;

    @Autowired
    public QuestionController(QuestionService questionService,
                              QuestionHelperService helperService) {
        this.questionService = questionService;
        this.helperService = helperService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createQuestion(@RequestParam("topicQuestion") String topicQuestion,
                                                      @RequestParam(value = "questionImageUrl", required = false)
                                                               MultipartFile questionImageUrl,
                                                      @RequestParam("answerA") String answerA,
                                                      @RequestParam("answerB") String answerB,
                                                      @RequestParam("answerC") String answerC,
                                                      @RequestParam("answerD") String answerD,
                                                      @RequestParam("correctResult") String correctResult,
                                                      @RequestParam("correctEssay") String correctEssay,
                                                      @RequestParam("type") String type,
                                                      @RequestParam("mark") String mark,
                                                      @RequestParam("quizzId") String quizzId,
                                                      @RequestParam("level") String level)
            throws IOException, NotAnImageFileException {
        questionService.createQuestion(
                topicQuestion, questionImageUrl,
                answerA, answerB, answerC, answerD,
                correctResult, correctEssay, type,
                Double.parseDouble(mark), Long.parseLong(quizzId), level
        );

        return response(CREATED, MESSAGE_SUCCESS, ADD_SUCCESS_QUESTION);
    }


    @PatchMapping("/update")
    public ResponseEntity<ApiResponse> updateQuestion(@RequestParam("id") String id,
                                                      @RequestParam("topicQuestion") String topicQuestion,
                                                      @RequestParam("answerA") String answerA,
                                                      @RequestParam("answerB") String answerB,
                                                      @RequestParam("answerC") String answerC,
                                                      @RequestParam("answerD") String answerD,
                                                      @RequestParam("correctResult") String correctResult,
                                                      @RequestParam("correctEssay") String correctEssay,
                                                      @RequestParam("type") String type,
                                                      @RequestParam("mark") String mark,
                                                      @RequestParam("quizzId") String quizzId,
                                                      @RequestParam(value = "questionImageUrl", required = false)
                                                                   MultipartFile questionImageUrl,
                                                      @RequestParam("level") String level)
            throws IOException, NotAnImageFileException {

        questionService.updateQuestion(
                Long.parseLong(id), topicQuestion,
                answerA, answerB,
                answerC, answerD,
                correctResult, correctEssay, type,
                Double.parseDouble(mark), Long.parseLong(quizzId),
                questionImageUrl, level
        );

        return response(OK, MESSAGE_SUCCESS, QUESTION_UPDATE_SUCCESSFUL);
    }

    @GetMapping("/list/{pageNum}")
    public ResponseEntity<List<QuestionDTO>> getAllQuestion(@PathVariable("pageNum") Integer pageNum) {
        return ResponseEntity
                .status(OK)
                .body(questionService.getAllQuestion(pageNum));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteQuestion(@PathVariable("id") Long id) {
        questionService.deleteQuestion(id);

        return response(OK, MESSAGE_SUCCESS, QUESTION_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/import/{id}")
    public ResponseEntity<ApiResponse> importFileExcel(@RequestParam("file") MultipartFile multipartFile,
                                                       @PathVariable("id") Long id) {
        if(ExcelHelper.hasExcelFormat(multipartFile)) {
            try {
                helperService.saveFile(multipartFile, id);
                return response(OK, MESSAGE_SUCCESS,
                        UPLOADED_THE_FILE_SUCCESSFULLY + multipartFile.getOriginalFilename());
            } catch (Exception exception) {
                return response(BAD_REQUEST, MESSAGE_ERROR,
                        COULD_NOT_UPLOAD_THE_FILE + multipartFile.getOriginalFilename() + EXCLAMATION_MARK);
            }
        }
        return response(BAD_REQUEST, MESSAGE_ERROR, PLEASE_UPLOAD_AN_EXCEL_FILE);
    }

}
