package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.discordia.sfql.domain.OHLCV;

public class FunctionUtil {
    public static int decimalPlaces(OHLCV source) {
        if (source == OHLCV.VOLUME) {
            return 0;
        }

        return 2;
    }

    public static BigDecimal formatOHLCV(BigDecimal value, OHLCV ohlcv) {
        if (ohlcv == OHLCV.VOLUME) {
            return value.setScale(0, RoundingMode.HALF_UP);
        }

        return formatPrice(value);
    }

    public static BigDecimal formatPrice(BigDecimal price) {
        return price.compareTo(BigDecimal.ONE) >= 0 || price.compareTo(new BigDecimal("-1")) <= 0 ?
               price.setScale(2, RoundingMode.HALF_UP) :
               price.setScale(4, RoundingMode.HALF_UP);
    }

    public static BigDecimal divideOHLCV(OHLCV source, BigDecimal value, BigDecimal divisor) {
        var decimalPlaces = decimalPlaces(source);
        var roundingMode = roundingMode(source);
        return formatOHLCV(value.divide(divisor, decimalPlaces, roundingMode), source);
    }

    public static RoundingMode roundingMode(OHLCV source) {
        if (source == OHLCV.VOLUME) {
            return RoundingMode.DOWN;
        }

        return RoundingMode.HALF_UP;
    }
}
