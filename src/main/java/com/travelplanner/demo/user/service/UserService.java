package com.travelplanner.demo.user.service;

import com.travelplanner.demo.common.service.RedisService;
import com.travelplanner.demo.common.token.JwtProvider;
import com.travelplanner.demo.user.dto.LoginRequest;
import com.travelplanner.demo.user.dto.LoginResponse;
import com.travelplanner.demo.user.dto.RegisterRequest;
import com.travelplanner.demo.user.dto.UserResponse;
import com.travelplanner.demo.user.entity.UserEntity;
import com.travelplanner.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("User ID already exists: " + request.getUserId());
        }

        UserEntity user = UserEntity.builder()
                .userId(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        UserEntity saved = userRepository.save(user);

        return UserResponse.builder()
                .userId(saved.getUserId())
                .name(saved.getName())
                .password("***")
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Access Token & Refresh Token 생성
        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());

        // Refresh Token을 Redis에 저장
        redisService.saveRefreshToken(user.getUserId(), refreshToken);

        return LoginResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String accessToken) {
        String userId = jwtProvider.getUserIdFromAccessToken(accessToken);
        redisService.deleteRefreshToken(userId);
    }

    public UserResponse getUserById(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .password("***")
                .build();
    }
}