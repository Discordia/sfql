package net.discordia.sfql.domain;

import java.time.LocalDateTime;

public record StockDataEntry(
    LocalDateTime datetime,
    String open,
    String high,
    String low,
    String close,
    String volume
) {
}
