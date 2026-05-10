package com.tradetracker.requirements;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BuyerRequirementDataSeeder implements CommandLineRunner {
    private final BuyerRequirementRepository buyerRequirementRepository;

    public BuyerRequirementDataSeeder(BuyerRequirementRepository buyerRequirementRepository) {
        this.buyerRequirementRepository = buyerRequirementRepository;
    }

    @Override
    public void run(String... args) {
        if (buyerRequirementRepository.count() > 0) {
            return;
        }

        buyerRequirementRepository.saveAll(List.of(
                requirement(
                        "Spices",
                        "Turmeric",
                        "Golden Harvest Foods",
                        "United Arab Emirates",
                        "18 metric tons",
                        "Letter of Credit",
                        LocalDate.now().plusDays(7),
                        "USD 1,950 per MT",
                        "Jebel Ali Port",
                        "CIF",
                        "Finger turmeric, 3% max moisture, food-grade packing",
                        "Open"
                ),
                requirement(
                        "Pulses",
                        "Red Lentils",
                        "Nordic Pantry Imports",
                        "Sweden",
                        "25 metric tons",
                        "Bank Transfer",
                        LocalDate.now().plusDays(10),
                        "USD 760 per MT",
                        "Port of Gothenburg",
                        "FOB",
                        "Machine cleaned, 2% broken max, 25kg PP bags",
                        "Open"
                ),
                requirement(
                        "Textiles",
                        "Organic Cotton Fabric",
                        "EcoWear Sourcing Co.",
                        "Canada",
                        "12,000 meters",
                        "30% advance, 70% against documents",
                        LocalDate.now().plusDays(14),
                        "USD 2.80 per meter",
                        "Port of Vancouver",
                        "CFR",
                        "GOTS certified, 180 GSM, natural dye compatible",
                        "Negotiating"
                ),
                requirement(
                        "Fresh Produce",
                        "Mangoes",
                        "Pacific Fresh Markets",
                        "Singapore",
                        "8 metric tons",
                        "Escrow",
                        LocalDate.now().plusDays(5),
                        "USD 1.20 per kg",
                        "Singapore Port",
                        "CIF",
                        "Alphonso grade A, pre-cooled, export carton packing",
                        "Urgent"
                ),
                requirement(
                        "Industrial",
                        "Stainless Steel Fasteners",
                        "BuildCore Components",
                        "Germany",
                        "500,000 pieces",
                        "Net 30",
                        LocalDate.now().plusDays(21),
                        "USD 0.06 per piece",
                        "Port of Hamburg",
                        "FOB",
                        "A2-70 grade, M6 and M8 mix, ISO compliant",
                        "Open"
                )
        ));
    }

    private BuyerRequirement requirement(
            String category,
            String productName,
            String buyerName,
            String country,
            String requiredQuantity,
            String paymentType,
            LocalDate requirementDate,
            String targetPrice,
            String deliveryPort,
            String incoterm,
            String qualitySpecs,
            String status
    ) {
        BuyerRequirement requirement = new BuyerRequirement();
        requirement.setRequirementType("Need");
        requirement.setCategory(category);
        requirement.setSubCategory(productName);
        requirement.setProductName(productName);
        requirement.setBuyerName(buyerName);
        requirement.setCountry(country);
        requirement.setRequiredQuantity(requiredQuantity);
        requirement.setPaymentType(paymentType);
        requirement.setRequirementDate(requirementDate);
        requirement.setTargetPrice(targetPrice);
        requirement.setDeliveryPort(deliveryPort);
        requirement.setIncoterm(incoterm);
        requirement.setShipmentType("Sea Freight");
        requirement.setQualitySpecs(qualitySpecs);
        requirement.setNotes("Seeded buyer requirement from TradeTracker marketplace demand.");
        requirement.setStatus(status);
        return requirement;
    }
}
