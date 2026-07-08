package com.travelplanner.demo.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final long REFRESH_TOKEN_TTL = 60 * 60 * 24 * 7; // 7일
    private final StringRedisTemplate stringRedisTemplate;

    public void saveRefreshToken(String userId, String refreshToken) {
        stringRedisTemplate.opsForValue()
                .set("RT:" + userId, refreshToken, REFRESH_TOKEN_TTL, TimeUnit.SECONDS);
    }

    public void deleteRefreshToken(String userId) {
        stringRedisTemplate.delete("RT:" + userId);
    }

    public String getRefreshToken(String userId) {
        return stringRedisTemplate.opsForValue().get("RT:" + userId);
    }

    public boolean validateRefreshToken(String userId, String refreshToken) {
        String storedToken = getRefreshToken(userId);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}