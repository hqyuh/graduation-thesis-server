package com.hqh.quizserver.service;

import com.hqh.quizserver.domain.TestQuizz;
import com.hqh.quizserver.exception.domain.quizz.TestQuizzExistException;
import com.hqh.quizserver.exception.domain.quizz.TestQuizzNotFoundException;

import java.util.List;
import java.util.Optional;


public interface TestQuizzService {

    TestQuizz createQuizz(String testName, Integer examTime, String isStart, String isEnd, Long topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException;

    TestQuizz updateQuizz(String currentTestName, String newTestName, Integer examTime,
                          String isStart, String isEnd, Long topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException;

    TestQuizz findTestQuizzByTestName(String testName);

    List<TestQuizz> getAllQuizz();

    void deleteQuizz(Long id);

    Optional<TestQuizz> findTestQuizzByActivationCode(String code) throws TestQuizzNotFoundException;

    List<TestQuizz> findAllTestQuizzByTopicId(Long id);

    TestQuizz findTestQuizzById(Long id);

    void lockQuizz(Long id, boolean isStatus);

}
