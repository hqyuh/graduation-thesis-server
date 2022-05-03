package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.dto.ReviewAnswerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    public void testGetQuestionByQuizzId() {
        List<ReviewAnswerDto> reviewAnswerDtos = userAnswerRepository.reviewAnswerUser(4L, 11L);
        reviewAnswerDtos.forEach(System.out::println);
    }
}