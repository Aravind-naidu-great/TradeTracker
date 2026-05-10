package com.tradetracker.integrations.marketplace;

import com.tradetracker.requirements.BuyerRequirement;
import com.tradetracker.requirements.BuyerRequirementRepository;
import com.tradetracker.sellers.SellerProfile;
import com.tradetracker.sellers.SellerProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class MarketplaceSyncService {
    private final List<MarketplaceClient> marketplaceClients;
    private final BuyerRequirementRepository buyerRequirementRepository;
    private final SellerProfileRepository sellerProfileRepository;

    public MarketplaceSyncService(
            List<MarketplaceClient> marketplaceClients,
            BuyerRequirementRepository buyerRequirementRepository,
            SellerProfileRepository sellerProfileRepository
    ) {
        this.marketplaceClients = marketplaceClients;
        this.buyerRequirementRepository = buyerRequirementRepository;
        this.sellerProfileRepository = sellerProfileRepository;
    }

    public List<String> availableSources() {
        return marketplaceClients.stream()
                .map(MarketplaceClient::source)
                .sorted()
                .toList();
    }

    public MarketplaceSyncResult sync(String source) {
        MarketplaceClient client = marketplaceClients.stream()
                .filter(candidate -> candidate.source().equalsIgnoreCase(source))
                .findFirst()
                .orElseThrow(() -> new MarketplaceIntegrationException("Unknown marketplace source: " + source));

        try {
            MarketplaceImportBatch batch = client.fetchImportBatch();
            int importedRequirements = importRequirements(batch.buyerRequirements());
            int importedSellers = importSellers(batch.sellerListings());

            return new MarketplaceSyncResult(
                    client.source(),
                    true,
                    importedRequirements,
                    importedSellers,
                    "Marketplace sync completed."
            );
        } catch (MarketplaceIntegrationException error) {
            return new MarketplaceSyncResult(client.source(), false, 0, 0, error.getMessage());
        }
    }

    private int importRequirements(List<ImportedBuyerRequirement> importedRequirements) {
        if (importedRequirements == null || importedRequirements.isEmpty()) {
            return 0;
        }

        List<BuyerRequirement> requirements = importedRequirements.stream()
                .map(this::toBuyerRequirement)
                .toList();
        buyerRequirementRepository.saveAll(requirements);
        return requirements.size();
    }

    private int importSellers(List<ImportedSellerListing> importedSellerListings) {
        if (importedSellerListings == null || importedSellerListings.isEmpty()) {
            return 0;
        }

        List<SellerProfile> sellers = importedSellerListings.stream()
                .map(this::toSellerProfile)
                .toList();
        sellerProfileRepository.saveAll(sellers);
        return sellers.size();
    }

    private BuyerRequirement toBuyerRequirement(ImportedBuyerRequirement imported) {
        BuyerRequirement requirement = new BuyerRequirement();
        requirement.setRequirementType(fallback(imported.requirementType(), "Need"));
        requirement.setCategory(fallback(imported.category(), "Marketplace"));
        requirement.setSubCategory(fallback(imported.subCategory(), imported.productName()));
        requirement.setProductName(fallback(imported.productName(), "Imported requirement"));
        requirement.setBuyerName(fallback(imported.buyerName(), imported.source() + " buyer"));
        requirement.setCountry(fallback(imported.country(), "Not listed"));
        requirement.setRequiredQuantity(fallback(imported.requiredQuantity(), "Not listed"));
        requirement.setPaymentType(fallback(imported.paymentType(), "Not listed"));
        requirement.setRequirementDate(imported.requirementDate() == null ? LocalDate.now() : imported.requirementDate());
        requirement.setTargetPrice(fallback(imported.targetPrice(), "Not listed"));
        requirement.setDeliveryPort(fallback(imported.deliveryPort(), "Not listed"));
        requirement.setIncoterm(fallback(imported.incoterm(), "Not listed"));
        requirement.setShipmentType(fallback(imported.shipmentType(), "Not listed"));
        requirement.setQualitySpecs(fallback(imported.qualitySpecs(), "Imported from " + imported.source()));
        requirement.setNotes(importNote(imported.source(), imported.externalId(), imported.notes()));
        requirement.setStatus(fallback(imported.status(), "Open"));
        return requirement;
    }

    private SellerProfile toSellerProfile(ImportedSellerListing imported) {
        SellerProfile seller = new SellerProfile();
        seller.setBusinessName(fallback(imported.businessName(), imported.source() + " seller"));
        seller.setCountry(fallback(imported.country(), "Not listed"));
        seller.setCategory(fallback(imported.category(), "Marketplace"));
        seller.setSubCategory(fallback(imported.subCategory(), "Imported listing"));
        seller.setProducts(fallback(imported.products(), "Not listed"));
        seller.setAvailableQuantity(fallback(imported.availableQuantity(), "Not listed"));
        seller.setPriceRange(fallback(imported.priceRange(), "Not listed"));
        seller.setShipmentTypes(fallback(imported.shipmentTypes(), "Not listed"));
        seller.setPaymentTerms(fallback(imported.paymentTerms(), "Not listed"));
        seller.setCertifications(fallback(imported.certifications(), "Not listed"));
        seller.setResponseTime(fallback(imported.responseTime(), "Not listed"));
        seller.setVerified(imported.verified());
        return seller;
    }

    private String fallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String importNote(String source, String externalId, String notes) {
        String normalizedSource = fallback(source, "marketplace").toUpperCase(Locale.ROOT);
        String normalizedExternalId = fallback(externalId, "not provided");
        String normalizedNotes = fallback(notes, "No notes provided.");
        return "Imported from " + normalizedSource + " external ID " + normalizedExternalId + ". " + normalizedNotes;
    }
}
