package com.tradetracker.sellers;

import com.tradetracker.imports.CsvImportResult;
import com.tradetracker.imports.MarketplaceCsvImportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class SellerProfileController {
    private final SellerProfileRepository sellerProfileRepository;
    private final MarketplaceCsvImportService marketplaceCsvImportService;
    private final SellerImageStorageService sellerImageStorageService;

    public SellerProfileController(
            SellerProfileRepository sellerProfileRepository,
            MarketplaceCsvImportService marketplaceCsvImportService,
            SellerImageStorageService sellerImageStorageService
    ) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.marketplaceCsvImportService = marketplaceCsvImportService;
        this.sellerImageStorageService = sellerImageStorageService;
    }

    @GetMapping("/api/sellers")
    public List<SellerProfile> sellers() {
        return sellerProfileRepository.findAllByOrderByBusinessNameAsc();
    }

    @PostMapping("/api/sellers/import-csv")
    public CsvImportResult importSellersCsv(@RequestParam("file") MultipartFile file) {
        return marketplaceCsvImportService.importSellers(file);
    }

    @PostMapping("/api/sellers")
    public SellerProfile createSeller(
            @RequestParam String businessName,
            @RequestParam String country,
            @RequestParam String category,
            @RequestParam String subCategory,
            @RequestParam String products,
            @RequestParam String availableQuantity,
            @RequestParam String priceRange,
            @RequestParam String shipmentTypes,
            @RequestParam String paymentTerms,
            @RequestParam(required = false, defaultValue = "Not listed") String certifications,
            @RequestParam(required = false, defaultValue = "Usually replies same day") String responseTime,
            @RequestParam(required = false) MultipartFile productImage
    ) {
        SellerProfile seller = new SellerProfile();
        seller.setBusinessName(businessName.trim());
        seller.setCountry(country.trim());
        seller.setCategory(category.trim());
        seller.setSubCategory(subCategory.trim());
        seller.setProducts(products.trim());
        seller.setAvailableQuantity(availableQuantity.trim());
        seller.setPriceRange(priceRange.trim());
        seller.setShipmentTypes(shipmentTypes.trim());
        seller.setPaymentTerms(paymentTerms.trim());
        seller.setCertifications(clean(certifications, "Not listed"));
        seller.setResponseTime(clean(responseTime, "Usually replies same day"));
        seller.setProductImageUrl(sellerImageStorageService.store(productImage));
        seller.setVerified(false);
        return sellerProfileRepository.save(seller);
    }

    private String clean(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
