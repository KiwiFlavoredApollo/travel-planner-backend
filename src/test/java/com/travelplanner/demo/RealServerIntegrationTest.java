package com.travelplanner.demo;

import com.travelplanner.demo.common.service.RedisService;
import com.travelplanner.demo.user.dto.LoginRequest;
import com.travelplanner.demo.user.dto.LoginResponse;
import com.travelplanner.demo.user.dto.RegisterRequest;
import com.travelplanner.demo.user.dto.UserResponse;
import com.travelplanner.demo.user.service.UserService;
import com.travelplanner.demo.travelplan.dto.TravelPlanRequest;
import com.travelplanner.demo.travelplan.dto.TravelPlanResponse;
import com.travelplanner.demo.travelplan.service.TravelPlanService;
import com.travelplanner.demo.destination.dto.DestinationRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RealServerIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TravelPlanService travelPlanService;

    @Autowired
    private RedisService redisService;

    @Test
    void testRealMariaDbAndRedisWorkflow() {
        System.out.println("=================================================");
        System.out.println("시작: [4단계] 실제 MariaDB 및 Redis 연동 통합 테스트");
        System.out.println("=================================================");

        // [1] Redis 연결 자가 진단 (RedisService의 실제 메소드로 수정)
        try {
            redisService.saveRefreshToken("integration-test-user", "test-refresh-token");
            String storedToken = redisService.getRefreshToken("integration-test-user");
            assertEquals("test-refresh-token", storedToken);
            assertTrue(redisService.validateRefreshToken("integration-test-user", "test-refresh-token"));
            
            redisService.deleteRefreshToken("integration-test-user");
            assertNull(redisService.getRefreshToken("integration-test-user"));
            System.out.println("[PASS] 1. 실제 Redis 읽기/쓰기/삭제 흐름 검증 완료");
        } catch (Exception e) {
            fail("Redis 연동 실패: " + e.getMessage());
        }

        // [2] 실제 MariaDB 회원가입 검증
        String testUserId = "realuser12";
        RegisterRequest registerReq = RegisterRequest.builder()
                .userId(testUserId)
                .password("securePass123")
                .name("실제테스터")
                .build();

        UserResponse registeredUser = userService.register(registerReq);
        assertNotNull(registeredUser);
        assertEquals(testUserId, registeredUser.getUserId());
        System.out.println("[PASS] 2. 실제 MariaDB 데이터 적재 회원가입 검증 완료 (ID: " + testUserId + ")");

        // [3] 실제 MariaDB 로그인 및 Redis 리프레시 토큰 바인딩 검증
        LoginRequest loginReq = LoginRequest.builder()
                .userId(testUserId)
                .password("securePass123")
                .build();

        LoginResponse loginRes = userService.login(loginReq);
        assertNotNull(loginRes);
        assertNotNull(loginRes.getAccessToken());
        assertNotNull(loginRes.getRefreshToken());
        System.out.println("[PASS] 3. 실제 MariaDB 조회 & Redis 토큰 적재 로그인 검증 완료");

        // [4] 실제 MariaDB 여행 계획 & 목적지 cascade 생성 검증
        DestinationRequest dest1 = DestinationRequest.builder()
                .keywords(Arrays.asList("실제경복궁", "실제광화문"))
                .build();

        TravelPlanRequest planReq = TravelPlanRequest.builder()
                .area("서울")
                .startDate(LocalDate.of(2026, 7, 9))
                .endDate(LocalDate.of(2026, 7, 10))
                .destinations(Arrays.asList(dest1))
                .build();

        TravelPlanResponse planRes = travelPlanService.createTravelPlan(testUserId, planReq);
        assertNotNull(planRes);
        assertEquals("서울", planRes.getArea());
        assertEquals(1, planRes.getDestinations().size());
        System.out.println("[PASS] 4. 실제 MariaDB 여행 계획 생성 및 Cascading 키워드 목적지 생성 완료");

        System.out.println("=================================================");
        System.out.println("종료: [4단계] 실제 MariaDB 및 Redis 연동 전체 흐름 성공!");
        System.out.println("=================================================");
    }
}
