package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.util.Optional;
import net.discordia.sfql.domain.OHLCV;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;

import static net.discordia.sfql.function.functions.FunctionUtil.divideOHLCV;

public class AvgSFQLFunction implements SFQLFunction {
    @Override
    public String getKey() {
        return "avg";
    }

    @Override
    public Optional<BigDecimal> apply(final FunctionContext context, final StockFrame stockFrame) {
        var period = context.period();
        var source = OHLCV.fromName(context.numericValue());

        var sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            var entry = stockFrame.getEntry(source, context.fromDaysAgo() + i);
            if (entry.isEmpty()) {
                return Optional.empty();
            }

            sum = sum.add(entry.get());
        }

        var result = divideOHLCV(source, sum, BigDecimal.valueOf(period));
        return Optional.of(result);
    }
}
