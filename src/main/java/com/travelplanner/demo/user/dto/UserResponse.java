package com.travelplanner.demo.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Schema(description = "사용자 응답")
public class UserResponse {

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "사용자 ID", example = "user123")
    private String userId;

    @Schema(description = "비밀번호 (마스킹됨)", example = "***")
    private String password;
}