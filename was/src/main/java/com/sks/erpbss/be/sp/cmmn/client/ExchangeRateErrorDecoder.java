package com.sks.erpbss.be.sp.cmmn.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sks.erpbss.be.sp.cmmn.CommonException;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.InputStream;

public class ExchangeRateErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    public ExchangeRateErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        int statusCode = (response != null && response.status() > 0) ? response.status() : 500;
        String message = "외부 환율 서비스 호출 오류가 발생했습니다.";

        if (response != null && response.body() != null) {
            try (InputStream bodyStream = response.body().asInputStream()) {
                JsonNode rootNode = objectMapper.readTree(bodyStream);

                JsonNode statusNode = rootNode.path("status");
                if (statusNode.isInt() && statusNode.intValue() > 0) {
                    statusCode = statusNode.intValue();
                }

                JsonNode messageNode = rootNode.path("message");
                if (messageNode.isTextual() && !messageNode.asText().isBlank()) {
                    message = messageNode.asText();
                } else if (messageNode.isArray() && !messageNode.isEmpty()) {
                    JsonNode first = messageNode.get(0);
                    if (first != null && first.isTextual() && !first.asText().isBlank()) {
                        message = first.asText();
                    }
                }
            } catch (IOException ignored) {
                // Error body parsing failure should not block status propagation.
            }
        }

        return new CommonException(statusCode, message);
    }
}
