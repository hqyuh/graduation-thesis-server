package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.QuestionDTO;
import com.hqh.quizserver.exceptions.domain.user.NotAnImageFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService {

    void createQuestion(String topicQuestion,
                        MultipartFile questionImageUrl,
                        String answerA, String answerB,
                        String answerC, String answerD,
                        String correctResult, String correctEssay,
                        String type, double mark, Long quizzId, String level)
            throws IOException, NotAnImageFileException;

    void updateQuestion(Long id, String topicQuestion,
                        String answerA, String answerB,
                        String answerC, String answerD,
                        String correctResult, String correctEssay,
                        String type, double mark, Long quizzId,
                        MultipartFile questionImageUrl, String level)
            throws IOException, NotAnImageFileException;

    void deleteQuestion(Long id);

    List<QuestionDTO> getAllQuestion(int currentPage);

}
