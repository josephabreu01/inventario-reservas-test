package com.inventrio.product.infrastructure.adapter.out.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "exchange-rate-api")
@Path("/latest")
public interface ExchangeRateApiClient {

    @GET
    @Path("/USD")
    @Produces(MediaType.APPLICATION_JSON)
    ExchangeRateResponse getUsdRates();
}
