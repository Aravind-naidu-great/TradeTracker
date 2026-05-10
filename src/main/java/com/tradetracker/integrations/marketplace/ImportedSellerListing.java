package com.tradetracker.integrations.marketplace;

public record ImportedSellerListing(
        String source,
        String externalId,
        String businessName,
        String country,
        String category,
        String subCategory,
        String products,
        String availableQuantity,
        String priceRange,
        String shipmentTypes,
        String paymentTerms,
        String certifications,
        String responseTime,
        boolean verified
) {
}
