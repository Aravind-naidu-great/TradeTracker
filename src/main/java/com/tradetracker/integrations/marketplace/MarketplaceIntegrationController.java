package com.tradetracker.integrations.marketplace;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MarketplaceIntegrationController {
    private final MarketplaceSyncService marketplaceSyncService;

    public MarketplaceIntegrationController(MarketplaceSyncService marketplaceSyncService) {
        this.marketplaceSyncService = marketplaceSyncService;
    }

    @GetMapping("/api/integrations/marketplaces")
    public List<String> marketplaces() {
        return marketplaceSyncService.availableSources();
    }

    @PostMapping("/api/integrations/{source}/sync")
    public MarketplaceSyncResult sync(@PathVariable String source) {
        return marketplaceSyncService.sync(source);
    }
}
