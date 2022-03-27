package com.hqh.graduationthesisserver.repository;

import com.hqh.graduationthesisserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User findUserById(Long id);

    @Modifying
    @Query("UPDATE User u SET u.isNotLocked = ?2 WHERE u.id = ?1")
    void accountLock(Long id, boolean isNotLocked);

}
