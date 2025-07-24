package net.discordia.sfql.function.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.discordia.sfql.domain.OHLCV;

public class FunctionUtil {
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
        var decimalPlaces = decimalPlacesDivide(source);
        var roundingMode = roundingModeDivide(source);
        return formatOHLCV(value.divide(divisor, decimalPlaces, roundingMode), source);
    }

    private static RoundingMode roundingModeDivide(OHLCV source) {
        if (source == OHLCV.VOLUME) {
            return RoundingMode.DOWN;
        }

        return RoundingMode.HALF_UP;
    }

    private static int decimalPlacesDivide(OHLCV source) {
        if (source == OHLCV.VOLUME) {
            return 0;
        }

        return 5;
    }
}
