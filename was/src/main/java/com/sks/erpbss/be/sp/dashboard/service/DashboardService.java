package com.sks.erpbss.be.sp.dashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sks.erpbss.be.sp.cmmn.CommonException;
import com.sks.erpbss.be.sp.cmmn.client.ExchangeRateClient;
import com.sks.erpbss.be.sp.dashboard.dto.ExchangeRDto;
import com.sks.erpbss.be.sp.dashboard.dto.ExchangeSDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExchangeRateClient exchangeRateClient;
    private final ObjectMapper objectMapper;

    public ExchangeSDto getExchangeRateData(ExchangeRDto rDto) {
        String base = rDto.getBase();
        String target = rDto.getTarget();

        if (base == null || base.isBlank() || target == null || target.isBlank()) {
            throw new CommonException(400, "기준 통화(base)와 대상 통화(target)는 필수입니다.");
        }

        base = base.trim().toUpperCase(Locale.ROOT);
        target = target.trim().toUpperCase(Locale.ROOT);

        try {
            log.info("Calling ExchangeRateClient for base: {}", base);
            String responseBody = exchangeRateClient.getExchangeRate(base);
            log.debug("API Response: {}", responseBody);

            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode targetNode = rootNode.path("rates").path(target);

            if (targetNode.isMissingNode()) {
                log.error("Target currency {} not found.", target);
                throw new CommonException(500, "환율 데이터를 찾을 수 없습니다. (" + target + ")");
            }

            BigDecimal rateValue = targetNode.isNumber() ? targetNode.decimalValue() : new BigDecimal(targetNode.asText());

            log.info("[환율 조회 성공] base={}, target={}, rate={}", base, target, rateValue);

            return new ExchangeSDto(base, target, rateValue);

        } catch (CommonException e) {
            throw e;
        } catch (FeignException e) {
            log.error("Feign error: {}", e.getMessage(), e);
            throw new CommonException(500, "외부 환율 서비스 호출 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("Parsing error: {}", e.getMessage(), e);
            throw new CommonException(500, "환율 데이터 처리 중 오류가 발생했습니다.");
        }
    }
}
