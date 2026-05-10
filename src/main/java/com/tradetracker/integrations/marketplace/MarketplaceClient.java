package com.tradetracker.integrations.marketplace;

public interface MarketplaceClient {
    String source();

    boolean isEnabled();

    MarketplaceImportBatch fetchImportBatch();
}
