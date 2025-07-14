package net.discordia.sfql;

import java.math.BigDecimal;
import java.util.Optional;
import net.discordia.sfql.eval.FailedToLookupVariableException;
import net.discordia.sfql.domain.VariableLookup;
import static java.math.RoundingMode.HALF_UP;

public class VariableLookupStub implements VariableLookup {
    @Override
    public Optional<BigDecimal> lookup(final String variable) {
        return switch (variable) {
            case "c" -> Optional.of(new BigDecimal("100.0").setScale(2, HALF_UP));
            case "h1" -> Optional.of(new BigDecimal("5.0").setScale(2, HALF_UP));
            case "c1" -> Optional.of(new BigDecimal("99.0").setScale(2, HALF_UP));
            case "o" -> Optional.of(new BigDecimal("90.0").setScale(2, HALF_UP));
            case "v" -> Optional.of(new BigDecimal("110000.0").setScale(2, HALF_UP));
            case "v1" -> Optional.of(new BigDecimal("10000.0").setScale(2, HALF_UP));
            case "avgv10" -> Optional.of(new BigDecimal("900000.0").setScale(2, HALF_UP));
            default -> parseDouble(variable);
        };
    }

    private Optional<BigDecimal> parseDouble(final String variable) {
        try {
            return Optional.of(BigDecimal.valueOf(Double.parseDouble(variable)).setScale(2, HALF_UP));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
