package com.travelplanner.demo;

import com.travelplanner.demo.destination.entity.DestinationEntity;
import com.travelplanner.demo.destination.repository.DestinationRepository;
import com.travelplanner.demo.travelplan.entity.TravelPlan;
import com.travelplanner.demo.travelplan.repository.TravelPlanRepository;
import com.travelplanner.demo.user.entity.UserEntity;
import com.travelplanner.demo.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class DbRepositoryQueryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Test
    void testUserEntityQueries() {
        System.out.println(">>>> [DbRepositoryQueryTest] USER_TBL CRUD 쿼리 검증 시작");

        // GIVEN
        UserEntity user = UserEntity.builder()
                .userId("querytester")
                .password("hashedPassword")
                .name("쿼리테스터")
                .build();

        // WHEN (Create)
        UserEntity savedUser = userRepository.save(user);

        // THEN (Read / Custom Query)
        assertNotNull(savedUser);
        assertEquals("querytester", savedUser.getUserId());

        boolean exists = userRepository.existsByUserId("querytester");
        assertTrue(exists);

        boolean notExists = userRepository.existsByUserId("unknownuser");
        assertFalse(notExists);

        System.out.println("[PASS] USER_TBL existsByUserId 쿼리 작동 검증 성공");
    }

    @Test
    void testTravelPlanAndDestinationQueries() {
        System.out.println(">>>> [DbRepositoryQueryTest] TravelPlan 및 Destination 연관 쿼리 검증 시작");

        // GIVEN
        String testUserId = "planowner";
        TravelPlan plan = TravelPlan.builder()
                .userId(testUserId)
                .area("제주도")
                .build();

        DestinationEntity dest1 = DestinationEntity.builder()
                .date("2026-07-09")
                .time("10:00")
                .place("협재해수욕장")
                .build();

        DestinationEntity dest2 = DestinationEntity.builder()
                .date("2026-07-09")
                .time("14:00")
                .place("한라산")
                .build();

        plan.addDestination(dest1);
        plan.addDestination(dest2);

        // WHEN
        TravelPlan savedPlan = travelPlanRepository.save(plan);

        // THEN (TravelPlan Custom Query)
        assertNotNull(savedPlan.getId());
        assertEquals(2, savedPlan.getDestinations().size());

        List<TravelPlan> userPlans = travelPlanRepository.findByUserIdOrderByIdDesc(testUserId);
        assertEquals(1, userPlans.size());
        assertEquals("제주도", userPlans.get(0).getArea());

        Optional<TravelPlan> foundPlan = travelPlanRepository.findByIdAndUserId(savedPlan.getId(), testUserId);
        assertTrue(foundPlan.isPresent());
        assertEquals("제주도", foundPlan.get().getArea());

        Optional<TravelPlan> notFoundPlan = travelPlanRepository.findByIdAndUserId(savedPlan.getId(), "wronguser");
        assertFalse(notFoundPlan.isPresent());

        System.out.println("[PASS] Travel_Plan_TBL 커스텀 조회 쿼리 검증 성공");

        // THEN (Destination Custom Query)
        List<DestinationEntity> sortedDestinations = destinationRepository.findByTravelPlanOrderByDateAscTimeAsc(savedPlan);
        assertEquals(2, sortedDestinations.size());
        assertEquals("협재해수욕장", sortedDestinations.get(0).getPlace());
        assertEquals("한라산", sortedDestinations.get(1).getPlace());

        System.out.println("[PASS] Destination_TBL Date/Time 순 정렬 쿼리 검증 성공");
    }
}
