package com.vcube.securebanking.controller;
 
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.vcube.securebanking.dto.AuthResponse;
import com.vcube.securebanking.dto.LoginRequest;
import com.vcube.securebanking.dto.RegisterRequest;
import com.vcube.securebanking.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String token) {
        return authService.refreshToken(token);
    }
}