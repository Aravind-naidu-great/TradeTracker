package com.tradetracker.integrations.marketplace;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AlibabaMarketplaceClient implements MarketplaceClient {
    private final boolean enabled;
    private final String apiKey;
    private final String apiBaseUrl;

    public AlibabaMarketplaceClient(
            @Value("${marketplace.alibaba.enabled:false}") boolean enabled,
            @Value("${marketplace.alibaba.api-key:}") String apiKey,
            @Value("${marketplace.alibaba.api-base-url:}") String apiBaseUrl
    ) {
        this.enabled = enabled;
        this.apiKey = apiKey;
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public String source() {
        return "alibaba";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public MarketplaceImportBatch fetchImportBatch() {
        if (!enabled) {
            throw new MarketplaceIntegrationException("Alibaba sync is disabled. Set marketplace.alibaba.enabled=true after API access is available.");
        }

        if (apiKey.isBlank() || apiBaseUrl.isBlank()) {
            throw new MarketplaceIntegrationException("Alibaba credentials are not configured.");
        }

        throw new MarketplaceIntegrationException("Alibaba API response mapping is ready for credentials, but no official endpoint contract has been configured yet.");
    }
}
