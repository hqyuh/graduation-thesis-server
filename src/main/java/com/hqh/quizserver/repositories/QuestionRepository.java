package com.hqh.quizserver.repositories;

import com.hqh.quizserver.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT u FROM Question u WHERE u.testQuizz.id = :id ORDER BY u.id")
    List<Question> findAllByQuizzId(@Param("id") Long id);

    Question findQuestionById(Long id);

    @Query(nativeQuery = true,
           value = "SELECT * FROM question q LIMIT :page, :size")
    List<Question> questionsPagination(@Param("page") Integer page,
                                       @Param("size") Integer size);

    @Query(nativeQuery = true,
           value = "SELECT COUNT(id) FROM question q")
    Integer getTotalNumberOfRecords();

    @Query(value = "SELECT q.* FROM question q WHERE quizz_id = :quizzId ORDER BY random() LIMIT :amount", nativeQuery = true)
    List<Question> randomQuestion(@Param("quizzId") Long quizzId, @Param("amount") Integer amount);

    List<Question> findAllByTestQuizzId(Long id);

}
