package net.discordia.sfql.function.functions;

import net.discordia.sfql.domain.OHLCV;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;

import java.math.BigDecimal;
import java.util.Optional;

// TODO: Decide on how to handle minc1
//  - is that min of index 0 and 1 or just index 0?
public class MaxSFQLFunction implements SFQLFunction {
    @Override
    public String getKey() {
        return "max";
    }

    @Override
    public Optional<BigDecimal> apply(FunctionContext context, StockFrame stockFrame) {
        var period = context.period();
        var ohlcv = OHLCV.fromName(context.numericValue());
        var fromDaysAgo = context.fromDaysAgo();

        BigDecimal currentMax = null;
        for (int i = fromDaysAgo; i < (fromDaysAgo + period); i++) {
            var entryMax = stockFrame.getEntry(ohlcv, i);
            if (entryMax.isEmpty()) {
                return Optional.empty();
            }

            var max = entryMax.get();
            if (currentMax == null || max.compareTo(currentMax) > 0) {
                currentMax = max;
            }
        }

        return Optional.ofNullable(currentMax);
    }
}
