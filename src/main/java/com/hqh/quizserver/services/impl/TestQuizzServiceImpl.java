package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.QuestionDTO;
import com.hqh.quizserver.dto.UserDTO;
import com.hqh.quizserver.entities.Question;
import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.entities.Topic;
import com.hqh.quizserver.dto.TestQuizzDTO;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzExistException;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzNotFoundException;
import com.hqh.quizserver.helper.quizz.ExcelHelper;
import com.hqh.quizserver.mapper.QuestionMapper;
import com.hqh.quizserver.mapper.TestQuizzMapper;
import com.hqh.quizserver.repositories.QuestionRepository;
import com.hqh.quizserver.repositories.TestQuizzRepository;
import com.hqh.quizserver.repositories.TopicRepository;
import com.hqh.quizserver.services.TestQuizzHelperService;
import com.hqh.quizserver.services.TestQuizzService;
import com.hqh.quizserver.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.*;

import static com.hqh.quizserver.utils.ConvertTimeUtils.convertTime;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class TestQuizzServiceImpl implements TestQuizzService, TestQuizzHelperService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final TestQuizzRepository quizzRepository;
    private final TopicRepository topicRepository;
    private final TestQuizzMapper testQuizzMapper;
    private final UserService userService;
    private final QuestionRepository questionRepo;

    @Autowired
    public TestQuizzServiceImpl(TestQuizzRepository quizzRepository,
                                TopicRepository topicRepository,
                                TestQuizzMapper testQuizzMapper,
                                UserService userService,
                                QuestionRepository questionRepo) {
        this.quizzRepository = quizzRepository;
        this.topicRepository = topicRepository;
        this.testQuizzMapper = testQuizzMapper;
        this.userService = userService;
        this.questionRepo = questionRepo;
    }


    /**
     * <h3>Validate that the new quiz exists and that the current quiz exists</h3>
     *
     * @param currentQuizz The name of the current quiz.
     * @param newQuizz The new name of the quiz
     */
    private TestQuizz validateNewQuizzExists (String currentQuizz, String newQuizz)
            throws TestQuizzNotFoundException, TestQuizzExistException {

//        TestQuizz testQuizz = findTestQuizzByTestName(newQuizz);
//
//        if(StringUtils.isNotBlank(currentQuizz)) {
//            TestQuizz currentTest = findTestQuizzByTestName(currentQuizz);
//
//            if(currentTest == null) {
//                log.error("No quiz found by name {}", currentQuizz);
//                throw new TestQuizzNotFoundException(NO_QUIZZ_FOUND_BY_NAME + currentQuizz);
//            }
//            if(testQuizz != null && !currentTest.getId().equals(testQuizz.getId())) {
//                log.error("Quizz already exists!");
//                throw new TestQuizzExistException(QUIZZ_ALREADY_EXISTS);
//            }
//            return currentTest;
//        } else {
//            if(testQuizz != null) {
//                log.error("Quizz already exists!");
//                throw new TestQuizzExistException(QUIZZ_ALREADY_EXISTS);
//            }
//            return null;
//        }
        return null;
    }


    /**
     * Generate a random string of 6 digits.
     *
     * @return A random string of 6 numbers
     */
    private String generateActivationCode() {
        return RandomStringUtils.randomNumeric(6);
    }


    /**
     * <h3>This function is creates a new test quizz.</h3>
     *
     * @param testName The name of the test
     * @param examTime The time the test will be taken.
     * @param isStart The time the test starts
     * @param isEnd The time when the test ends.
     * @param topicId The id of the topic that the test belongs to.
     * @return TestQuizz
     */
    @Override
    public TestQuizz createQuizz(String testName, Integer examTime, String isStart, String isEnd, Long topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException {

        validateNewQuizzExists(EMPTY, testName);
        TestQuizzDTO testQuizzDto = new TestQuizzDTO();
        Topic topic = topicRepository.findTopicById(topicId);
        TestQuizz quizz = testQuizzMapper.map(testQuizzDto, topic);
        UserDTO user = userService.getCurrentUser();
        quizz.setTestName(testName);
        String code = generateActivationCode();
        quizz.setActivationCode(code);
        quizz.setDateCreated(Instant.now());
        quizz.setExamTime(examTime);
        quizz.setIsStart(convertTime(isStart));
        quizz.setIsEnd(convertTime(isEnd));
        quizz.setStatus(true);
        quizz.setCreatedAt(new Date());
        quizz.setUpdatedAt(new Date());
        quizz.setCreatedBy(user.getUsername());
        quizz.setUpdatedBy(user.getUsername());
        log.info("Code {} is for test name {}", code, testName);
        log.info("{} created a test with name {}", user.getUsername(), testName);
        quizzRepository.save(quizz);

        return quizz;
    }


    /**
     * <h3>Update a quizz</h3>
     *
     * @param currentTestName The name of the test that is currently being edited.
     * @param newTestName The new name of the test
     * @param examTime The time for the test
     * @param isStart The time the test starts
     * @param isEnd The time when the test ends.
     * @param topicId The id of the topic that the test belongs to.
     * @return The updated TestQuizz object.
     */
    @Override
    public TestQuizz updateQuizz(String currentTestName, String newTestName, Integer examTime, String isStart,
                                 String isEnd, Long topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException {

        Topic topic = topicRepository.findTopicById(topicId);
        TestQuizz currentQuizz = validateNewQuizzExists(currentTestName, newTestName);
        if (currentQuizz != null) {
            currentQuizz.setTestName(newTestName);
            currentQuizz.setExamTime(examTime);
            currentQuizz.setActivationCode(currentQuizz.getActivationCode());
            currentQuizz.setIsStart(convertTime(isStart));
            currentQuizz.setIsEnd(convertTime(isEnd));
            currentQuizz.setTopic(topic);
            quizzRepository.save(currentQuizz);
        }
        log.info("Update quick test success");
        return currentQuizz;
    }


    /**
     * <h3>It returns a TestQuizz object by searching for the testName.</h3>
     *
     * @param testName The name of the test you want to find.
     * @return TestQuizz
     */
    @Override
    public TestQuizzDTO findTestQuizzByTestName(String testName) {
        TestQuizz testQuizz = quizzRepository.findTestQuizzByTestName(testName);

//        return TestQuizzDTO.builder()
//                .testName(testQuizz.getTestName())
//                .examTime(testQuizz.getExamTime())
//                .dateCreated(testQuizz.getDateCreated())
//                .isStart(testQuizz.getIsStart())
//                .isEnd(testQuizz.getIsEnd())
//                .activationCode(testQuizz.getActivationCode())
//                .questionDTOList(questionRepo.randomQuestion(3L))
//                .topicId(testQuizz.getTopic().getId())
//                .build();
        return null;
    }

    @Override
    public List<TestQuizz> getAllQuizz() {
        return quizzRepository.findAll();
    }

    @Override
    public void deleteQuizz(Long id) {
        quizzRepository.deleteById(id);
    }

    /**
     * <h3>It returns an optional of a TestQuizz object, which is found by the activation code, or throws a
     *  TestQuizzNotFoundException if the code is not found</h3>
     *
     * @param code The activation code of the test quizz.
     * @return Optional<TestQuizz>
     */
    @Override
    public TestQuizzDTO findTestQuizzByActivationCode(String code) throws TestQuizzNotFoundException {
        TestQuizz testQuizz = quizzRepository.findTestQuizzByActivationCode(code);
        List<Question> questionFromData = questionRepo.randomQuestion(testQuizz.getId());

        List<QuestionDTO> questionDTOListTemp = questionMapToQuestionDTO(questionFromData);

        return TestQuizzDTO.builder()
                .testName(testQuizz.getTestName())
                .examTime(testQuizz.getExamTime())
                .dateCreated(testQuizz.getDateCreated())
                .isStart(testQuizz.getIsStart())
                .isEnd(testQuizz.getIsEnd())
                .activationCode(testQuizz.getActivationCode())
                .questionDTOList(questionDTOListTemp)
                .build();
    }

    private List<QuestionDTO> questionMapToQuestionDTO(List<Question> questions) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        questions.forEach(srcQuestionDTO -> {
            QuestionDTO targetQuestionDTO = new QuestionDTO();
            BeanUtils.copyProperties(srcQuestionDTO, targetQuestionDTO);
            targetQuestionDTO.setId(srcQuestionDTO.getId());
            targetQuestionDTO.setTopicQuestion(srcQuestionDTO.getTopicQuestion());
            targetQuestionDTO.setQuestionImageUrl(srcQuestionDTO.getQuestionImageUrl());
            targetQuestionDTO.setAnswerA(srcQuestionDTO.getAnswerA());
            targetQuestionDTO.setAnswerB(srcQuestionDTO.getAnswerB());
            targetQuestionDTO.setAnswerC(srcQuestionDTO.getAnswerC());
            targetQuestionDTO.setAnswerD(srcQuestionDTO.getAnswerD());
            targetQuestionDTO.setCorrectResult(srcQuestionDTO.getCorrectResult());
            targetQuestionDTO.setCorrectEssay(srcQuestionDTO.getCorrectEssay());
            targetQuestionDTO.setMark(srcQuestionDTO.getMark());
            targetQuestionDTO.setMilestones(srcQuestionDTO.getMilestones());
            targetQuestionDTO.setQuizzId(srcQuestionDTO.getTestQuizz().getId());

            questionDTOList.add(targetQuestionDTO);
        });
        return questionDTOList;
    }

    @Override
    public List<TestQuizz> findAllTestQuizzByTopicId(Long id) {
        return quizzRepository.findTestQuizzByTopicId(id);
    }

    @Override
    public TestQuizz findTestQuizzById(Long id) {
        return quizzRepository.findTestQuizzById(id);
    }


    /**
     * <h3>It takes a quizz id, finds the quizz, and returns an Excel file containing the quizz</h3>
     *
     * @param id the id of the quizz you want to export
     * @return A ByteArrayInputStream
     */
    @Override
    public ByteArrayInputStream loadExcel(long id) {
        TestQuizz quizz = quizzRepository.findTestQuizzById(id);

        return ExcelHelper.quizzesToExcel(quizz);
    }

    @Override
    @Transactional
    public void lockQuizz(Long id, boolean isStatus) {
        quizzRepository.quizzLock(id, isStatus);
    }
}
