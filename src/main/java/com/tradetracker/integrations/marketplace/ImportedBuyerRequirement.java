package com.tradetracker.integrations.marketplace;

import java.time.LocalDate;

public record ImportedBuyerRequirement(
        String source,
        String externalId,
        String requirementType,
        String category,
        String subCategory,
        String productName,
        String buyerName,
        String country,
        String requiredQuantity,
        String paymentType,
        LocalDate requirementDate,
        String targetPrice,
        String deliveryPort,
        String incoterm,
        String shipmentType,
        String qualitySpecs,
        String notes,
        String status
) {
}
