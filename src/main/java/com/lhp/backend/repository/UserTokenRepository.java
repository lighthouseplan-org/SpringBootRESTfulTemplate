package com.lhp.backend.repository;

import com.lhp.backend.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserTokenRepository extends JpaRepository<UserToken, String> {

    UserToken findByToken(String token);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM UserToken WHERE userId = :userId")
    void deleteByUserId(String userId);
}