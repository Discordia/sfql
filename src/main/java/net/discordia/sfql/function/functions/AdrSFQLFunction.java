package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.util.Optional;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;
import static java.math.RoundingMode.HALF_UP;

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
            var dataEntry = stockFrame.getEntry(i);

            if (dataEntry.isEmpty()) {
                return Optional.empty();
            }

            var entry = dataEntry.get();
            var dailyRange = new BigDecimal(entry.high()).divide(
                new BigDecimal(entry.low()),
                ADR_DECIMAL_PLACES,
                HALF_UP
            );
            accumulatedRange = accumulatedRange.add(dailyRange);
        }

        var avgRange = accumulatedRange.divide(new BigDecimal(period), ADR_DECIMAL_PLACES, HALF_UP);
        var adrDecimal = avgRange.subtract(new BigDecimal(1));
        var adr = adrDecimal.multiply(new BigDecimal(ONE_HUNDRED)).setScale(2, HALF_UP);
        return Optional.of(adr);
    }
}
