package com.sks.erpbss.be.sp.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "환율 조회 요청 DTO")
public class ExchangeRDto {

    @Schema(description = "기준 통화 (예: USD)", example = "USD")
    private String base;

    @Schema(description = "대상 통화 (예: KRW)", example = "KRW")
    private String target;
}
