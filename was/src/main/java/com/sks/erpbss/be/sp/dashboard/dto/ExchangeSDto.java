package com.sks.erpbss.be.sp.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "환율 조회 응답 DTO")
public class ExchangeSDto {

    @Schema(description = "기준 통화")
    private String base;

    @Schema(description = "대상 통화")
    private String target;

    @Schema(description = "환율")
    private BigDecimal rate;
}
