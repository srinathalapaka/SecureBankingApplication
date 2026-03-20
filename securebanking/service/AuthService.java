package com.vcube.securebanking.service;

 
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vcube.securebanking.config.JwtUtil;
import com.vcube.securebanking.dto.AuthResponse;
import com.vcube.securebanking.dto.LoginRequest;
import com.vcube.securebanking.dto.RegisterRequest;
import com.vcube.securebanking.model.RefreshToken;
import com.vcube.securebanking.model.Role;
import com.vcube.securebanking.model.User;
import com.vcube.securebanking.repository.RefreshTokenRepository;
import com.vcube.securebanking.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // REGISTER
    public String register(RegisterRequest request) {

        if(userRepo.findByEmail(request.getEmail()).isPresent())
            throw new RuntimeException("Email already exists");

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_CUSTOMER);

        userRepo.save(user);
        return "User Registered Successfully";
    }

    // LOGIN
    public AuthResponse login(LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        String accessToken = jwtUtil.generateToken(user.getEmail());
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    // REFRESH TOKEN
    public AuthResponse refreshToken(String token) {

        RefreshToken refreshToken = refreshRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Refresh token expired");

        String accessToken = jwtUtil.generateToken(refreshToken.getUser().getEmail());

        return new AuthResponse(accessToken, token);
    }

    // CREATE REFRESH TOKEN
    private String createRefreshToken(User user) {

        refreshRepo.findByUser(user).ifPresent(refreshRepo::delete);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshRepo.save(refreshToken);

        return refreshToken.getToken();
    }
}