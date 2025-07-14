package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.util.Optional;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;
import static java.math.RoundingMode.HALF_UP;
import static net.discordia.sfql.util.BigMathUtils.bigDecimal;

public class AdrSFQLFunction implements SFQLFunction {
    private static final int ADR_DECIMAL_PLACES = 4;
    private static final int ONE_HUNDRED = 100;

    @Override
    public String getKey() {
        return "adr";
    }

    @Override
    public Optional<BigDecimal> apply(final FunctionContext context, final StockFrame stockFrame) {
        var period = context.period();
        var fromDaysAgo = context.fromDaysAgo();

        var accumulatedRange = BigDecimal.ZERO;
        for (int i = fromDaysAgo; i < (fromDaysAgo + period); i++) {
            var entry = stockFrame.getEntry(i);

            var dailyRange = bigDecimal(entry.high()).divide(
                bigDecimal(entry.low()),
                ADR_DECIMAL_PLACES,
                HALF_UP
            );
            accumulatedRange = accumulatedRange.add(dailyRange);
        }

        var avgRange = accumulatedRange.divide(bigDecimal(period), ADR_DECIMAL_PLACES, HALF_UP);
        var adrDecimal = avgRange.subtract(bigDecimal(1));
        var adr = adrDecimal.multiply(bigDecimal(ONE_HUNDRED));
        return Optional.of(adr);
    }
}
