package com.hqh.quizserver.repositories;

import com.hqh.quizserver.entities.UserMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMarkRepository extends JpaRepository<UserMark, Long> {

    @Query("SELECT u "
         + "FROM UserMark u "
         + "WHERE u.user.username = :username")
    List<UserMark> findByAllUsername(@Param("username") String username);

    List<UserMark> findByTestQuizzId(Long id);

    @Query("SELECT u "
         + "FROM UserMark u "
         + "WHERE u.testQuizz.id = :id "
         + "ORDER BY u.mark DESC ")
    List<UserMark> getMarkTop3(@Param("id") Long id);

    @Modifying
    @Query("UPDATE UserMark u "
         + "SET u.pointLock = ?2 "
         + "WHERE u.user.id = ?1")
    void markLock(Long userId, boolean isLock);

}