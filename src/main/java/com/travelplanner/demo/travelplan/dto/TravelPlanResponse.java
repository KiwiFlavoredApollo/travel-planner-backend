package com.travelplanner.demo.travelplan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import com.travelplanner.demo.destination.dto.DestinationResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "여행 계획 응답")
public class TravelPlanResponse {

    @Schema(description = "여행 계획 ID", example = "1")
    private Integer id;

    @Schema(description = "사용자 ID", example = "user123")
    private String userId;

    @Schema(description = "여행 지역", example = "서울")
    private String area;

    @Schema(description = "여행 시작일", example = "2026-07-08")
    private LocalDate startDate;

    @Schema(description = "여행 종료일", example = "2026-07-14")
    private LocalDate endDate;

    @Schema(description = "여행지 목록")
    private List<DestinationResponse> destinations;
}