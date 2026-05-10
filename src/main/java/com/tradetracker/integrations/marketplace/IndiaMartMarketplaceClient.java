package com.tradetracker.integrations.marketplace;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IndiaMartMarketplaceClient implements MarketplaceClient {
    private final boolean enabled;
    private final String apiKey;
    private final String apiBaseUrl;

    public IndiaMartMarketplaceClient(
            @Value("${marketplace.indiamart.enabled:false}") boolean enabled,
            @Value("${marketplace.indiamart.api-key:}") String apiKey,
            @Value("${marketplace.indiamart.api-base-url:}") String apiBaseUrl
    ) {
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public String source() {
        return "indiamart";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public MarketplaceImportBatch fetchImportBatch() {
        if (!enabled) {
            throw new MarketplaceIntegrationException("IndiaMART sync is disabled. Set marketplace.indiamart.enabled=true after credentials are available.");
        }

        if (apiKey.isBlank() || apiBaseUrl.isBlank()) {
            throw new MarketplaceIntegrationException("IndiaMART credentials are not configured.");
        }

        throw new MarketplaceIntegrationException("IndiaMART API response mapping is ready for credentials, but no official endpoint contract has been configured yet.");
    }
}
