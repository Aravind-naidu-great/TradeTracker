package com.tradetracker.search;

import java.util.List;

public record SearchResponse(
        String query,
        String answer,
        List<SearchResult> results
) {
}
