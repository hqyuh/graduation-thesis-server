package com.hqh.graduationthesisserver.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class UserMarkRepositoryTest {

    private final UserMarkRepository userMarkRepository;

    @Autowired
    UserMarkRepositoryTest(UserMarkRepository userMarkRepository) {
        this.userMarkRepository = userMarkRepository;
    }

    @Test
    void markLock() {
        userMarkRepository.markLock(11L, true);
    }
}