package com.travelplanner.demo.destination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "여행지 요청 (키워드 기반)")
public class DestinationRequest {

    @Schema(description = "검색 키워드 리스트", example = "[\"경복궁\", \"맛집\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Keywords list cannot be empty")
    private List<String> keywords;
}