package com.hqh.quizserver.controller;

import com.hqh.quizserver.dto.TestQuizzDTO;
import com.hqh.quizserver.dto.TestQuizzResponseDTO;
import com.hqh.quizserver.entities.HttpResponse;
import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.exceptions.ExceptionHandling;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzCreateTimeException;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzExistException;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzNotFoundException;
import com.hqh.quizserver.dto.TestQuizzRequestDTO;
import com.hqh.quizserver.services.TestQuizzHelperService;
import com.hqh.quizserver.services.TestQuizzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static com.hqh.quizserver.constant.FileConstant.*;
import static com.hqh.quizserver.constant.MessageTypeConstant.SUCCESS;
import static com.hqh.quizserver.constant.TestQuizzImplConstant.*;
import static com.hqh.quizserver.utils.ResponseUtils.response;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = { "/quizz" })
public class TestQuizzController extends ExceptionHandling {

    private final TestQuizzService testQuizzService;
    private final TestQuizzHelperService helperService;

    @Autowired
    public TestQuizzController(TestQuizzService testQuizzService,
                               TestQuizzHelperService helperService) {
        this.testQuizzService = testQuizzService;
        this.helperService = helperService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public ResponseEntity<HttpResponse> addNewTestQuizz(@RequestBody TestQuizzRequestDTO testQuizz)
            throws TestQuizzExistException, TestQuizzNotFoundException, TestQuizzCreateTimeException {
        testQuizzService.createQuizz(
                testQuizz.getTestName(),
                Integer.parseInt(testQuizz.getExamTime()),
                testQuizz.getIsStart(),
                testQuizz.getIsEnd(),
                Long.parseLong(testQuizz.getTopicId())
        );
        return response(OK, SUCCESS, ADD_QUICK_TEST_SUCCESS);
}

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public ResponseEntity<HttpResponse> updateTestQuizz(@RequestBody TestQuizzRequestDTO testQuizz)
            throws TestQuizzExistException, TestQuizzNotFoundException, TestQuizzCreateTimeException {
        testQuizzService.updateQuizz(
                testQuizz.getCurrentTestName(),
                testQuizz.getTestName(),
                Integer.parseInt(testQuizz.getExamTime()),
                testQuizz.getIsStart(),
                testQuizz.getIsEnd(),
                Long.parseLong(testQuizz.getTopicId())
        );
        return response(OK, SUCCESS, UPDATE_QUICK_TEST_SUCCESS);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TestQuizzResponseDTO>> getAllQuizz() {
        List<TestQuizzResponseDTO> quizzes = testQuizzService.getAllQuizz();

        return new ResponseEntity<>(quizzes, OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public ResponseEntity<?> deleteTestQuizz(@PathVariable("id") Long id) {
        testQuizzService.deleteQuizz(id);

        return response(OK, SUCCESS, DELETED_QUIZZ_TEST_SUCCESSFULLY);
    }

    @GetMapping("/activated")
    public ResponseEntity<TestQuizzDTO> getTestQuizzByCode(@RequestParam String code,
                                                           @RequestParam Integer amount)
            throws TestQuizzNotFoundException {
        TestQuizzDTO testQuizzDTO = testQuizzService.findTestQuizzByActivationCode(code, amount);

        return new ResponseEntity<>(testQuizzDTO, OK);
    }

    @GetMapping("/list-topic/{id}")
    public ResponseEntity<List<TestQuizzResponseDTO>> getAllQuizzByTopicId(@PathVariable("id") Long id) {
        List<TestQuizzResponseDTO> quizzes = testQuizzService.findAllTestQuizzByTopicId(id);

        return new ResponseEntity<>(quizzes, OK);
    }

    @GetMapping("/export/excel/{id}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long id) {
        String fileName = "quizzes" + DOT + XLSX_EXTENSION;
        InputStreamResource file = new InputStreamResource(helperService.loadExcel(id));
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName)
                             .contentType(MediaType.parseMediaType(APPLICATION_EXCEL))
                             .body(file);
    }

    @GetMapping("/{id}/locked/{isStatus}")
    public ResponseEntity<?> quizzLock(@PathVariable("id") Long id,
                                       @PathVariable("isStatus") String isStatus) {
        testQuizzService.lockQuizz(id, Boolean.parseBoolean(isStatus));
        String message = Boolean.parseBoolean(isStatus) ? "LOCK OK" : "UNLOCK OK";

        return response(OK, SUCCESS, message);
    }

}
