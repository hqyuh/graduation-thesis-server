package com.hqh.graduationthesisserver.service.impl;

import com.hqh.graduationthesisserver.domain.TestQuizz;
import com.hqh.graduationthesisserver.domain.Topic;
import com.hqh.graduationthesisserver.dto.TestQuizzDto;
import com.hqh.graduationthesisserver.exception.domain.quizz.TestQuizzExistException;
import com.hqh.graduationthesisserver.exception.domain.quizz.TestQuizzNotFoundException;
import com.hqh.graduationthesisserver.helper.quizz.ExcelHelper;
import com.hqh.graduationthesisserver.mapper.TestQuizzMapper;
import com.hqh.graduationthesisserver.repository.TestQuizzRepository;
import com.hqh.graduationthesisserver.repository.TopicRepository;
import com.hqh.graduationthesisserver.service.TestQuizzHelperService;
import com.hqh.graduationthesisserver.service.TestQuizzService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Optional;

import static com.hqh.graduationthesisserver.constant.TestQuizzConstant.*;
import static java.time.temporal.ChronoField.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class TestQuizzServiceImpl implements TestQuizzService, TestQuizzHelperService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final TestQuizzRepository quizzRepository;
    private final TopicRepository topicRepository;
    private final TestQuizzMapper testQuizzMapper;

    @Autowired
    public TestQuizzServiceImpl(TestQuizzRepository quizzRepository,
                                TopicRepository topicRepository,
                                TestQuizzMapper testQuizzMapper) {
        this.quizzRepository = quizzRepository;
        this.topicRepository = topicRepository;
        this.testQuizzMapper = testQuizzMapper;
    }

    /***
     *
     * @param currentQuizz
     * @param newQuizz
     * @return
     * @throws TestQuizzNotFoundException
     * @throws TestQuizzExistException
     */
    private TestQuizz validateNewQuizzExists (String currentQuizz,
                                              String newQuizz)
            throws TestQuizzNotFoundException, TestQuizzExistException {

        TestQuizz testQuizz = findTestQuizzByTestName(newQuizz);

        if(StringUtils.isNotBlank(currentQuizz)) {
            TestQuizz currentTest = findTestQuizzByTestName(currentQuizz);

            if(currentTest == null) {
                throw new TestQuizzNotFoundException(NO_QUIZZ_FOUND_BY_NAME + currentQuizz);
            }
            if(testQuizz != null && !currentTest.getId().equals(testQuizz.getId())) {
                throw new TestQuizzExistException(QUIZZ_ALREADY_EXISTS);
            }
            return currentTest;
        } else {
            if(testQuizz != null) {
                throw new TestQuizzExistException(QUIZZ_ALREADY_EXISTS);
            }
            return null;
        }
    }

    /***
     * random 6 characters
     *
     * @return
     */
    private String generateActivationCode() {
        return RandomStringUtils.randomNumeric(6);
    }

    /***
     * convert from dd/MM/yyyy to timestamp in sql
     *
     * @param time
     * @return timestamp
     */
    private Timestamp convertTime(String time) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter DATE_FORMAT =
                new DateTimeFormatterBuilder().appendPattern(DD_MM_YYYY_HH_MM_SS_SSS)
                                              .parseDefaulting(HOUR_OF_DAY, now.getHour())
                                              .parseDefaulting(MINUTE_OF_HOUR, now.getMinute())
                                              .parseDefaulting(SECOND_OF_MINUTE, now.getSecond())
                                              .parseDefaulting(NANO_OF_SECOND, now.getNano())
                                              .toFormatter()
                                              .withZone(ZoneId.of(ZONE_ID));
        LocalDateTime localDateTime = LocalDateTime.from(DATE_FORMAT.parse(time));

        return Timestamp.valueOf(localDateTime);
    }

    /***
     *
     * @param testName
     * @param examTime
     * @param isStart
     * @param isEnd
     * @return quizz
     * @throws TestQuizzExistException
     * @throws TestQuizzNotFoundException
     */
    @Override
    public TestQuizz createQuizz(String testName,
                                 Integer examTime,
                                 String isStart,
                                 String isEnd,
                                 Long topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException {

        validateNewQuizzExists(EMPTY, testName);
        TestQuizzDto testQuizzDto = new TestQuizzDto();
        Topic topic = topicRepository.findTopicById(topicId);
        TestQuizz quizz = testQuizzMapper.map(testQuizzDto, topic);
        quizz.setTestName(testName);
        String code = generateActivationCode();
        quizz.setActivationCode(code);
        quizz.setDateCreated(Instant.now());
        quizz.setExamTime(examTime);
        quizz.setIsStart(convertTime(isStart));
        quizz.setIsEnd(convertTime(isEnd));
        LOGGER.info(CODE + code + IS_FOR_TEST_NAME + testName);
        quizzRepository.save(quizz);

        return quizz;
    }

    /***
     *
     * @param currentTestName
     * @param newTestName
     * @param examTime
     * @param isStart
     * @param isEnd
     * @param topicId
     * @return
     * @throws TestQuizzExistException
     * @throws TestQuizzNotFoundException
     */
    @Override
    public TestQuizz updateQuizz(String currentTestName,
                                 String newTestName,
                                 Integer examTime,
                                 String isStart,
                                 String isEnd,
                                 Long topicId)
            throws TestQuizzExistException, TestQuizzNotFoundException {
        Topic topic = topicRepository.findTopicById(topicId);
        TestQuizz currentQuizz = validateNewQuizzExists(currentTestName, newTestName);
        currentQuizz.setTestName(newTestName);
        currentQuizz.setExamTime(examTime);
        currentQuizz.setActivationCode(currentQuizz.getActivationCode());
        currentQuizz.setIsStart(convertTime(isStart));
        currentQuizz.setIsEnd(convertTime(isEnd));
        currentQuizz.setTopic(topic);
        quizzRepository.save(currentQuizz);

        return currentQuizz;
    }

    /***
     *
     * @param testName
     * @return
     */
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
    public Optional<TestQuizz> findTestQuizzByActivationCode(String code) throws TestQuizzNotFoundException {
        return Optional.ofNullable(quizzRepository
                .findTestQuizzByActivationCode(code)
                .orElseThrow(() -> new TestQuizzNotFoundException(NO_QUIZZ_TEST_FOUND_WITH_CODE + code)));
    }

    @Override
    public List<TestQuizz> findAllTestQuizzByTopicId(Long id) {
        return quizzRepository.findTestQuizzByTopicId(id);
    }

    @Override
    public TestQuizz findTestQuizzById(Long id) {
        return quizzRepository.findTestQuizzById(id);
    }

    /***
     * export quiz by id
     *
     * @param id
     * @return
     */
    @Override
    public ByteArrayInputStream loadExcel(long id) {
        TestQuizz quizz = quizzRepository.findTestQuizzById(id);

        return ExcelHelper.quizzesToExcel(quizz);
    }
}
