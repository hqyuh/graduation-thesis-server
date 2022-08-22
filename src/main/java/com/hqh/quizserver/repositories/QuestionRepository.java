package com.hqh.quizserver.repositories;

import com.hqh.quizserver.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT u FROM Question u WHERE u.testQuizz.id = :id")
    List<Question> findAllByQuizzId(@Param("id") Long id);

    Question findQuestionById(Long id);

    @Query(value = "SELECT * FROM question q LIMIT :page :size", nativeQuery = true)
    List<Question> questionsPagination(@Param("page") Integer page,
                                       @Param("size") Integer size);

    @Query(value = "SELECT COUNT(id) FROM question q", nativeQuery = true)
    Integer getTotalNumberOfRecords();

    @Query(value = "SELECT q.id, q.topic_question, q.question_image_url, q.answer_a, q.answer_b, q.answer_c, q.answer_d, "
            + "q.correct_result, q.correct_essay, q.mark, q.milestones, q.quizz_id FROM question q WHERE quizz_id = :quizzId ORDER BY random() LIMIT 3", nativeQuery = true)
    List<Question> randomQuestion(@Param("quizzId") Long quizzId);

}
