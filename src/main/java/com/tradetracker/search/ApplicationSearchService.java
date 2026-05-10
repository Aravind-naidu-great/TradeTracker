package com.tradetracker.search;

import com.tradetracker.domain.BusinessProfile;
import com.tradetracker.domain.Shipment;
import com.tradetracker.repository.BusinessProfileRepository;
import com.tradetracker.repository.ShipmentRepository;
import com.tradetracker.requirements.BuyerRequirement;
import com.tradetracker.requirements.BuyerRequirementRepository;
import com.tradetracker.sellers.SellerProfile;
import com.tradetracker.sellers.SellerProfileRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ApplicationSearchService {
    private static final List<SearchDocument> APP_DOCUMENTS = List.of(
            new SearchDocument("Page", "Buyers", "Discover buyer demand, matching opportunities, and verified customer requests.", "#buyers",
                    List.of("buyer", "buyers", "demand", "customer", "customers", "matches", "opportunities", "requests")),
            new SearchDocument("Page", "Sellers", "Explore suppliers, export-ready companies, and trusted business profiles.", "sellers.html",
                    List.of("seller", "sellers", "supplier", "suppliers", "export", "company", "companies", "business", "profiles", "supply")),
            new SearchDocument("Page", "Requirement", "Post a buying need or selling availability with quantity, price, category, and shipment details.", "requirements.html",
                    List.of("post", "publish", "requirement", "requirements", "need", "availability", "quantity", "price", "shipment", "category")),
            new SearchDocument("Page", "Chat", "Keep buyer, seller, and shipment conversations connected to trade activity.", "chat.html",
                    List.of("chat", "message", "messages", "conversation", "conversations", "talk", "contact")),
            new SearchDocument("Page", "About", "Review how TradeTracker supports global trade operations and customer intelligence.", "#about",
                    List.of("about", "help", "platform", "trade", "operations", "intelligence")),
            new SearchDocument("Account", "Profile", "Review the signed-in business profile and account details.", "#profile",
                    List.of("profile", "account", "business", "me", "settings")),
            new SearchDocument("Account", "Previous Shipments", "Review shipment history and completed trade movement.", "#previous-shipments",
                    List.of("previous", "past", "history", "shipment", "shipments", "completed")),
            new SearchDocument("Account", "Revenue", "Review revenue and commercial performance.", "#revenue",
                    List.of("revenue", "income", "sales", "money", "commercial")),
            new SearchDocument("Account", "Analytics", "Explore analytics, performance reports, and trade intelligence.", "#analytics",
                    List.of("analytics", "reports", "report", "performance", "insights", "intelligence")),
            new SearchDocument("Account", "Current Shipments", "Track active shipments and current delivery status.", "#current-shipments",
                    List.of("current", "active", "shipment", "shipments", "tracking", "status", "delivery"))
    );

    private final BusinessProfileRepository businessProfileRepository;
    private final ShipmentRepository shipmentRepository;
    private final BuyerRequirementRepository buyerRequirementRepository;
    private final SellerProfileRepository sellerProfileRepository;

    public ApplicationSearchService(
            BusinessProfileRepository businessProfileRepository,
            ShipmentRepository shipmentRepository,
            BuyerRequirementRepository buyerRequirementRepository,
            SellerProfileRepository sellerProfileRepository
    ) {
        this.businessProfileRepository = businessProfileRepository;
        this.shipmentRepository = shipmentRepository;
        this.buyerRequirementRepository = buyerRequirementRepository;
        this.sellerProfileRepository = sellerProfileRepository;
    }

    public SearchResponse search(String rawQuery) {
        String query = rawQuery == null ? "" : rawQuery.trim();

        if (query.isBlank()) {
            return new SearchResponse(query, "Ask me for buyers, sellers, shipments, revenue, analytics, or a business profile.", List.of());
        }

        List<String> terms = tokenize(query);
        List<SearchResult> results = new ArrayList<>();

        for (SearchDocument document : APP_DOCUMENTS) {
            double score = score(document, terms, query);

            if (score > 0) {
                results.add(new SearchResult(document.type(), document.title(), document.description(), document.url(), score));
            }
        }

        for (BusinessProfile profile : businessProfileRepository.findAll()) {
            SearchDocument document = businessProfileDocument(profile);
            double score = score(document, terms, query);

            if (score > 0) {
                results.add(new SearchResult(document.type(), document.title(), document.description(), document.url(), score));
            }
        }

        for (Shipment shipment : shipmentRepository.findAll()) {
            SearchDocument document = shipmentDocument(shipment);
            double score = score(document, terms, query);

            if (score > 0) {
                results.add(new SearchResult(document.type(), document.title(), document.description(), document.url(), score));
            }
        }

        for (BuyerRequirement requirement : buyerRequirementRepository.findAll()) {
            SearchDocument document = buyerRequirementDocument(requirement);
            double score = score(document, terms, query);

            if (score > 0) {
                results.add(new SearchResult(document.type(), document.title(), document.description(), document.url(), score));
            }
        }

        for (SellerProfile seller : sellerProfileRepository.findAll()) {
            SearchDocument document = sellerDocument(seller);
            double score = score(document, terms, query);

            if (score > 0) {
                results.add(new SearchResult(document.type(), document.title(), document.description(), document.url(), score));
            }
        }

        List<SearchResult> rankedResults = results.stream()
                .sorted(Comparator.comparingDouble(SearchResult::score).reversed())
                .limit(6)
                .toList();

        return new SearchResponse(query, buildAnswer(query, rankedResults), rankedResults);
    }

    private SearchDocument businessProfileDocument(BusinessProfile profile) {
        String title = emptyToFallback(profile.getName(), "Business profile");
        String country = emptyToFallback(profile.getCountry(), "country not listed");
        String product = emptyToFallback(profile.getProduct(), "product not listed");
        String purpose = emptyToFallback(profile.getPurpose(), "trade purpose not listed");
        String description = String.format("%s business profile in %s, product: %s, purpose: %s.", title, country, product, purpose);

        return new SearchDocument("Business", title, description, "#sellers",
                List.of(title, country, product, purpose, "business", "profile", "company", "seller", "buyer"));
    }

    private SearchDocument shipmentDocument(Shipment shipment) {
        String trackingCode = emptyToFallback(shipment.getTrackingCode(), "Shipment");
        String origin = emptyToFallback(shipment.getOrigin(), "origin not listed");
        String destination = emptyToFallback(shipment.getDestination(), "destination not listed");
        String status = emptyToFallback(shipment.getStatus(), "status not listed");
        String description = String.format("%s from %s to %s is %s.", trackingCode, origin, destination, status);

        return new SearchDocument("Shipment", trackingCode, description, "#current-shipments",
                List.of(trackingCode, origin, destination, status, "shipment", "shipments", "tracking", "delivery"));
    }

    private SearchDocument buyerRequirementDocument(BuyerRequirement requirement) {
        String productName = emptyToFallback(requirement.getProductName(), "Buyer requirement");
        String buyerName = emptyToFallback(requirement.getBuyerName(), "buyer not listed");
        String country = emptyToFallback(requirement.getCountry(), "country not listed");
        String category = emptyToFallback(requirement.getCategory(), "category not listed");
        String quantity = emptyToFallback(requirement.getRequiredQuantity(), "quantity not listed");
        String paymentType = emptyToFallback(requirement.getPaymentType(), "payment not listed");
        String status = emptyToFallback(requirement.getStatus(), "status not listed");
        String description = String.format("%s buyer requirement from %s in %s for %s. Payment: %s. Status: %s.",
                productName, buyerName, country, quantity, paymentType, status);

        return new SearchDocument("Buyer Requirement", productName, description, "#buyer-requirements",
                List.of(productName, buyerName, country, category, quantity, paymentType, status,
                        emptyToFallback(requirement.getDeliveryPort(), ""),
                        emptyToFallback(requirement.getIncoterm(), ""),
                        emptyToFallback(requirement.getQualitySpecs(), ""),
                        "buyer", "buyers", "requirement", "requirements", "demand", "quantity", "payment"));
    }

    private SearchDocument sellerDocument(SellerProfile seller) {
        String businessName = emptyToFallback(seller.getBusinessName(), "Seller");
        String country = emptyToFallback(seller.getCountry(), "country not listed");
        String category = emptyToFallback(seller.getCategory(), "category not listed");
        String products = emptyToFallback(seller.getProducts(), "products not listed");
        String description = String.format("%s sells %s from %s. Category: %s.", businessName, products, country, category);

        return new SearchDocument("Seller", businessName, description, "sellers.html",
                List.of(businessName, country, category,
                        emptyToFallback(seller.getSubCategory(), ""),
                        products,
                        emptyToFallback(seller.getShipmentTypes(), ""),
                        "seller", "sellers", "supplier", "supply", "products"));
    }

    private double score(SearchDocument document, List<String> terms, String rawQuery) {
        String searchable = normalize(document.title() + " " + document.description() + " " + String.join(" ", document.tags()));
        String title = normalize(document.title());
        String query = normalize(rawQuery);
        double score = 0;

        for (String term : terms) {
            if (title.contains(term)) {
                score += 4;
            }

            if (searchable.contains(term)) {
                score += 2;
            }
        }

        for (String tag : document.tags()) {
            String normalizedTag = normalize(tag);

            if (!normalizedTag.isBlank() && query.contains(normalizedTag)) {
                score += 3;
            }
        }

        score += intentBoost(document, query);
        return score;
    }

    private double intentBoost(SearchDocument document, String query) {
        String title = normalize(document.title());
        List<String> tags = document.tags();
        double boost = 0;

        if (containsAny(query, "find", "show", "search", "where", "open", "go to", "look for")) {
            boost += 0.5;
        }

        if (containsAny(query, "buy", "buyer", "buyers", "customer", "demand") && tags.contains("buyer")) {
            boost += 6;
        }

        if (containsAny(query, "requirement", "requirements", "need", "wanted", "quantity") && tags.contains("requirement")) {
            boost += 7;
        }

        if (containsAny(query, "sell", "seller", "sellers", "supplier", "suppliers", "export") && tags.contains("seller")) {
            boost += 6;
        }

        if (containsAny(query, "ship", "shipment", "delivery", "tracking", "where is", "status") && tags.contains("shipment")) {
            boost += 6;
        }

        if (containsAny(query, "money", "income", "sales", "revenue") && title.contains("revenue")) {
            boost += 8;
        }

        if (containsAny(query, "insight", "insights", "report", "analytics", "performance") && title.contains("analytics")) {
            boost += 8;
        }

        if (containsAny(query, "message", "chat", "talk", "conversation", "contact") && title.contains("chat")) {
            boost += 8;
        }

        if (containsAny(query, "who am i", "account", "profile", "my business") && title.contains("profile")) {
            boost += 8;
        }

        return boost;
    }

    private String buildAnswer(String query, List<SearchResult> results) {
        if (results.isEmpty()) {
            return "I could not find an exact match yet. Try asking about buyers, sellers, shipments, revenue, analytics, chat, or requirements.";
        }

        SearchResult topResult = results.getFirst();
        return "I interpreted \"" + query + "\" as " + topResult.type().toLowerCase(Locale.ROOT)
                + " search. Best match: " + topResult.title() + ".";
    }

    private List<String> tokenize(String value) {
        String normalized = normalize(value);

        if (normalized.isBlank()) {
            return List.of();
        }

        return List.of(normalized.split("\\s+")).stream()
                .filter(term -> term.length() > 1)
                .toList();
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\s-]", " ").replaceAll("\\s+", " ").trim();
    }

    private boolean containsAny(String query, String... phrases) {
        for (String phrase : phrases) {
            if (query.contains(phrase)) {
                return true;
            }
        }

        return false;
    }

    private String emptyToFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private record SearchDocument(
            String type,
            String title,
            String description,
            String url,
            List<String> tags
    ) {
    }
}
