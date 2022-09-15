package com.hqh.quizserver.repositories;

import com.hqh.quizserver.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {


    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void testEncodePassword() {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "123456789";
        String passInData = "$2a$10$pG5PryrFl/vb/y5boDLt4O1zCjEq9UkBahusLi5nSUHUDT36guJku";
        boolean matches = passwordEncoder.matches(rawPassword, passInData);
        assertThat(matches).isTrue();

    }

    @Test
    public void testLockAccount() {
        Long id = 44L;
        userRepository.accountLock(id, false);
    }

    @Test
    public void testUnLockAccount() {
        Long id = 44L;
        userRepository.accountLock(id, true);
    }

}