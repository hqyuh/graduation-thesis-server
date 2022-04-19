package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.domain.UserMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMarkRepository extends JpaRepository<UserMark, Long> {

    @Query("SELECT u " +
           "FROM UserMark u " +
           "WHERE u.user.username = :username")
    List<UserMark> findByAllUsername(@Param("username") String username);

    List<UserMark> findByTestQuizzId(Long id);

    @Query("SELECT u " +
           "FROM UserMark u " +
           "WHERE u.testQuizz.id = :id " +
           "ORDER BY u.mark DESC ")
    List<UserMark> getMarkTop3(@Param("id") Long id);

}