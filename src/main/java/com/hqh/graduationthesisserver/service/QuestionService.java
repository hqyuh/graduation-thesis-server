package com.hqh.graduationthesisserver.service;

import com.hqh.graduationthesisserver.domain.Question;
import com.hqh.graduationthesisserver.dto.QuestionDto;
import com.hqh.graduationthesisserver.exception.domain.user.NotAnImageFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService {

    void createQuestion(String topicQuestion, MultipartFile questionImageUrl, String answerA,
                        String answerB, String answerC, String answerD, String correctResult,
                        String correctEssay, String type, float mark, Long quizzId)
            throws IOException, NotAnImageFileException;

    void updateQuestion(Long id, String topicQuestion, String answerA, String answerB,
                        String answerC, String answerD, String correctResult, String correctEssay,
                        String type, float mark, Long quizzId, MultipartFile questionImageUrl)
            throws IOException, NotAnImageFileException;

    void deleteQuestion(Long id);

    List<QuestionDto> getAllQuestion();

}
