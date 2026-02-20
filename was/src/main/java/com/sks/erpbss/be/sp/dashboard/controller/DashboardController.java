package com.sks.erpbss.be.sp.dashboard.controller;

import com.sks.erpbss.be.sp.cmmn.CommonResponse;
import com.sks.erpbss.be.sp.dashboard.dto.ExchangeRDto;
import com.sks.erpbss.be.sp.dashboard.dto.ExchangeSDto;
import com.sks.erpbss.be.sp.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "대시보드 관련 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/exchange")
    @Operation(summary = "환율 정보 조회", description = "기준 통화(base)와 대상 통화(target)를 입력받아 실시간 환율을 조회합니다.")
    public CommonResponse<ExchangeSDto> getExchangeRate(@ModelAttribute ExchangeRDto rDto) {
        log.info("GET /api/dashboard/exchange called with base={}, target={}", rDto.getBase(), rDto.getTarget());
        ExchangeSDto sDto = dashboardService.getExchangeRateData(rDto);
        return CommonResponse.success(HttpStatus.OK.value(), sDto);
    }
}
