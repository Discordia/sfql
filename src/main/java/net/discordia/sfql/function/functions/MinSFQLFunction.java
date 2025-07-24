package net.discordia.sfql.function.functions;

import net.discordia.sfql.domain.OHLCV;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;

import java.math.BigDecimal;
import java.util.Optional;

public class MinSFQLFunction implements SFQLFunction {
    @Override
    public String getKey() {
        return "min";
    }

    @Override
    public Optional<BigDecimal> apply(FunctionContext context, StockFrame stockFrame) {
        var period = context.period();
        var ohlcv = OHLCV.fromName(context.numericValue());
        var fromDaysAgo = context.fromDaysAgo();

        BigDecimal currentMin = null;
        for (int i = fromDaysAgo; i < (fromDaysAgo + period); i++) {
            var entryMin = stockFrame.getEntry(ohlcv, i);
            if (entryMin.isEmpty()) {
                return Optional.empty();
            }

            var min = entryMin.get();
            if (currentMin == null || min.compareTo(currentMin) < 0) {
                currentMin = min;
            }
        }

        return Optional.ofNullable(currentMin);
    }
}
