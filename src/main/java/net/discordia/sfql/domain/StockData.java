package net.discordia.sfql.domain;

import java.util.List;

public record StockData(
        String symbol,
        List<StockDataEntry> entries
) {
}
