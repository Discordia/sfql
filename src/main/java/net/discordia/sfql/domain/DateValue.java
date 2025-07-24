package net.discordia.sfql.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DateValue(
    LocalDate date,
    BigDecimal value
) {
}
