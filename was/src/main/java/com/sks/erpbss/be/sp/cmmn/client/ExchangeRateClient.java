package com.sks.erpbss.be.sp.cmmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * ExchangeRateClient 설정 기준:
 * - url 값은 코드 하드코딩이 아닌 application*.yaml의 설정 키를 참조합니다.
 * - 현재 참조 키: apim.api.exchange-rate-url
 * - 해당 설정은 반드시 프로토콜(http/https)을 포함한 완전한 URL이어야 합니다.
 *   예) https://open.er-api.com/v6
 * - 키 경로가 불일치하면 애플리케이션 기동 시 placeholder가 치환되지 않아
 *   "http://${...} is malformed" 오류가 발생할 수 있습니다.
 */
@FeignClient(name = "exchangeRateClient", url = "${apim.api.exchange-rate-url}")
public interface ExchangeRateClient {

    @GetMapping("/latest/{base}")
    String getExchangeRate(@PathVariable("base") String base);
}
