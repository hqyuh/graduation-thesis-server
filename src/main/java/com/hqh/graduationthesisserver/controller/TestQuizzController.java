package com.hqh.graduationthesisserver.controller;

import com.hqh.graduationthesisserver.domain.HttpResponse;
import com.hqh.graduationthesisserver.domain.TestQuizz;
import com.hqh.graduationthesisserver.exception.ExceptionHandling;
import com.hqh.graduationthesisserver.exception.domain.quizz.TestQuizzExistException;
import com.hqh.graduationthesisserver.exception.domain.quizz.TestQuizzNotFoundException;
import com.hqh.graduationthesisserver.service.TestQuizzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.hqh.graduationthesisserver.constant.TestQuizzConstant.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = { "/quizz" })
public class TestQuizzController extends ExceptionHandling {

    private final TestQuizzService testQuizzService;

    @Autowired
    public TestQuizzController(TestQuizzService testQuizzService) {
        this.testQuizzService = testQuizzService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public ResponseEntity<HttpResponse> addNewTestQuizz(@RequestParam("testName") String testName,
                                                        @RequestParam("examTime") Integer examTime,
                                                        @RequestParam("isStart") String isStart,
                                                        @RequestParam("isEnd") String isEnd,
                                                        @RequestParam(name = "topicId", required = false) String topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException {
        TestQuizz newQuizz = testQuizzService.createQuizz(testName, examTime, isStart, isEnd, Long.parseLong(topicId));
        return response(OK, ADD_QUICK_TEST_SUCCESS);
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public ResponseEntity<HttpResponse> updateTestQuizz(@RequestParam("currentTestName") String currentTestName,
                                                        @RequestParam("testName") String testName,
                                                        @RequestParam("examTime") Integer examTime,
                                                        @RequestParam("isStart") String isStart,
                                                        @RequestParam("isEnd") String isEnd,
                                                        @RequestParam(name = "topicId", required = false) String topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException {
        TestQuizz updateQuizz = testQuizzService.updateQuizz(currentTestName, testName, examTime, isStart, isEnd, Long.parseLong(topicId));
        return response(OK, UPDATE_QUICK_TEST_SUCCESS);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TestQuizz>> getAllQuizz() {
        List<TestQuizz> quizzes = testQuizzService.getAllQuizz();

        return new ResponseEntity<>(quizzes, OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public ResponseEntity<?> deleteTestQuizz(@PathVariable("id") Long id) {
        testQuizzService.deleteQuizz(id);

        return response(OK, DELETED_QUIZZ_TEST_SUCCESSFULLY);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Object> getTestQuizzByCode(@PathVariable("code") String code)
            throws TestQuizzNotFoundException {
        Optional<TestQuizz> quizz = testQuizzService.findTestQuizzByActivationCode(code);

        return new ResponseEntity<>(quizz, OK);
    }

    @GetMapping("/list-topic/{id}")
    public ResponseEntity<List<TestQuizz>> getAllQuizzByTopicId(@PathVariable("id") Long id) {
        List<TestQuizz> quizzes = testQuizzService.findAllTestQuizzByTopicId(id);

        return new ResponseEntity<>(quizzes, OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message){
        HttpResponse body = new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }

}
