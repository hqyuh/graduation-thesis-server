package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.domain.TestQuizz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestQuizzRepository extends JpaRepository<TestQuizz, Long> {

    @Query("SELECT u FROM TestQuizz u WHERE u.activationCode = :code")
    Optional<TestQuizz> findTestQuizzByActivationCode(@Param("code") String code);

    @Query("SELECT u FROM TestQuizz u WHERE u.testName = :name")
    TestQuizz findTestQuizzByTestName(@Param("name") String name);

    List<TestQuizz> findTestQuizzByTopicId(Long id);

    TestQuizz findTestQuizzById(Long id);

    @Modifying
    @Query("UPDATE TestQuizz t SET t.isStatus = ?2 WHERE t.id = ?1")
    void quizzLock(Long id, boolean isStatus);

}
