package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.QuestionDTO;
import com.hqh.quizserver.entity.Question;
import com.hqh.quizserver.entity.TestQuizz;
import com.hqh.quizserver.exceptions.domain.user.NotAnImageFileException;
import com.hqh.quizserver.helper.quizz.ExcelHelper;
import com.hqh.quizserver.mapper.QuestionMapper;
import com.hqh.quizserver.repositories.QuestionRepository;
import com.hqh.quizserver.repositories.TestQuizzRepository;
import com.hqh.quizserver.services.QuestionHelperService;
import com.hqh.quizserver.services.QuestionService;
import com.hqh.quizserver.services.UserService;
import com.hqh.quizserver.utils.FileUpLoadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hqh.quizserver.constant.FileConstant.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.util.StringUtils.cleanPath;


@Service
public class QuestionServiceImpl implements QuestionService, QuestionHelperService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final int NUMBER_OF_QUESTIONS = 3;
    private final QuestionRepository questionRepository;
    private final TestQuizzRepository quizzRepository;
    private final QuestionMapper questionMapper;
    private final UserService userService;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,
                               TestQuizzRepository quizzRepository,
                               QuestionMapper questionMapper,
                               UserService userService) {
        this.questionRepository = questionRepository;
        this.quizzRepository = quizzRepository;
        this.questionMapper = questionMapper;
        this.userService = userService;
    }


    /**
     * It creates a question and saves it to the database
     *
     * @param topicQuestion The question itself
     * @param questionImageUrl the image file that is uploaded
     * @param answerA String
     * @param answerB The answer to the question
     * @param answerC "C"
     * @param answerD "D"
     * @param correctResult The correct answer to the question.
     * @param correctEssay This is the correct answer for the essay type question.
     * @param type This is the type of question. It can be either "multiple choice" or "essay".
     * @param mark the mark of the question
     * @param quizzId The id of the quiz that the question belongs to.
     */
    @Override
    public void createQuestion(String topicQuestion, MultipartFile questionImageUrl, String answerA, String answerB,
                               String answerC, String answerD, String correctResult, String correctEssay, String type,
                               double mark, Long quizzId) throws IOException, NotAnImageFileException {

        log.info("Get user create");

        log.info("Create question");
        QuestionDTO questionDTO = new QuestionDTO();
        TestQuizz quizz = quizzRepository.findTestQuizzById(quizzId);
        Question question = questionMapper.map(questionDTO, quizz);
        question.setTopicQuestion(topicQuestion);
        question.setAnswerA(answerA);
        question.setAnswerB(answerB);
        question.setAnswerC(answerC);
        question.setAnswerD(answerD);
        question.setDateCreated(Instant.now());
        question.setCorrectResult(correctResult.isBlank() ? null : correctResult);
        question.setCorrectEssay(correctEssay.isBlank() ? null : correctEssay);
        question.setType(type);
        question.setMark(mark);
        question.setMilestones(1);
        question.setCreatedAt(new Date());
        question.setUpdatedAt(new Date());
        question.setCreatedBy(userService.getCurrentUser().getUsername());
        question.setUpdatedBy(userService.getCurrentUser().getUsername());
        saveQuestionImage(question, questionImageUrl);
        questionRepository.save(question);
    }


    private void saveQuestionImage(Question question, MultipartFile questionImage)
            throws NotAnImageFileException, IOException {

        if(!questionImage.isEmpty()) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_GIF_VALUE, IMAGE_PNG_VALUE)
                      .contains(questionImage.getContentType())) {
                log.error("{} is not an image file. Please upload an image", questionImage.getOriginalFilename());
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
    public List<QuestionDTO> getAllQuestion(int currentPage) {
        int start = (currentPage - 1) * NUMBER_OF_QUESTIONS;
        int end = NUMBER_OF_QUESTIONS;
        if (start > end) {
            start = end;
        }
        return questionRepository.questionsPagination(start, end)
                .stream()
                .map(questionMapper::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Update question
     *
     * @param id the id of the question to be updated
     * @param topicQuestion The question itself
     * @param answerA String
     * @param answerB "B"
     * @param answerC "C"
     * @param answerD "D"
     * @param correctResult the correct answer to the question
     * @param correctEssay the correct answer for the essay question
     * @param type The type of question, which can be multiple choice or essay.
     * @param mark the mark of the question
     * @param quizzId the id of the quiz that the question belongs to
     * @param questionImageUrl the image file that the user uploads
     */
    @Override
    public void updateQuestion(Long id, String topicQuestion, String answerA, String answerB, String answerC, String answerD,
                               String correctResult, String correctEssay, String type, double mark, Long quizzId,
                               MultipartFile questionImageUrl) throws IOException, NotAnImageFileException {
        TestQuizz quizz = quizzRepository.findTestQuizzById(quizzId);
        Question question = questionRepository.findQuestionById(id);
        question.setTopicQuestion(topicQuestion);
        question.setAnswerA(answerA);
        question.setAnswerB(answerB);
        question.setAnswerC(answerC);
        question.setAnswerD(answerD);
        question.setCorrectResult(correctResult.isBlank() ? null : correctResult);
        question.setCorrectEssay(correctEssay.isBlank() ? null : correctEssay);
        question.setType(type);
        question.setMark(mark);
        question.setCreatedAt(new Date());
        question.setUpdatedAt(new Date());
        question.setCreatedBy(userService.getCurrentUser().getUsername());
        question.setUpdatedBy(userService.getCurrentUser().getUsername());
        question.setTestQuizz(quizz);
        saveQuestionImage(question, questionImageUrl);
        questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public void saveFile(MultipartFile multipartFile, Long id) {
        TestQuizz quizz = quizzRepository.findTestQuizzById(id);
        String username = userService.getCurrentUser().getUsername();
        try {
            List<Question> questions = ExcelHelper.importFromExcel(multipartFile.getInputStream(), quizz, username);
            questionRepository.saveAll(questions);
        } catch (IOException exception) {
            throw new RuntimeException("Fail to store excel data: " + exception.getMessage());
        }
    }
}
