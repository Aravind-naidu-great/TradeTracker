package com.tradetracker.sellers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SellerProfileDataSeeder implements CommandLineRunner {
    private final SellerProfileRepository sellerProfileRepository;

    public SellerProfileDataSeeder(SellerProfileRepository sellerProfileRepository) {
        this.sellerProfileRepository = sellerProfileRepository;
    }

    @Override
    public void run(String... args) {
        if (sellerProfileRepository.count() > 0) {
            return;
        }

        sellerProfileRepository.saveAll(List.of(
                seller("Sunrise Spice Exports", "India", "Spices", "Turmeric", "Turmeric fingers, turmeric powder, cumin", "40 metric tons monthly", "USD 1,850 - 2,050 per MT", "Sea Freight, Air Freight", "LC, 30% advance", "FSSAI, ISO 22000", "Usually replies in 2 hours", true),
                seller("Deccan Pulses Trading", "India", "Pulses", "Red Lentils", "Red lentils, chickpeas, green gram", "75 metric tons ready", "USD 720 - 820 per MT", "Sea Freight", "Bank transfer, LC", "APEDA registered", "Usually replies same day", true),
                seller("EcoWeave Mills", "Turkey", "Textiles", "Organic Cotton Fabric", "Organic cotton, denim, home linen", "30,000 meters monthly", "USD 2.50 - 3.20 per meter", "Sea Freight, Road Freight", "30/70 against documents", "GOTS, OEKO-TEX", "Usually replies in 4 hours", true),
                seller("Tropic Fresh Exporters", "Philippines", "Fresh Produce", "Mangoes", "Mangoes, bananas, pineapples", "15 metric tons weekly", "USD 1.05 - 1.35 per kg", "Reefer Sea Freight, Air Freight", "Escrow, advance", "GlobalG.A.P.", "Usually replies in 1 hour", false),
                seller("Rhine Industrial Supply", "Germany", "Industrial", "Fasteners", "Steel fasteners, bearings, machine parts", "1M pieces available", "Custom quote by grade", "Sea Freight, Road Freight", "Net 30 for approved buyers", "ISO 9001", "Usually replies in 3 hours", true)
        ));
    }

    private SellerProfile seller(
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
        SellerProfile seller = new SellerProfile();
        seller.setBusinessName(businessName);
        seller.setCountry(country);
        seller.setCategory(category);
        seller.setSubCategory(subCategory);
        seller.setProducts(products);
        seller.setAvailableQuantity(availableQuantity);
        seller.setPriceRange(priceRange);
        seller.setShipmentTypes(shipmentTypes);
        seller.setPaymentTerms(paymentTerms);
        seller.setCertifications(certifications);
        seller.setResponseTime(responseTime);
        seller.setVerified(verified);
        return seller;
    }
}
