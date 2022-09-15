package com.hqh.quizserver.repository;

import com.hqh.quizserver.entity.UserMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMarkRepository extends JpaRepository<UserMark, Long> {

    @Query("SELECT u FROM UserMark u WHERE u.user.username = :username")
    List<UserMark> findByAllUsername(@Param("username") String username);

    List<UserMark> findByTestQuizzId(Long id);

    @Query(value = "SELECT * FROM user_mark um WHERE um.quizz_id = :quizzId ORDER BY um.mark DESC LIMIT 3",
           nativeQuery = true)
    List<UserMark> getMarkTop3(@Param("quizzId") Long quizzId);

    @Modifying
    @Query("UPDATE UserMark u SET u.pointLock = ?2 WHERE u.user.id = ?1")
    void markLock(Long userId, boolean isLock);

}