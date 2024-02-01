package com.bitmutex.shortener;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    UserEntity findByEmail(String email);

    @NotNull
    Optional<UserEntity> findById(@NotNull Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.email = :email AND u.username != :username")
    boolean existsByEmailAndUsernameNot(@Param("email") String email, @Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.phoneNumber = :phoneNumber AND u.username != :username")
    boolean existsByPhoneNumberAndUsernameNot(@Param("phoneNumber") String phoneNumber, @Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.username = :username AND u.id <> :currentUserId")
    boolean existsByUsernameExcludingCurrentUser(@Param("username") String username, @Param("currentUserId") Long currentUserId);


    UserEntity findByResetToken(String resetToken);

}
