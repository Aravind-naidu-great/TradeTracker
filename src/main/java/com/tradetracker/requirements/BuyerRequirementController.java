package com.tradetracker.requirements;

import com.tradetracker.imports.CsvImportResult;
import com.tradetracker.imports.MarketplaceCsvImportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class BuyerRequirementController {
    private final BuyerRequirementRepository buyerRequirementRepository;
    private final MarketplaceCsvImportService marketplaceCsvImportService;

    public BuyerRequirementController(
            BuyerRequirementRepository buyerRequirementRepository,
            MarketplaceCsvImportService marketplaceCsvImportService
    ) {
        this.buyerRequirementRepository = buyerRequirementRepository;
        this.marketplaceCsvImportService = marketplaceCsvImportService;
    }

    @GetMapping("/api/buyer-requirements")
    public List<BuyerRequirement> buyerRequirements() {
        return buyerRequirementRepository.findAllByOrderByRequirementDateDesc();
    }

    @GetMapping("/api/requirement-categories")
    public Map<String, List<String>> requirementCategories() {
        return Map.of(
                "Spices", List.of("Turmeric", "Black Pepper", "Cardamom", "Cumin", "Coriander"),
                "Pulses", List.of("Red Lentils", "Chickpeas", "Green Gram", "Kidney Beans"),
                "Textiles", List.of("Organic Cotton Fabric", "Denim", "Home Linen", "Garments"),
                "Fresh Produce", List.of("Mangoes", "Bananas", "Onions", "Grapes"),
                "Industrial", List.of("Fasteners", "Bearings", "Machine Parts", "Steel Coils")
        );
    }

    @PostMapping("/api/buyer-requirements")
    @ResponseStatus(HttpStatus.CREATED)
    public BuyerRequirement createRequirement(@Valid @RequestBody RequirementRequest request) {
        BuyerRequirement requirement = new BuyerRequirement();
        requirement.setRequirementType(request.requirementType().trim());
        requirement.setCategory(request.category().trim());
        requirement.setSubCategory(request.subCategory().trim());
        requirement.setProductName(request.productName().trim());
        requirement.setBuyerName(request.buyerName().trim());
        requirement.setCountry(request.country().trim());
        requirement.setRequiredQuantity(request.requiredQuantity().trim());
        requirement.setPaymentType(request.paymentType().trim());
        requirement.setRequirementDate(LocalDate.now());
        requirement.setTargetPrice(request.targetPrice().trim());
        requirement.setDeliveryPort(request.deliveryPort().trim());
        requirement.setIncoterm(request.incoterm().trim());
        requirement.setShipmentType(request.shipmentType().trim());
        requirement.setQualitySpecs(clean(request.qualitySpecs()));
        requirement.setNotes(clean(request.notes()));
        requirement.setStatus("Open");
        return buyerRequirementRepository.save(requirement);
    }

    @PostMapping("/api/buyer-requirements/import-csv")
    public CsvImportResult importRequirementsCsv(@RequestParam("file") MultipartFile file) {
        return marketplaceCsvImportService.importRequirements(file);
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
