package com.travelplanner.demo.travelplan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import com.travelplanner.demo.destination.dto.DestinationRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "여행 계획 생성/수정 요청")
public class TravelPlanRequest {

    @Schema(description = "여행 지역", example = "서울", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Area is required")
    private String area;

    @Schema(description = "여행 시작일", example = "2026-07-08", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Schema(description = "여행 종료일", example = "2026-07-14", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Schema(description = "여행지 목록")
    private List<DestinationRequest> destinations;
}