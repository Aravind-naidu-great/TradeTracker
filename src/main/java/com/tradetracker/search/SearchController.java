package com.tradetracker.search;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    private final ApplicationSearchService applicationSearchService;

    public SearchController(ApplicationSearchService applicationSearchService) {
        this.applicationSearchService = applicationSearchService;
    }

    @GetMapping("/api/search")
    public SearchResponse search(@RequestParam(name = "q", defaultValue = "") String query) {
        return applicationSearchService.search(query);
    }
}
