package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.discordia.sfql.domain.StockDataEntry;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;
import static java.math.RoundingMode.HALF_UP;

public class AtrSFQLFunction implements SFQLFunction {
    @Override
    public String getKey() {
        return "atr";
    }

    @Override
    public Optional<BigDecimal> apply(final FunctionContext context, final StockFrame stockFrame) {
        var period = context.period();
        var fromDaysAgo = context.fromDaysAgo();

        return calculateATR(stockFrame, period, fromDaysAgo);
    }

    // TODO: double check ATR calculations
    private Optional<BigDecimal> calculateATR(final StockFrame stockFrame, int period, int fromDaysAgo) {
        LinkedList<BigDecimal> trValues = new LinkedList<>();
        LinkedList<StockDataEntry> atrEntries = new LinkedList<>();
        for (int i = fromDaysAgo; i < Math.min(fromDaysAgo + period + 1, stockFrame.size()); i++) {
            var dataEntry = stockFrame.getEntry(i);
            if (dataEntry.isEmpty()) {
                return Optional.empty();
            }

            atrEntries.push(dataEntry.get());
        }

        var atr = new BigDecimal(0);
        for (int i = 0; i < atrEntries.size(); i++) {
            var tr = new BigDecimal(stockFrame.getEntry(i).get().high())
                .subtract(new BigDecimal(stockFrame.getEntry(i).get().low()));

            if (i < period) {
                trValues.add(tr);
            } else {
                var prevAtr = new BigDecimal(0);
                for (BigDecimal atrValue : trValues) {
                    prevAtr = prevAtr.add(atrValue);
                }

                final BigDecimal periodMult = new BigDecimal("1").divide(
                    new BigDecimal(Integer.toString(period)),
                    2,
                    HALF_UP
                );
                prevAtr = prevAtr.multiply(periodMult);

                atr = (prevAtr
                    .multiply(new BigDecimal(period - 1))
                    .add(tr))
                    .divide(new BigDecimal(period), 2, HALF_UP);
            }
        }

        return Optional.of(atr);
    }
}
