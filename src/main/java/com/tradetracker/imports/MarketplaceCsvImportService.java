package com.tradetracker.imports;

import com.tradetracker.requirements.BuyerRequirement;
import com.tradetracker.requirements.BuyerRequirementRepository;
import com.tradetracker.sellers.SellerProfile;
import com.tradetracker.sellers.SellerProfileRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketplaceCsvImportService {
    private final BuyerRequirementRepository buyerRequirementRepository;
    private final SellerProfileRepository sellerProfileRepository;

    public MarketplaceCsvImportService(
            BuyerRequirementRepository buyerRequirementRepository,
            SellerProfileRepository sellerProfileRepository
    ) {
        this.buyerRequirementRepository = buyerRequirementRepository;
        this.sellerProfileRepository = sellerProfileRepository;
    }

    public CsvImportResult importRequirements(MultipartFile file) {
        List<BuyerRequirement> requirements = new ArrayList<>();

        for (CSVRecord record : parse(file)) {
            BuyerRequirement requirement = new BuyerRequirement();
            requirement.setRequirementType(value(record, "requirementType", "Need"));
            requirement.setCategory(value(record, "category", "Marketplace"));
            requirement.setSubCategory(value(record, "subCategory", value(record, "productName", "Imported")));
            requirement.setProductName(value(record, "productName", "Imported requirement"));
            requirement.setBuyerName(value(record, "buyerName", value(record, "businessName", "Imported business")));
            requirement.setCountry(value(record, "country", "Not listed"));
            requirement.setRequiredQuantity(value(record, "requiredQuantity", value(record, "quantity", "Not listed")));
            requirement.setPaymentType(value(record, "paymentType", "Not listed"));
            requirement.setRequirementDate(dateValue(record, "requirementDate"));
            requirement.setTargetPrice(value(record, "targetPrice", value(record, "price", "Not listed")));
            requirement.setDeliveryPort(value(record, "deliveryPort", "Not listed"));
            requirement.setIncoterm(value(record, "incoterm", "Not listed"));
            requirement.setShipmentType(value(record, "shipmentType", "Not listed"));
            requirement.setQualitySpecs(value(record, "qualitySpecs", "Imported from CSV"));
            requirement.setNotes(value(record, "notes", "Imported from marketplace CSV."));
            requirement.setStatus(value(record, "status", "Open"));
            requirements.add(requirement);
        }

        buyerRequirementRepository.saveAll(requirements);
        return new CsvImportResult("requirements", requirements.size(), "Imported requirement rows from CSV.");
    }

    public CsvImportResult importSellers(MultipartFile file) {
        List<SellerProfile> sellers = new ArrayList<>();

        for (CSVRecord record : parse(file)) {
            SellerProfile seller = new SellerProfile();
            seller.setBusinessName(value(record, "businessName", value(record, "sellerName", "Imported seller")));
            seller.setCountry(value(record, "country", "Not listed"));
            seller.setCategory(value(record, "category", "Marketplace"));
            seller.setSubCategory(value(record, "subCategory", "Imported listing"));
            seller.setProducts(value(record, "products", value(record, "productName", "Not listed")));
            seller.setAvailableQuantity(value(record, "availableQuantity", value(record, "quantity", "Not listed")));
            seller.setPriceRange(value(record, "priceRange", value(record, "price", "Not listed")));
            seller.setShipmentTypes(value(record, "shipmentTypes", value(record, "shipmentType", "Not listed")));
            seller.setPaymentTerms(value(record, "paymentTerms", value(record, "paymentType", "Not listed")));
            seller.setCertifications(value(record, "certifications", "Not listed"));
            seller.setResponseTime(value(record, "responseTime", "Not listed"));
            seller.setProductImageUrl(value(record, "productImageUrl", value(record, "imageUrl", "")));
            seller.setVerified(booleanValue(record, "verified"));
            sellers.add(seller);
        }

        sellerProfileRepository.saveAll(sellers);
        return new CsvImportResult("sellers", sellers.size(), "Imported seller rows from CSV.");
    }

    private Iterable<CSVRecord> parse(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a CSV file.");
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .get()
                    .parse(reader);
            return parser.getRecords();
        } catch (IOException error) {
            throw new IllegalArgumentException("Could not read CSV file.", error);
        }
    }

    private String value(CSVRecord record, String header, String fallback) {
        if (!record.isMapped(header)) {
            return fallback;
        }

        String value = record.get(header);
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private LocalDate dateValue(CSVRecord record, String header) {
        String value = value(record, header, "");

        if (value.isBlank()) {
            return LocalDate.now();
        }

        try {
            return LocalDate.parse(value);
        } catch (RuntimeException ignored) {
            return LocalDate.now();
        }
    }

    private boolean booleanValue(CSVRecord record, String header) {
        String value = value(record, header, "false").toLowerCase();
        return value.equals("true") || value.equals("yes") || value.equals("verified") || value.equals("1");
    }
}
