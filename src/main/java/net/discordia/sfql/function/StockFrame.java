package net.discordia.sfql.function;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import net.discordia.sfql.domain.OHLCV;
import net.discordia.sfql.domain.StockDataEntry;

public class StockFrame {
    private final List<StockDataEntry> entries;
    private final int frameDaysBack;

    public StockFrame(final List<StockDataEntry> entries, final int frameDaysBack) {
        this.entries = entries;
        this.frameDaysBack = frameDaysBack;
    }

    public List<StockDataEntry> getEntries() {
        return entries;
    }

    public StockDataEntry getEntry(int fromDaysAgo) {
        final int index = fromDaysAgo + frameDaysBack;
        if (entries.size() <= index) {
            throw new ArrayIndexOutOfBoundsException("index out of bounds");
        }

        return entries.get(index);
    }

    public Optional<BigDecimal> getOHLCV(OHLCV ohlcv, int fromDaysAgo) {
        int days = frameDaysBack + fromDaysAgo;

        if (entries.size() < (days + 1)) {
            return Optional.empty();
        }

        var entry = entries.get(days);

        BigDecimal result = BigDecimal.ZERO;
        switch (ohlcv) {
            case OPEN -> result = new BigDecimal(entry.open());
            case HIGH -> result = new BigDecimal(entry.high());
            case LOW -> result = new BigDecimal(entry.low());
            case CLOSE -> result = new BigDecimal(entry.close());
            case VOLUME -> result = new BigDecimal(entry.volume());
        }

        return Optional.of(result);
    }

    public int size() {
        return entries.size() - frameDaysBack;
    }
}

