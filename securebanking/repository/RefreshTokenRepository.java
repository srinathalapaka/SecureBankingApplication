package com.vcube.securebanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vcube.securebanking.model.RefreshToken;
import com.vcube.securebanking.model.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}