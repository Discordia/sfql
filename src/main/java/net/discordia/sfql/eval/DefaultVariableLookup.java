package net.discordia.sfql.eval;

import java.math.BigDecimal;
import static java.math.RoundingMode.HALF_UP;

public class DefaultVariableLookup implements VariableLookup {
    @Override
    public BigDecimal lookup(final String variable) {
        try {
            return new BigDecimal(variable).setScale(2, HALF_UP);
        } catch (NumberFormatException e) {
            throw new FailedToLookupVariableException("Failed to lookup variable %s".formatted(variable));
        }
    }
}
