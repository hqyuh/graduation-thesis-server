package com.hqh.graduationthesisserver.repository;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest {

    @Test
    public void testEncodePassword() {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "123456789";
        String passInData = "$2a$10$pG5PryrFl/vb/y5boDLt4O1zCjEq9UkBahusLi5nSUHUDT36guJku";
        boolean matches = passwordEncoder.matches(rawPassword, passInData);
        assertThat(matches).isTrue();

    }

}