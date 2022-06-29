package com.hqh.quizserver.repositories;

import com.hqh.quizserver.entities.Question;
import com.hqh.quizserver.entities.TestQuizz;
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
class QuestionRepositoryTest {

    private final QuestionRepository questionRepository;
    private final TestEntityManager entityManager;

    @Autowired
    QuestionRepositoryTest(QuestionRepository questionRepository,
                           TestEntityManager entityManager) {
        this.questionRepository = questionRepository;
        this.entityManager = entityManager;
    }

    @Test
    public void testCreateQuestion() {
        TestQuizz quizzID = entityManager.find(TestQuizz.class, 4L);
        Question question = new Question(
                "How can you achieve runtime polymorphism in Java?",
                "",
                "method overloading",
                "method overrunning",
                "method overriding",
                "method calling",
                "C",
                2.0F,
                Instant.now(),
                quizzID
        );

        Question savedQuestion = questionRepository.save(question);
        assertThat(savedQuestion.getId()).isGreaterThan(0);

    }

    @Test
    public void testDeleteQuestion() {
        Long questionID = 4L;
        questionRepository.deleteById(questionID);
    }

    @Test
    public void testUpdateQuestion() {
        Question question = questionRepository.findById(3L).get();

        question.setMark(5);
        question.setDateCreated(Instant.now());

        questionRepository.save(question);
    }

    @Test
    public void testGetQuestionById() {
        Question question = questionRepository.findById(3L).get();

        System.out.println(question);

        assertThat(question).isNotNull();
    }

    @Test
    public void testGetQuestionByQuizzId() {
        List<Question> question = questionRepository.findAllByQuizzId(4L);
        question.forEach(System.out::println);
    }

    @Test
    public void testQuestionsPagination() {
        List<Question> questions = questionRepository.questionsPagination(1,  2);
        questions.forEach(System.out::println);
    }

    @Test
    public void testCountQuestions() {
        int sum = questionRepository.getTotalNumberOfRecords();
        System.out.println(sum);
    }

}