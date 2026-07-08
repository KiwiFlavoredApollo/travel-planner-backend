package com.travelplanner.demo.travelplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelplanner.demo.travelplan.entity.TravelPlan;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Integer> {

    List<TravelPlan> findByUserIdOrderByIdDesc(String userId);

    Optional<TravelPlan> findByIdAndUserId(Integer id, String userId);
}