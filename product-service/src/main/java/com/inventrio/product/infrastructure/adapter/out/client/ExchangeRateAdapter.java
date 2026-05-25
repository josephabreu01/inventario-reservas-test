package com.inventrio.product.infrastructure.adapter.out.client;

import com.inventrio.product.domain.port.out.ExchangeRatePort;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.math.BigDecimal;
import java.util.Optional;

@ApplicationScoped
public class ExchangeRateAdapter implements ExchangeRatePort {

    private final ExchangeRateApiClient apiClient;

    public ExchangeRateAdapter(@RestClient ExchangeRateApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    @CacheResult(cacheName = "exchange-rates")
    public BigDecimal getExchangeRate(String targetCurrency) {
        if (targetCurrency == null || targetCurrency.isBlank() || targetCurrency.equalsIgnoreCase("USD")) {
            return BigDecimal.ONE;
        }

        ExchangeRateResponse response;
        try {
            response = apiClient.getUsdRates();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching exchange rate for " + targetCurrency + ": " + e.getMessage(), e);
        }

        return Optional.ofNullable(response)
                .map(ExchangeRateResponse::rates)
                .map(rates -> rates.get(targetCurrency.toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported or invalid currency: " + targetCurrency));
    }
}
