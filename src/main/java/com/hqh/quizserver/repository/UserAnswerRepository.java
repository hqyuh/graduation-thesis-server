package com.hqh.quizserver.repository;

import com.hqh.quizserver.entity.UserAnswer;
import com.hqh.quizserver.dto.IReviewAnswerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {


    @Query("SELECT SUM(q.milestones)"
         + "FROM Question q "
         + "INNER JOIN UserAnswer u "
         + "ON q.id = u.question.id "
         + "AND q.correctResult = u.isSelected "
         + "AND u.testQuizz.id = :id")
    float totalNumberOfCorrectAnswersByQuizzId(@Param("id") Long id);

    @Query("SELECT COUNT(q.milestones)"
         + "FROM Question q "
         + "INNER JOIN UserAnswer u "
         + "ON q.id = u.question.id "
         + "AND u.testQuizz.id = :id")
    float totalNumberOfAnswersByQuizzId(@Param("id") Long id);

    @Query("SELECT SUM(q.mark) "
         + "FROM Question q "
         + "INNER JOIN UserAnswer u "
         + "ON q.id = u.question.id "
         + "AND u.testQuizz.id = :quizzId "
         + "AND u.user.id = :userId "
         + "AND u.isCorrect = true ")
    float totalMarkByQuizzId(@Param("quizzId") Long quizzId, @Param("userId") Long userId);

    @Query("SELECT "
         + "q.topicQuestion AS topicQuestion, "
         + "q.answerA AS answerA, "
         + "q.answerB AS answerB, "
         + "q.answerC AS answerC, "
         + "q.answerD AS answerD, "
         + "ua.isSelected AS isSelected, "
         + "q.correctResult AS correctResult, "
         + "ua.shortAnswer AS shortAnswer, "
         + "q.correctEssay AS correctEssay, "
         + "ua.isCorrect AS isCorrect "
         + "FROM TestQuizz tq "
         + "INNER JOIN Question q ON q.testQuizz.id = tq.id "
         + "INNER JOIN UserAnswer ua ON q.id = ua.question.id "
         + "AND tq.id = :quizzId AND ua.user.id = :userId")
    List<IReviewAnswerResponse> reviewAnswerUser(@Param("quizzId") Long quizzId, @Param("userId") Long userId);

    @Query("SELECT ua "
         + "FROM UserAnswer ua "
         + "WHERE ua.question.id = :questionId "
         + "AND ua.user.id = :userId")
    List<UserAnswer> getAllUserAnswerByQuestionIdAndUserId(Long questionId, Long userId);

}