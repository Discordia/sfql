package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import net.discordia.sfql.domain.OHLCV;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;

import static net.discordia.sfql.function.functions.FunctionUtil.getAvgResultScale;

public class AvgSFQLFunction implements SFQLFunction {
    @Override
    public String getKey() {
        return "avg";
    }

    @Override
    public Optional<BigDecimal> apply(final FunctionContext context, final StockFrame stockFrame) {
        var period = context.period();
        var ohlcv = OHLCV.fromName(context.numericValue());

        var sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            var entry = stockFrame.getEntry(ohlcv, context.fromDaysAgo() + i);
            if (entry.isEmpty()) {
                return Optional.empty();
            }

            sum = sum.add(entry.get());
        }

        var scale = getAvgResultScale(ohlcv);
        var result = sum.divide(BigDecimal.valueOf(period), 2, RoundingMode.HALF_UP)
                .setScale(scale, RoundingMode.HALF_UP);
        return Optional.of(result);
    }
}
