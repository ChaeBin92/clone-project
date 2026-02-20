package com.sks.erpbss.be.sp.cmmn.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeRateFeignConfig {

    @Bean
    public ErrorDecoder exchangeRateErrorDecoder(ObjectMapper objectMapper) {
        return new ExchangeRateErrorDecoder(objectMapper);
    }
}
