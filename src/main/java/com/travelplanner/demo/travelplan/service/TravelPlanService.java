package com.travelplanner.demo.travelplan.service;

import com.travelplanner.demo.travelplan.dto.TravelPlanRequest;
import com.travelplanner.demo.travelplan.dto.TravelPlanResponse;
import com.travelplanner.demo.travelplan.entity.TravelPlan;
import com.travelplanner.demo.travelplan.repository.TravelPlanRepository;
import com.travelplanner.demo.destination.dto.DestinationResponse;
import com.travelplanner.demo.destination.entity.DestinationEntity;
import com.travelplanner.demo.destination.repository.DestinationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final DestinationRepository destinationRepository;

    public TravelPlanResponse createTravelPlan(String userId, TravelPlanRequest request) {
        TravelPlan travelPlan = TravelPlan.builder()
                .userId(userId)
                .area(request.getArea())
                .build();

        TravelPlan saved = travelPlanRepository.save(travelPlan);

        if (request.getDestinations() != null && !request.getDestinations().isEmpty()) {
            List<DestinationEntity> destinations = request.getDestinations().stream()
                    .map(destReq -> DestinationEntity.builder()
                            .travelPlan(saved)
                            .place(extractPlaceFromKeywords(destReq.getKeywords()))
                            .date(request.getStartDate().toString())
                            .time(LocalTime.now().toString())
                            .build())
                    .collect(Collectors.toList());

            saved.getDestinations().addAll(destinations);
            destinationRepository.saveAll(destinations);
        }

        TravelPlan finalPlan = travelPlanRepository.save(saved);
        return toResponse(finalPlan);
    }

    @Transactional(readOnly = true)
    public List<TravelPlanResponse> getTravelPlans(String userId) {
        return travelPlanRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TravelPlanResponse getTravelPlan(Integer id, String userId) {
        TravelPlan travelPlan = travelPlanRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found: " + id));
        return toResponse(travelPlan);
    }

    public TravelPlanResponse updateTravelPlan(Integer id, String userId, TravelPlanRequest request) {
        TravelPlan travelPlan = travelPlanRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found: " + id));

        travelPlan.setArea(request.getArea());

        destinationRepository.deleteAll(travelPlan.getDestinations());
        travelPlan.getDestinations().clear();

        if (request.getDestinations() != null && !request.getDestinations().isEmpty()) {
            List<DestinationEntity> destinations = request.getDestinations().stream()
                    .map(destReq -> DestinationEntity.builder()
                            .travelPlan(travelPlan)
                            .place(extractPlaceFromKeywords(destReq.getKeywords()))
                            .date(request.getStartDate().toString())
                            .time(LocalTime.now().toString())
                            .build())
                    .collect(Collectors.toList());

            travelPlan.getDestinations().addAll(destinations);
            destinationRepository.saveAll(destinations);
        }

        TravelPlan updated = travelPlanRepository.save(travelPlan);
        return toResponse(updated);
    }

    public void deleteTravelPlan(Integer id, String userId) {
        TravelPlan travelPlan = travelPlanRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found: " + id));

        destinationRepository.deleteAll(travelPlan.getDestinations());
        travelPlanRepository.delete(travelPlan);
    }

    private String extractPlaceFromKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return "Unknown Place";
        }
        return String.join(", ", keywords);
    }

    private TravelPlanResponse toResponse(TravelPlan travelPlan) {
        List<DestinationResponse> destinations = travelPlan.getDestinations().stream()
                .map(dest -> DestinationResponse.builder()
                        .id(dest.getId())
                        .place(dest.getPlace())
                        .date(dest.getDate())
                        .time(dest.getTime())
                        .build())
                .collect(Collectors.toList());

        return TravelPlanResponse.builder()
                .id(travelPlan.getId())
                .userId(travelPlan.getUserId())
                .area(travelPlan.getArea())
                .destinations(destinations)
                .build();
    }
}