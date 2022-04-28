package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.domain.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.websocket.server.PathParam;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {


    @Query("SELECT SUM(q.milestones) " +
           "FROM Question q, UserAnswer u " +
           "WHERE q.id = u.question.id " +
           "AND q.correctResult = u.isSelected " +
           "AND u.testQuizz.id = :id")
    float totalNumberOfCorrectAnswersByQuizzId(@Param("id") Long id);

    @Query("SELECT COUNT(q.milestones) " +
           "FROM Question q, UserAnswer u " +
           "WHERE q.id = u.question.id " +
           "AND u.testQuizz.id = :id")
    float totalNumberOfAnswersByQuizzId(@Param("id") Long id);

    @Query("SELECT SUM(q.mark) " +
           "FROM Question q, UserAnswer u " +
           "WHERE q.id = u.question.id " +
           "AND q.correctResult = u.isSelected " +
           "AND u.testQuizz.id = :id")
    float totalMarkByQuizzId(@Param("id") Long id);

}