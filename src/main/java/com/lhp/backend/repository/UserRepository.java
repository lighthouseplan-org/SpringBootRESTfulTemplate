package com.lhp.backend.repository;

import com.lhp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE User SET username = :username WHERE id = :id")
    void setUsername(@Param("username") String username,@Param("id") String id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE User SET phone = :phone WHERE id = :id")
    void setPhone(@Param("phone") String phone,@Param("id") String id);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE User SET email = :email WHERE id = :id")
    void setEmail(@Param("email") String email,@Param("id") String id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User SET password = :password WHERE id = :id")
    void updatePassword(String password, String id);

    Optional<User> findByUsername(String username);

    User findByEmail(String email);
}