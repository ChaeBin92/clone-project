package com.sks.erpbss.be.sp.dashboard.service;

import com.sks.erpbss.be.sp.dashboard.dto.ExchangeRDto;
import com.sks.erpbss.be.sp.dashboard.dto.ExchangeSDto;

/**
 * 대시보드 관련 비즈니스 로직 인터페이스.
 */
public interface DashboardService {

    /**
     * 외부 API를 통해 환율을 조회하고 가공하여 반환합니다.
     *
     * @param rDto 환율 조회 요청 DTO
     * @return 환율 조회 응답 DTO
     */
    ExchangeSDto getExchangeRateData(ExchangeRDto rDto);
}
