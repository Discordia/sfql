package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.discordia.sfql.domain.DateValue;
import net.discordia.sfql.domain.OHLCV;
import net.discordia.sfql.domain.StockDataEntry;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;
import static java.math.RoundingMode.HALF_UP;
import static net.discordia.sfql.function.functions.FunctionUtil.formatOHLCV;

public class XavgSFQLFunction implements SFQLFunction {
    @Override
    public String getKey() {
        return "xavg";
    }

    @Override
    public Optional<BigDecimal> apply(final FunctionContext context, final StockFrame stockFrame) {
        var entries = stockFrame.getEntries();
        var period = context.period();
        var fromDaysAgo = context.fromDaysAgo();
        var source = OHLCV.fromName(context.numericValue());
        var ema = calculateEMA(entries, period, source);
        // TODO: cache EMA

        if (ema.size() < fromDaysAgo + 1) {
            return Optional.empty();
        }

        var emaValue = formatOHLCV(ema.get(fromDaysAgo).value(), source);
        return Optional.of(emaValue);
    }

    private List<DateValue> calculateEMA(final List<StockDataEntry> entries, final int period, OHLCV source) {
        if (entries.size() < period) {
            return List.of();
        }

        var avgAcc = BigDecimal.ZERO;
        for (int i = entries.size() - 1; i >= entries.size() - period; i--) {
            avgAcc = avgAcc.add(new BigDecimal(source.fromEntry(entries.get(i))));
        }

        List<DateValue> result = new ArrayList<>();
        var previousEMA = avgAcc.divide(new BigDecimal(period), 4, HALF_UP);
        var firstEntryDateTime = entries.get(entries.size() - period).datetime();
        result.add(new DateValue(firstEntryDateTime.toLocalDate(), previousEMA));

        var multiplier = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(period + 1), 4, HALF_UP);
        for (int i = entries.size() - (period + 1); i >= 0; i--) {
            var entry = entries.get(i);
            var v = new BigDecimal(source.fromEntry(entry));
            var vMinusPrev = v.subtract(previousEMA);
            var withMultiplier = vMinusPrev.multiply(multiplier);
            var ema = withMultiplier.add(previousEMA).setScale(4, HALF_UP);
            result.add(new DateValue(entry.datetime().toLocalDate(), ema));
            previousEMA = ema;
        }

        Collections.reverse(result);
        return result;
    }
}
