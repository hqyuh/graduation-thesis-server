package com.hqh.graduationthesisserver.service;

import com.hqh.graduationthesisserver.dto.QuestionDto;

import java.util.List;

public interface QuestionService {

    void createQuestion(String topicQuestion, String answerA,
                        String answerB, String answerC, String answerD, String correctResult,
                        String correctEssay, String type, float mark, Long quizzId);

    void updateQuestion(Long id, String topicQuestion, String answerA, String answerB,
                        String answerC, String answerD, String correctResult, String correctEssay,
                        String type, float mark, Long quizzId);

    void deleteQuestion(Long id);

    List<QuestionDto> getAllQuestion();

}
