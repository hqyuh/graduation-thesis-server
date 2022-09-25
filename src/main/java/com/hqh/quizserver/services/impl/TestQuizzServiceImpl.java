package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.QuestionDTO;
import com.hqh.quizserver.dto.TestQuizzDTO;
import com.hqh.quizserver.dto.TestQuizzRequestDTO;
import com.hqh.quizserver.dto.TestQuizzResponseDTO;
import com.hqh.quizserver.entity.*;
import com.hqh.quizserver.enumeration.LevelQuizEnum;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzCreateTimeException;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzExistException;
import com.hqh.quizserver.exceptions.domain.quizz.TestQuizzNotFoundException;
import com.hqh.quizserver.helper.quizz.ExcelHelper;
import com.hqh.quizserver.mapper.QuestionMapper;
import com.hqh.quizserver.mapper.TestQuizzMapper;
import com.hqh.quizserver.repository.QuestionRepository;
import com.hqh.quizserver.repository.TestQuizzRepository;
import com.hqh.quizserver.repository.TopicRepository;
import com.hqh.quizserver.repository.UserMarkRepository;
import com.hqh.quizserver.services.TestQuizzHelperService;
import com.hqh.quizserver.services.TestQuizzService;
import com.hqh.quizserver.services.UserService;
import com.hqh.quizserver.utils.ConvertTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static com.hqh.quizserver.constant.TestQuizzImplConstant.*;
import static com.hqh.quizserver.utils.ConvertTimeUtils.convertTime;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
@Slf4j
public class TestQuizzServiceImpl implements TestQuizzService, TestQuizzHelperService {

    private final TestQuizzRepository quizzRepository;
    private final TopicRepository topicRepository;
    private final TestQuizzMapper testQuizzMapper;
    private final UserService userService;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final UserMarkRepository userMarkRepository;

    @Autowired
    public TestQuizzServiceImpl(TestQuizzRepository quizzRepository,
                                TopicRepository topicRepository,
                                TestQuizzMapper testQuizzMapper,
                                UserService userService,
                                QuestionRepository questionRepository,
                                QuestionMapper questionMapper,
                                UserMarkRepository userMarkRepository) {
        this.quizzRepository = quizzRepository;
        this.topicRepository = topicRepository;
        this.testQuizzMapper = testQuizzMapper;
        this.userService = userService;
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.userMarkRepository = userMarkRepository;
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
     * Create a new test
     *
     * @param request the request object that contains the parameters of the request
     * @return A TestQuizz object
     */
    @Override
    public TestQuizz createQuizz(TestQuizzRequestDTO request)
            throws TestQuizzExistException, TestQuizzNotFoundException, TestQuizzCreateTimeException {

        String testName = request.getTestName().trim();
        Timestamp startQuiz = convertTime(request.getIsStart().trim());
        Timestamp endQuiz = convertTime(request.getIsEnd().trim());
        String examTime = request.getExamTime().trim();
        String level = request.getLevel().trim();
        String type = request.getType().trim();
        Long topicId = request.getTopicId();
        LevelQuizEnum levelQuizEnum = LevelQuizEnum.getLevel(level);

        log.info("Create quizz");
        validateNewQuizzExists(EMPTY, testName);
        TestQuizzDTO testQuizzDTO = new TestQuizzDTO();
        Topic topic = topicRepository.findTopicById(topicId);
        TestQuizz quizz = testQuizzMapper.map(testQuizzDTO, topic);
        User user = userService.getCurrentUser();

        boolean isCheckCreate = isCheckCreateTime(startQuiz, endQuiz);
        if (!isCheckCreate) {
            quizz.setIsStart(startQuiz);
            quizz.setIsEnd(endQuiz);
            quizz.setTestName(testName);
            String code = generateActivationCode();
            quizz.setActivationCode(code);
            quizz.setDateCreated(Instant.now());
            Long convertTime = ConvertTimeUtils.parsePeriod(examTime);
            quizz.setExamTime(Math.toIntExact(convertTime));
            quizz.setStatus(true);
            quizz.setCreatedAt(new Date());
            quizz.setUpdatedAt(new Date());
            quizz.setCreatedBy(logged);
            quizz.setUpdatedBy(logged);
            quizz.setLevel(levelQuizEnum.getNumericValue());
            quizz.setType(type);
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
     * @param startTime The start time of the test
     * @param endTime The end time of the test
     * @return A boolean value.
     */
    private boolean isCheckCreateTime(Timestamp startTime, Timestamp endTime) throws TestQuizzCreateTimeException {
        Timestamp currTime = new Timestamp(new Date().getTime());
        if (startTime.after(endTime)) {
            log.error("Start time cannot be greater than end time!");
            throw new TestQuizzCreateTimeException("Start time cannot be greater than end time!");
        } else if (startTime.after(currTime) || endTime.after(currTime)) {
            log.error("Start time and end time cannot be greater than current time!");
            throw new TestQuizzCreateTimeException("Start time and end time cannot be greater than current time!");
        }
        return false;
    }

    /**
     * Update quizz
     *
     * @param request The request object that contains the data to be updated.
     * @return The currentQuizz is being returned.
     */
    @Override
    public TestQuizz updateQuizz(TestQuizzRequestDTO request)
            throws TestQuizzExistException, TestQuizzNotFoundException, TestQuizzCreateTimeException {

        String currentTestName = request.getCurrentTestName();
        String newTestName = request.getTestName();
        Timestamp startQuiz = convertTime(request.getIsStart());
        Timestamp endQuiz = convertTime(request.getIsEnd());
        String examTime = request.getExamTime();
        String level = request.getLevel();
        String type = request.getType();
        Long topicId = request.getTopicId();
        LevelQuizEnum levelQuizEnum = LevelQuizEnum.getLevel(level);

        log.info("Update quizz");
        Topic topic = topicRepository.findTopicById(topicId);
        TestQuizz currentQuizz = validateNewQuizzExists(currentTestName, newTestName);

        boolean isCheckCreate = isCheckCreateTime(startQuiz, endQuiz);

        if (!isCheckCreate && currentQuizz != null) {
            currentQuizz.setTestName(newTestName);
            Long convertTime = ConvertTimeUtils.parsePeriod(examTime);
            currentQuizz.setExamTime(Math.toIntExact(convertTime));
            currentQuizz.setActivationCode(currentQuizz.getActivationCode());
            currentQuizz.setIsStart(startQuiz);
            currentQuizz.setIsEnd(endQuiz);
            currentQuizz.setTopic(topic);
            currentQuizz.setCreatedAt(new Date());
            currentQuizz.setUpdatedAt(new Date());
            currentQuizz.setCreatedBy(logged);
            currentQuizz.setUpdatedBy(logged);
            currentQuizz.setLevel(levelQuizEnum.getNumericValue());
            currentQuizz.setType(type);
            quizzRepository.save(currentQuizz);
        }
        return currentQuizz;
    }

    @Override
    public TestQuizz findTestQuizzByTestName(String testName) {
        return quizzRepository.findTestQuizzByTestName(testName);
    }

    @Override
    public List<TestQuizzResponseDTO> getAllQuizz() {
        log.info("Get all quizz ::");
        List<TestQuizz> testQuizzList = quizzRepository.findAll();

        return testQuizzMapper.listQuizzToQuizzResponseDTO(testQuizzList);
    }

    @Override
    public void deleteQuizz(Long id) {
        quizzRepository.deleteById(id);
    }

    /**
     * It returns a TestQuizzDTO object with a list of QuestionDTO objects.
     *
     * @param code the activation code of the test
     * @return A list of questions
     */
    @Override
    public TestQuizzDTO findTestQuizzByActivationCode(String code, Integer amount) throws TestQuizzNotFoundException {
        Optional<TestQuizz> testQuizz = quizzRepository.findTestQuizzByActivationCode(code);
        User user = userService.getCurrentUser();

        if (testQuizz.isEmpty()) {
            log.error("No quizz found with code {}", code);
            throw new TestQuizzNotFoundException(NO_QUIZZ_TEST_FOUND_WITH_CODE + code);
        }
        Set<Question> questionList = questionRepository.randomQuestion(testQuizz.get().getId(), amount);
        Set<QuestionDTO> questionDTOList = questionMapper.questionMapToQuestionDTO(questionList);
        Integer size = questionDTOList.size();

        Map<String, Integer> totalElements = new HashMap<>();
        totalElements.put("totalElements", size);

        UserMark userMark = new UserMark();
        userMark.setStartedAt(Instant.now());
        userMark.setUser(user);
        userMark.setCreatedAt(new Date());
        userMark.setUpdatedAt(new Date());
        userMark.setCreatedBy(user.getUsername());
        userMark.setUpdatedBy(user.getUsername());
        userMark.setStatus("INCOMPLETE");
        userMark.setTestQuizz(testQuizz.get());
        userMarkRepository.save(userMark);

        return TestQuizzDTO.builder()
                .id(testQuizz.get().getId())
                .testName(testQuizz.get().getTestName())
                .examTime(testQuizz.get().getExamTime())
                .dateCreated(testQuizz.get().getDateCreated())
                .isStart(testQuizz.get().getIsStart())
                .isEnd(testQuizz.get().getIsEnd())
                .activationCode(testQuizz.get().getActivationCode())
                .level(testQuizz.get().getLevel())
                .questionDTOList(questionDTOList)
                .paging(totalElements)
                .build();
    }

    @Override
    public List<TestQuizzResponseDTO> findAllTestQuizzByTopicId(Long id) {
        log.info("Get all quizz by topic ID :: {}", id);
        List<TestQuizz> testQuizzList = quizzRepository.findTestQuizzByTopicId(id);

        return testQuizzMapper.listQuizzToQuizzResponseDTO(testQuizzList);
    }

    @Override
    public TestQuizz findTestQuizzById(Long id) {
        return quizzRepository.findTestQuizzById(id);
    }

    @Override
    public ByteArrayInputStream loadExcel(long id) {
        TestQuizz quizz = quizzRepository.findTestQuizzById(id);
        log.info("Export quizz with name: {}", quizz.getTestName());

        return ExcelHelper.quizzesToExcel(quizz);
    }

    @Override
    @Transactional
    public void lockQuizz(Long id, boolean isStatus) {
        log.info("Lock quizz");
        quizzRepository.quizzLock(id, isStatus);
    }
}
