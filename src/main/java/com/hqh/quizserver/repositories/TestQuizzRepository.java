package com.hqh.quizserver.repositories;

import com.hqh.quizserver.entities.TestQuizz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestQuizzRepository extends JpaRepository<TestQuizz, Long> {

    @Query("SELECT u "
         + "FROM TestQuizz u "
         + "WHERE u.activationCode = :code")
    TestQuizz findTestQuizzByActivationCode(@Param("code") String code);

    @Query("SELECT u "
         + "FROM TestQuizz u "
         + "WHERE u.testName = :name")
    TestQuizz findTestQuizzByTestName(@Param("name") String name);

    List<TestQuizz> findTestQuizzByTopicId(Long id);

    TestQuizz findTestQuizzById(Long id);

    @Modifying
    @Query("UPDATE TestQuizz t "
         + "SET t.isStatus = ?2 "
         + "WHERE t.id = ?1")
    void quizzLock(Long id, boolean isStatus);

    @Query("SELECT t "
         + "FROM TestQuizz t "
         + "WHERE CONCAT(t.testName, ' ', t.activationCode) LIKE %?1%")
    List<TestQuizz> searchTestQuizzes(String keyword);
}
