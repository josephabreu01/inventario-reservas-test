package com.inventrio.product.infrastructure.adapter.out.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeRateResponse(
    @JsonProperty("result") String result,
    @JsonProperty("base_code") String baseCode,
    @JsonAlias({"rates", "conversion_rates"}) @JsonProperty("rates") Map<String, BigDecimal> rates
) {}
