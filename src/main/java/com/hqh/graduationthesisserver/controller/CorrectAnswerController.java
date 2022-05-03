package com.hqh.graduationthesisserver.controller;

import com.hqh.graduationthesisserver.dto.CorrectAnswerDto;
import com.hqh.graduationthesisserver.service.CorrectAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = { "/correct-answer" })
public class CorrectAnswerController {

    private final CorrectAnswerService correctAnswerService;

    @Autowired
    public CorrectAnswerController(CorrectAnswerService correctAnswerService) {
        this.correctAnswerService = correctAnswerService;
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<CorrectAnswerDto> getCorrectAnswer(@PathVariable("id") Long id) {
        return ResponseEntity
                .status(OK)
                .body(correctAnswerService.getTotalNumberOfCorrectAnswers(id));
    }

}
