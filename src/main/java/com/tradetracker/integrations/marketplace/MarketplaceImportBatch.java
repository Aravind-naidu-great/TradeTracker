package com.tradetracker.integrations.marketplace;

import java.util.List;

public record MarketplaceImportBatch(
        String source,
        List<ImportedBuyerRequirement> buyerRequirements,
        List<ImportedSellerListing> sellerListings
) {
}
