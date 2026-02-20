package com.sks.erpbss.be.sp.dashboard.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sks.erpbss.be.sp.cmmn.CommonException;
import com.sks.erpbss.be.sp.cmmn.client.ExchangeRateClient;
import com.sks.erpbss.be.sp.dashboard.dto.ExchangeRDto;
import com.sks.erpbss.be.sp.dashboard.dto.ExchangeSDto;
import com.sks.erpbss.be.sp.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");

    private final ExchangeRateClient exchangeRateClient;
    private final ObjectMapper objectMapper;

    @Override
    public ExchangeSDto getExchangeRateData(ExchangeRDto rDto) {
        if (rDto == null) {
            throw new CommonException(400, "요청 값이 비어 있습니다.");
        }

        String base = rDto.getBase();
        String target = rDto.getTarget();

        if (base == null || base.isBlank() || target == null || target.isBlank()) {
            throw new CommonException(400, "기준 통화(base)와 대상 통화(target)는 필수입니다.");
        }

        base = base.trim().toUpperCase(Locale.ROOT);
        target = target.trim().toUpperCase(Locale.ROOT);

        if (!CURRENCY_CODE_PATTERN.matcher(base).matches() || !CURRENCY_CODE_PATTERN.matcher(target).matches()) {
            throw new CommonException(400, "통화 코드는 영문 대문자 3자리여야 합니다.");
        }

        JsonNode rootNode;
        try {
            log.info("Calling ExchangeRateClient for base: {}", base);
            String responseBody = exchangeRateClient.getExchangeRate(base);
            rootNode = objectMapper.readTree(responseBody);
        } catch (Exception e) {
            if (e instanceof CommonException) {
                throw (CommonException) e;
            }
            log.error("Failed to fetch exchange rate data. base={}, target={}", base, target, e);
            throw new CommonException(500, "환율 데이터 처리 중 오류가 발생했습니다.");
        }

        JsonNode targetNode = rootNode.path("rates").path(target);

        if (targetNode.isMissingNode()) {
            log.error("Target currency {} not found.", target);
            throw new CommonException(400, "환율 데이터를 찾을 수 없습니다. (" + target + ")");
        }

        BigDecimal rateValue = targetNode.isNumber() ? targetNode.decimalValue() : new BigDecimal(targetNode.asText());

        log.info("[환율 조회 성공] base={}, target={}, rate={}", base, target, rateValue);

        return new ExchangeSDto(base, target, rateValue);
    }
}
