package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.util.Optional;
import net.discordia.sfql.domain.OHLCV;
import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.SFQLFunction;
import net.discordia.sfql.function.StockFrame;

// TODO: rename to something with ohlcv
public class ValueSFQLFunction implements SFQLFunction {
    private final OHLCV ohlcv;

    public ValueSFQLFunction(OHLCV ohlcv) {
        this.ohlcv = ohlcv;
    }

    @Override
    public String getKey() {
        return ohlcv.getName();
    }

    @Override
    public Optional<BigDecimal> apply(final FunctionContext context, final StockFrame frame) {
        return frame.getOHLCV(ohlcv, context.period() + context.fromDaysAgo());
    }
}
