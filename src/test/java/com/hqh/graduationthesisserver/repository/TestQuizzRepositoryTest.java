package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.domain.TestQuizz;
import com.hqh.graduationthesisserver.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;


import java.time.Instant;
import java.util.List;

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
        TestQuizz testQuizz = quizzRepository
                .findTestQuizzByActivationCode("568008");

        System.out.println(testQuizz);

        assertThat(testQuizz).isNotNull();
    }

    @Test
    public void testUpdateQuizz() {
        TestQuizz testQuizz = quizzRepository.findById(9L)
                                             .get();
        testQuizz.setActivationCode("568008");
        testQuizz.setDateCreated(Instant.now());
        testQuizz.setTestName("typescript");
        testQuizz.setExamTime(600);

        quizzRepository.save(testQuizz);
    }

    @Test
    public void testDeleteQuizz() {
        Long testID = 6L;
        quizzRepository.deleteById(testID);
    }

}