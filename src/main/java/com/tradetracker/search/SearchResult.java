package com.tradetracker.search;

public record SearchResult(
        String type,
        String title,
        String description,
        String url,
        double score
) {
}
