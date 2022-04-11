package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.domain.UserMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMarkRepository extends JpaRepository<UserMark, Long> {
}