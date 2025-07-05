package net.discordia.sfql;

import java.math.BigDecimal;
import net.discordia.sfql.eval.FailedToLookupVariableException;
import net.discordia.sfql.eval.VariableLookup;
import static java.math.RoundingMode.HALF_UP;

public class VariableLookupStub implements VariableLookup {
    @Override
    public BigDecimal lookup(final String variable) {
        return switch (variable) {
            case "c" -> new BigDecimal("100.0").setScale(2, HALF_UP);
            case "h1" -> new BigDecimal("5.0").setScale(2, HALF_UP);
            case "c1" -> new BigDecimal("99.0").setScale(2, HALF_UP);
            case "o" -> new BigDecimal("90.0").setScale(2, HALF_UP);
            case "v" -> new BigDecimal("110000.0").setScale(2, HALF_UP);
            case "v1" -> new BigDecimal("10000.0").setScale(2, HALF_UP);
            case "avgv10" -> new BigDecimal("900000.0").setScale(2, HALF_UP);
            default -> parseDouble(variable);
        };
    }

    private BigDecimal parseDouble(final String variable) {
        try {
            return BigDecimal.valueOf(Double.parseDouble(variable)).setScale(2, HALF_UP);
        } catch (NumberFormatException e) {
            throw new FailedToLookupVariableException("Failed to lookup variable %s".formatted(variable));
        }
    }
}
