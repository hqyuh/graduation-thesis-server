package com.hqh.quizserver.repositories;

import com.hqh.quizserver.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT u "
         + "FROM Question u "
         + "WHERE u.testQuizz.id = :id")
    List<Question> findAllByQuizzId(@Param("id") Long id);

    Question findQuestionById(Long id);

    @Query(nativeQuery = true,
           value = "SELECT * FROM tbl_question q LIMIT :page, :size")
    List<Question> questionsPagination(@Param("page") Integer page,
                                       @Param("size") Integer size);

    @Query(nativeQuery = true,
           value = "SELECT COUNT(id) FROM tbl_question q")
    Integer getTotalNumberOfRecords();

}
