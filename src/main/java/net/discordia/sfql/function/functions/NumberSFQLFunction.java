package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.util.Optional;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;

public class NumberSFQLFunction implements SFQLFunction {

    @Override
    public String getKey() {
        return "digit";
    }

    @Override
    public Optional<BigDecimal> apply(final FunctionContext context, final StockFrame frame) {
        return Optional.of(new BigDecimal(context.variableName()));
    }
}
