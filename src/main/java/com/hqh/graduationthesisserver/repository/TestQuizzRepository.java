package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.domain.TestQuizz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.websocket.server.PathParam;

@Repository
public interface TestQuizzRepository extends JpaRepository<TestQuizz, Long> {

    @Query("SELECT u FROM TestQuizz u WHERE u.activationCode = :code")
    TestQuizz findTestQuizzByActivationCode(@PathParam("code") String code);

}
