package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.domain.Question;
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

}
