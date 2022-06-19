package com.hqh.quizserver.repositories;

import com.hqh.quizserver.entities.TestQuizz;
import com.hqh.quizserver.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;


import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class TestQuizzRepositoryTest {

    private final TestQuizzRepository quizzRepository;
    private final TestEntityManager entityManager;

    @Autowired
    TestQuizzRepositoryTest(TestQuizzRepository quizzRepository,
                            TestEntityManager entityManager) {
        this.quizzRepository = quizzRepository;
        this.entityManager = entityManager;
    }

    @Test
    public void testCreateQuizz() {
        User userA = entityManager.find(User.class, 44L);
        TestQuizz quizz = new TestQuizz("javascript", "454645");
        quizz.addUser(userA);

        TestQuizz savedQuizz = quizzRepository.save(quizz);
        assertThat(savedQuizz.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateNewQuizzWithNUser() {
        User userA = entityManager.find(User.class, 44L);
        User userB = entityManager.find(User.class, 11L);

        TestQuizz quizz = new TestQuizz("typescript", "568008");

        quizz.addUser(userA);
        quizz.addUser(userB);

        TestQuizz savedQuizz = quizzRepository.save(quizz);
        assertThat(savedQuizz.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllQuizz() {
        List<TestQuizz> listQuizz = quizzRepository.findAll();
        listQuizz.forEach(System.out::println);
    }

    @Test
    public void testGetQuizzById() {
        TestQuizz testQuizz = quizzRepository.findById(4L)
                                             .get();
        System.out.println(testQuizz);

        assertThat(testQuizz).isNotNull();
    }

    @Test
    public void testGetQuizzByCode() {
        Optional<TestQuizz> testQuizz = quizzRepository
                .findTestQuizzByActivationCode("446614");

        System.out.println(testQuizz);

        assertThat(testQuizz).isNotNull();
    }

    @Test
    public void testUpdateQuizz() {
        TestQuizz testQuizz = quizzRepository.findById(17L)
                                             .get();
        testQuizz.setActivationCode("446614");
        testQuizz.setDateCreated(Instant.now());
        testQuizz.setExamTime(1000);

        quizzRepository.save(testQuizz);
    }

    @Test
    public void testDeleteQuizz() {
        Long testID = 6L;
        quizzRepository.deleteById(testID);
    }

    @Test
    public void testGetQuizzByTestName() {
        TestQuizz testQuizz = quizzRepository
                .findTestQuizzByTestName("java");
        System.out.println(testQuizz);

        assertThat(testQuizz).isNotNull();
    }

    @Test
    public void testConvertTime() {
        String date = "10/05/2022";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter DATE_FORMAT =
                new DateTimeFormatterBuilder().appendPattern("dd/MM/yyyy[[HH][:mm][:ss][.SSS]]")
                                              .parseDefaulting(ChronoField.HOUR_OF_DAY, now.getHour())
                                              .parseDefaulting(ChronoField.MINUTE_OF_HOUR, now.getMinute())
                                              .parseDefaulting(ChronoField.SECOND_OF_MINUTE, now.getSecond())
                                              .parseDefaulting(ChronoField.NANO_OF_SECOND, now.getNano())
                                              .toFormatter()
                                              .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime localDateTime = LocalDateTime.from(DATE_FORMAT.parse(date));
        Timestamp ts = Timestamp.valueOf(localDateTime);

        System.out.println(ts);

    }

    @Test
    public void testGetAllQuizzByTopicId() {
        List<TestQuizz> listQuizz = quizzRepository.findTestQuizzByTopicId(1L);
        listQuizz.forEach(System.out::println);
    }

}