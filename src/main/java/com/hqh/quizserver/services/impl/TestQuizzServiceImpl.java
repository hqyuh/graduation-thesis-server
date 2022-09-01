package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.QuestionDTO;
import com.hqh.quizserver.dto.TestQuizzDTO;
import com.hqh.quizserver.entities.Question;
import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.entities.Topic;
import com.hqh.quizserver.entities.User;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzCreateTimeException;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.hqh.quizserver.constant.TestQuizzImplConstant.*;
import static com.hqh.quizserver.utils.ConvertTimeUtils.convertTime;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class TestQuizzServiceImpl implements TestQuizzService, TestQuizzHelperService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final TestQuizzRepository quizzRepository;
    private final TopicRepository topicRepository;
    private final TestQuizzMapper testQuizzMapper;
    private final UserService userService;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Autowired
    public TestQuizzServiceImpl(TestQuizzRepository quizzRepository,
                                TopicRepository topicRepository,
                                TestQuizzMapper testQuizzMapper,
                                UserService userService,
                                QuestionRepository questionRepository,
                                QuestionMapper questionMapper) {
        this.quizzRepository = quizzRepository;
        this.topicRepository = topicRepository;
        this.testQuizzMapper = testQuizzMapper;
        this.userService = userService;
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
    }

    private String logged = null;

    /**
     * > Validate that the new quizz exists and that the current quizz exists if it is not null
     *
     * @param currentQuizz The name of the current quizz.
     * @param newQuizz The new name of the test
     */
    private TestQuizz validateNewQuizzExists (String currentQuizz, String newQuizz)
            throws TestQuizzNotFoundException, TestQuizzExistException {

        TestQuizz testQuizz = findTestQuizzByTestName(newQuizz);
        logged = userService.getCurrentUser().getUsername();

        if(StringUtils.isNotBlank(currentQuizz)) {
            TestQuizz currentTest = findTestQuizzByTestName(currentQuizz);

            if(currentTest == null) {
                log.error("No quizz found by name: {}", currentQuizz);
                throw new TestQuizzNotFoundException(NO_QUIZZ_FOUND_BY_NAME + currentQuizz);
            }
            if(testQuizz != null && !currentTest.getId().equals(testQuizz.getId())) {
                log.error("Quizz already exists");
                throw new TestQuizzExistException(QUIZZ_ALREADY_EXISTS);
            }
            return currentTest;
        } else {
            if(testQuizz != null) {
                log.error("Quizz already exists");
                throw new TestQuizzExistException(QUIZZ_ALREADY_EXISTS);
            }
            return null;
        }
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
     * Create a new quiz
     *
     * @param testName The name of the test
     * @param examTime The time for the test
     * @param isStart The time the test starts
     * @param isEnd The time when the test ends.
     * @param topicId The id of the topic that the quiz belongs to.
     * @return A new TestQuizz object
     */
    @Override
    public TestQuizz createQuizz(String testName, Integer examTime, String isStart, String isEnd, Long topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException, TestQuizzCreateTimeException {

        log.info("Create quizz");
        validateNewQuizzExists(EMPTY, testName);
        TestQuizzDTO testQuizzDTO = new TestQuizzDTO();
        Topic topic = topicRepository.findTopicById(topicId);
        TestQuizz quizz = testQuizzMapper.map(testQuizzDTO, topic);
        User user = userService.getCurrentUser();

        boolean isCheckCreate = isCheckCreateTime(isStart, isEnd);
        if (!isCheckCreate) {
            quizz.setIsStart(convertTime(isStart));
            quizz.setIsEnd(convertTime(isEnd));
            quizz.setTestName(testName);
            String code = generateActivationCode();
            quizz.setActivationCode(code);
            quizz.setDateCreated(Instant.now());
            quizz.setExamTime(examTime);
            quizz.setStatus(true);
            quizz.setCreatedAt(new Date());
            quizz.setUpdatedAt(new Date());
            quizz.setCreatedBy(logged);
            quizz.setUpdatedBy(logged);
            log.info("Code {} is for test name {}", code, testName);
            quizz.addUser(user);
            log.info("{} created quizz", user.getUsername());
            quizzRepository.save(quizz);
        }
        return quizz;
    }

    /**
     * This function is used to check if the start time and end time of the test is valid
     *
     * @param isStart The start time of the test
     * @param isEnd The end time of the test
     * @return A boolean value.
     */
    private boolean isCheckCreateTime(String isStart, String isEnd) throws TestQuizzCreateTimeException {
        Timestamp currTime = new Timestamp(new Date().getTime());
        Timestamp startTime = convertTime(isStart);
        Timestamp endTime = convertTime(isEnd);
        if (startTime.after(endTime)) {
            log.error("Start time cannot be greater than end time!");
            throw new TestQuizzCreateTimeException("Start time cannot be greater than end time!");
        } else if (startTime.after(currTime) || endTime.after(currTime)) {
            log.error("Start time and end time cannot be greater than current time!");
            throw new TestQuizzCreateTimeException("Start time and end time cannot be greater than current time!");
        }
        return false;
    }


    @Override
    public TestQuizz updateQuizz(String currTestName, String newTestName, Integer examTime, String isStart,
                                 String isEnd, Long topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException, TestQuizzCreateTimeException {

        log.info("Update quizz");
        Topic topic = topicRepository.findTopicById(topicId);
        TestQuizz currentQuizz = validateNewQuizzExists(currTestName, newTestName);

        boolean isCheckCreate = isCheckCreateTime(isStart, isEnd);

        if (!isCheckCreate && currentQuizz != null) {
            currentQuizz.setTestName(newTestName);
            currentQuizz.setExamTime(examTime);
            currentQuizz.setActivationCode(currentQuizz.getActivationCode());
            currentQuizz.setIsStart(convertTime(isStart));
            currentQuizz.setIsEnd(convertTime(isEnd));currentQuizz.setTopic(topic);
            currentQuizz.setCreatedAt(new Date());
            currentQuizz.setUpdatedAt(new Date());
            currentQuizz.setCreatedBy(logged);
            currentQuizz.setUpdatedBy(logged);
            quizzRepository.save(currentQuizz);
        }
        return currentQuizz;
    }

    @Override
    public TestQuizz findTestQuizzByTestName(String testName) {
        return quizzRepository.findTestQuizzByTestName(testName);
    }

    @Override
    public List<TestQuizz> getAllQuizz() {
        return quizzRepository.findAll();
    }

    @Override
    public void deleteQuizz(Long id) {
        quizzRepository.deleteById(id);
    }

    @Override
    public TestQuizzDTO findTestQuizzByActivationCode(String code) throws TestQuizzNotFoundException {
        Optional<TestQuizz> testQuizz = quizzRepository.findTestQuizzByActivationCode(code);
        if (testQuizz.isEmpty()) {
            log.error("No quizz found with code {}", code);
            throw new TestQuizzNotFoundException(NO_QUIZZ_TEST_FOUND_WITH_CODE + code);
        }
        List<Question> questionList = questionRepository.randomQuestion(testQuizz.get().getId(), 5);
        List<QuestionDTO> questionDTOList = questionMapper.questionMapToQuestionDTO(questionList);

        return TestQuizzDTO.builder()
                .id(testQuizz.get().getId())
                .testName(testQuizz.get().getTestName())
                .examTime(testQuizz.get().getExamTime())
                .dateCreated(testQuizz.get().getDateCreated())
                .isStart(testQuizz.get().getIsStart())
                .isEnd(testQuizz.get().getIsEnd())
                .activationCode(testQuizz.get().getActivationCode())
                .questionDTOList(questionDTOList)
                .build();
    }

    @Override
    public List<TestQuizz> findAllTestQuizzByTopicId(Long id) {
        return quizzRepository.findTestQuizzByTopicId(id);
    }

    @Override
    public TestQuizz findTestQuizzById(Long id) {
        return quizzRepository.findTestQuizzById(id);
    }

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
