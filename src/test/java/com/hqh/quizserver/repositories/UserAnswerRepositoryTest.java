package com.hqh.quizserver.repositories;

import com.hqh.quizserver.dto.IReviewAnswerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class UserAnswerRepositoryTest {

    private final UserAnswerRepository userAnswerRepository;

    @Autowired
    UserAnswerRepositoryTest(UserAnswerRepository userAnswerRepository) {
        this.userAnswerRepository = userAnswerRepository;
    }

    @Test
    void testGetQuestionByQuizzId() {
        List<IReviewAnswerResponse> reviewAnswerResponseDtos = userAnswerRepository.reviewAnswerUser(1L, 1L);
        reviewAnswerResponseDtos.forEach(System.out::println);
    }
}