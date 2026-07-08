package com.travelplanner.demo.destination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "여행지 응답")
public class DestinationResponse {

    @Schema(description = "여행지 ID", example = "1")
    private Integer id;

    @Schema(description = "날짜", example = "2026-07-08")
    private String date;

    @Schema(description = "시간", example = "14:30:00")
    private String time;

    @Schema(description = "장소명", example = "경복궁")
    private String place;
}