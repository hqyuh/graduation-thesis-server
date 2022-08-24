package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.TestQuizzDTO;
import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzExistException;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzNotFoundException;

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

    TestQuizzDTO findTestQuizzByActivationCode(String code) throws TestQuizzNotFoundException;

    List<TestQuizz> findAllTestQuizzByTopicId(Long id);

    TestQuizz findTestQuizzById(Long id);

    void lockQuizz(Long id, boolean isStatus);

}
