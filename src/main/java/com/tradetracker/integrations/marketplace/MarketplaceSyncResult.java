package com.tradetracker.integrations.marketplace;

public record MarketplaceSyncResult(
        String source,
        boolean success,
        int importedRequirements,
        int importedSellers,
        String message
) {
}
