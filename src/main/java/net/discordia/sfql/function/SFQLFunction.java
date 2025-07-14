package net.discordia.sfql.function;

import java.math.BigDecimal;
import java.util.Optional;

public interface SFQLFunction {
    String getKey();
    Optional<BigDecimal> apply(final FunctionContext context, final StockFrame stockFrame);
}
