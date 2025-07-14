package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import net.discordia.sfql.domain.OHLCV;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;

public class AvgSFQLFunction implements SFQLFunction {
    @Override
    public String getKey() {
        return "avg";
    }

    @Override
    public Optional<BigDecimal> apply(final FunctionContext context, final StockFrame stockFrame) {
        var period = context.period();
        var ohlcv = OHLCV.valueOf(context.numericValue());

        var sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sum = stockFrame.getOHLCV(ohlcv, context.fromDaysAgo() + i).map(sum::add).orElse(sum);
        }

        return Optional.of(sum.divide(BigDecimal.valueOf(period), 2,  RoundingMode.HALF_UP));
    }
}
