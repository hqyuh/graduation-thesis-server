package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.entities.Question;
import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.dto.QuestionDto;
import com.hqh.quizserver.exceptions.domain.user.NotAnImageFileException;
import com.hqh.quizserver.helper.quizz.ExcelHelper;
import com.hqh.quizserver.mapper.QuestionMapper;
import com.hqh.quizserver.repositories.QuestionRepository;
import com.hqh.quizserver.repositories.TestQuizzRepository;
import com.hqh.quizserver.services.QuestionHelperService;
import com.hqh.quizserver.services.QuestionService;
import com.hqh.quizserver.utils.FileUpLoadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqh.quizserver.constant.FileConstant.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.util.StringUtils.cleanPath;


@Service
public class QuestionServiceImpl implements QuestionService, QuestionHelperService {

    public static final int NUMBER_OF_QUESTIONS = 3;
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
     * @param correctEssay
     * @param type
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
                               String correctEssay,
                               String type,
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
        question.setCorrectEssay(correctEssay);
        question.setType(type);
        question.setMark(mark);
        question.setMilestones(1);
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

            String fileName = cleanPath(Objects.requireNonNull(questionImage.getOriginalFilename()));
            question.setQuestionImageUrl(fileName);
            Question savedQuestion = questionRepository.save(question);

            String uploadDir = QUESTION_IMAGE_PATH + savedQuestion.getId();
            FileUpLoadUtils.clearDir(uploadDir);
            FileUpLoadUtils.saveFile(uploadDir, fileName, questionImage);

        }
    }

    @Override
    public List<QuestionDto> getAllQuestion(int currentPage) {
        // int totalNumberOfRecords = questionRepository.getTotalNumberOfRecords();
        // int totalPage = totalNumberOfRecords / NUMBER_OF_QUESTIONS;
        int START = (currentPage - 1) * NUMBER_OF_QUESTIONS;
        int END = NUMBER_OF_QUESTIONS;
        if (START > END) {
            START = END;
        }
        return questionRepository.questionsPagination(START, END)
                .stream()
                .map(questionMapper::mapToDto)
                .collect(Collectors.toList());
    }

    /***
     *
     * @param id
     * @param topicQuestion
     * @param answerA
     * @param answerB
     * @param answerC
     * @param answerD
     * @param correctResult
     * @param mark
     * @param quizzId
     */
    @Override
    public void updateQuestion(Long id,
                               String topicQuestion,
                               String answerA,
                               String answerB,
                               String answerC,
                               String answerD,
                               String correctResult,
                               String correctEssay,
                               String type,
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
        question.setCorrectEssay(correctEssay);
        question.setType(type);
        question.setMark(mark);
        question.setTestQuizz(quizz);
        saveQuestionImage(question, questionImageUrl);
        questionRepository.save(question);
    }

    /***
     * @param id
     */
    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    /***
     * @param multipartFile
     * @param id
     */
    @Override
    public void saveFile(MultipartFile multipartFile, Long id) {
        TestQuizz quizz = quizzRepository.findTestQuizzById(id);
        try {
            List<Question> questions = ExcelHelper.importFromExcel(multipartFile.getInputStream(), quizz);
            questionRepository.saveAll(questions);
        } catch (IOException exception) {
            throw new RuntimeException(FAIL_TO_STORE_EXCEL_DATA + exception.getMessage());
        }
    }
}
