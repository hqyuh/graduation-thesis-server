package com.hqh.graduationthesisserver.service.impl;

import com.hqh.graduationthesisserver.domain.Question;
import com.hqh.graduationthesisserver.domain.TestQuizz;
import com.hqh.graduationthesisserver.dto.QuestionDto;
import com.hqh.graduationthesisserver.exception.domain.user.NotAnImageFileException;
import com.hqh.graduationthesisserver.mapper.QuestionMapper;
import com.hqh.graduationthesisserver.repository.QuestionRepository;
import com.hqh.graduationthesisserver.repository.TestQuizzRepository;
import com.hqh.graduationthesisserver.service.QuestionService;
import com.hqh.graduationthesisserver.utils.FileUpLoadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.hqh.graduationthesisserver.constant.FileConstant.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.util.StringUtils.cleanPath;


@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TestQuizzRepository quizzRepository;
    private final QuestionMapper questionMapper;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,
                               TestQuizzRepository quizzRepository,
                               QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.quizzRepository = quizzRepository;
        this.questionMapper = questionMapper;
    }

    /***
     *
     * @param topicQuestion
     * @param questionImageUrl
     * @param answerA
     * @param answerB
     * @param answerC
     * @param answerD
     * @param correctResult
     * @param mark
     * @param quizzId
     * @throws IOException
     * @throws NotAnImageFileException
     */
    @Override
    public void createQuestion(String topicQuestion,
                               MultipartFile questionImageUrl,
                               String answerA,
                               String answerB,
                               String answerC,
                               String answerD,
                               String correctResult,
                               float mark,
                               Long quizzId)
            throws IOException, NotAnImageFileException {

        QuestionDto questionDto = new QuestionDto();
        TestQuizz quizz = quizzRepository.findTestQuizzById(quizzId);
        Question question = questionMapper.map(questionDto, quizz);
        question.setTopicQuestion(topicQuestion);
        question.setAnswerA(answerA);
        question.setAnswerB(answerB);
        question.setAnswerC(answerC);
        question.setAnswerD(answerD);
        question.setDateCreated(Instant.now());
        question.setCorrectResult(correctResult);
        question.setMark(mark);
        saveQuestionImage(question, questionImageUrl);

        questionRepository.save(question);

    }

    /***
     *
     * @param question
     * @param questionImage
     * @throws NotAnImageFileException
     * @throws IOException
     */
    private void saveQuestionImage(Question question,
                                   MultipartFile questionImage)
            throws NotAnImageFileException, IOException {

        if(!questionImage.isEmpty()) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_GIF_VALUE, IMAGE_PNG_VALUE)
                      .contains(questionImage.getContentType())) {
                throw new NotAnImageFileException(questionImage.getOriginalFilename() + PLEASE_UPLOAD_AN_IMAGE);
            }

            String fileName = cleanPath(questionImage.getOriginalFilename());
            question.setQuestionImageUrl(fileName);
            Question savedQuestion = questionRepository.save(question);

            String uploadDir = QUESTION_IMAGE_PATH + savedQuestion.getId();
            FileUpLoadUtil.clearDir(uploadDir);
            FileUpLoadUtil.saveFile(uploadDir, fileName, questionImage);

        }
    }

    @Override
    public List<QuestionDto> getAllQuestion() {
        return questionRepository
                .findAll()
                .stream()
                .map(questionMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateQuestion(Long id,
                               String topicQuestion,
                               String answerA,
                               String answerB,
                               String answerC,
                               String answerD,
                               String correctResult,
                               float mark,
                               Long quizzId,
                               MultipartFile questionImageUrl)
            throws IOException, NotAnImageFileException {
        TestQuizz quizz = quizzRepository.findTestQuizzById(quizzId);
        Question question = questionRepository.findQuestionById(id);
        question.setTopicQuestion(topicQuestion);
        question.setAnswerA(answerA);
        question.setAnswerB(answerB);
        question.setAnswerC(answerC);
        question.setAnswerD(answerD);
        question.setCorrectResult(correctResult);
        question.setMark(mark);
        question.setTestQuizz(quizz);
        saveQuestionImage(question, questionImageUrl);

        questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
