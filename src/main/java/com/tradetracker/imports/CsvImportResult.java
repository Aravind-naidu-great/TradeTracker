package com.tradetracker.imports;

public record CsvImportResult(
        String type,
        int importedRows,
        String message
) {
}
