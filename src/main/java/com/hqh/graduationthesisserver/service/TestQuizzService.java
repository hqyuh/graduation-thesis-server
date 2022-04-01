package com.hqh.graduationthesisserver.service;

import com.hqh.graduationthesisserver.domain.TestQuizz;
import com.hqh.graduationthesisserver.exception.domain.quizz.TestQuizzExistException;
import com.hqh.graduationthesisserver.exception.domain.quizz.TestQuizzNotFoundException;

import java.util.List;
import java.util.Optional;


public interface TestQuizzService {

    TestQuizz createQuizz(String testName, Integer examTime, String isStart, String isEnd)
            throws TestQuizzExistException, TestQuizzNotFoundException;

    TestQuizz updateQuizz(String currentTestName, String newTestName, Integer examTime,
                          String isStart, String isEnd)
            throws TestQuizzExistException, TestQuizzNotFoundException;

    TestQuizz findTestQuizzByTestName(String testName);

    List<TestQuizz> getAllQuizz();

    void deleteQuizz(Long id);

    Optional<TestQuizz> findTestQuizzByActivationCode(String code) throws TestQuizzNotFoundException;

}
