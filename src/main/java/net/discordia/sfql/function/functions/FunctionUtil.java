package net.discordia.sfql.function.functions;

import net.discordia.sfql.domain.OHLCV;

public class FunctionUtil {

    public static int getAvgResultScale(OHLCV ohlcv) {
        if (ohlcv == OHLCV.VOLUME) {
            return 0;
        }

        return 2;
    }
}
