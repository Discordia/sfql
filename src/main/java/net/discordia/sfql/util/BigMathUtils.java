package net.discordia.sfql.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigMathUtils {
    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100").setScale(5, RoundingMode.HALF_UP);
    public static final BigDecimal THOUSAND = new BigDecimal("1000").setScale(5, RoundingMode.HALF_UP);
    public static final BigDecimal MILLION = new BigDecimal("1000000").setScale(5, RoundingMode.HALF_UP);
    public static final BigDecimal BILLION = new BigDecimal("1000000000").setScale(5, RoundingMode.HALF_UP);
    public static final BigDecimal TRILLION = new BigDecimal("1000000000000").setScale(5, RoundingMode.HALF_UP);

    public static BigDecimal divide(BigDecimal first, BigDecimal second) {
        return first.divide(second, 5, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal first, BigDecimal second, int decimalPlaces) {
        return first.divide(second, decimalPlaces, RoundingMode.HALF_UP);
    }

    public static BigDecimal bigDecimal(String value) {
        return formatPrice(new BigDecimal(value, new MathContext(4, RoundingMode.HALF_UP)));
    }

    public static BigDecimal bigDecimal(int value) {
        return formatPrice(new BigDecimal(value, new MathContext(4, RoundingMode.HALF_UP)));
    }

    public static String formatPrice(String price) {
        var bdp = new BigDecimal(price);
        return formatPrice(bdp).toString();
    }

    public static BigDecimal formatPrice(BigDecimal price) {
        return price.compareTo(BigDecimal.ONE) >= 0 || price.compareTo(new BigDecimal("-1")) <= 0 ?
               price.setScale(2, RoundingMode.HALF_UP) :
               price.setScale(4, RoundingMode.HALF_UP);
    }

    public static BigDecimal max(final BigDecimal bd1, BigDecimal bd2, BigDecimal bd3) {
        var max12 = bd1.max(bd2);
        return max12.max(bd3);
    }
}
