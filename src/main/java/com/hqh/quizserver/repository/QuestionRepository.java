package com.hqh.quizserver.repository;

import com.hqh.quizserver.entity.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QuestionRepository extends PagingAndSortingRepository<Question, Long> {

    List<Question> getAllByTestQuizzIdOrderById(Long id);

    Question findQuestionById(Long id);

    @Query(value = "SELECT * FROM question q LIMIT :page, :size", nativeQuery = true)
    List<Question> questionsPagination(@Param("page") Integer page, @Param("size") Integer size);

    @Query(value = "SELECT q.* FROM question q WHERE quizz_id = :quizzId ORDER BY random() LIMIT :amount", nativeQuery = true)
    Set<Question> randomQuestion(@Param("quizzId") Long quizzId, @Param("amount") Integer amount);

    List<Question> findAllByTestQuizzId(Long id);

}
