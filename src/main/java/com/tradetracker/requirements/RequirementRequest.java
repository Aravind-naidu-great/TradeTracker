package com.tradetracker.requirements;

import jakarta.validation.constraints.NotBlank;

public record RequirementRequest(
        @NotBlank String requirementType,
        @NotBlank String category,
        @NotBlank String subCategory,
        @NotBlank String productName,
        @NotBlank String buyerName,
        @NotBlank String country,
        @NotBlank String requiredQuantity,
        @NotBlank String paymentType,
        @NotBlank String targetPrice,
        @NotBlank String deliveryPort,
        @NotBlank String incoterm,
        @NotBlank String shipmentType,
        String qualitySpecs,
        String notes
) {
}
