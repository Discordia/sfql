package net.discordia.sfql.function;

import java.util.HashMap;
import java.util.Map;
import net.discordia.sfql.domain.OHLCV;
import net.discordia.sfql.function.functions.*;

public class FunctionUniverse {
    private final Map<String, SFQLFunction> functions = new HashMap<>();

    public FunctionUniverse() {
        var adrFunction = new AdrSFQLFunction();
        functions.put(adrFunction.getKey(), adrFunction);

        var atrFunction = new AtrSFQLFunction();
        functions.put(atrFunction.getKey(), atrFunction);

        var avgFunction = new AvgSFQLFunction();
        functions.put(avgFunction.getKey(), avgFunction);

        var maxFunction = new MaxSFQLFunction();
        functions.put(maxFunction.getKey(), maxFunction);

        var minFunction = new MinSFQLFunction();
        functions.put(minFunction.getKey(), minFunction);

        var numberFunction = new NumberSFQLFunction();
        functions.put(numberFunction.getKey(), numberFunction);

        var openFunction = new OhlcvSFQLFunction(OHLCV.OPEN);
        functions.put(openFunction.getKey(), openFunction);

        var highFunction = new OhlcvSFQLFunction(OHLCV.HIGH);
        functions.put(highFunction.getKey(), highFunction);

        var lowFunction = new OhlcvSFQLFunction(OHLCV.LOW);
        functions.put(lowFunction.getKey(), lowFunction);

        var closeFunction = new OhlcvSFQLFunction(OHLCV.CLOSE);
        functions.put(closeFunction.getKey(), closeFunction);

        var volumeFunction = new OhlcvSFQLFunction(OHLCV.VOLUME);
        functions.put(volumeFunction.getKey(), volumeFunction);

        var xavgFunction = new XavgSFQLFunction();
        functions.put(xavgFunction.getKey(), xavgFunction);
    }

    public SFQLFunction getFunction(final String key) {
        if (!functions.containsKey(key)) {
            throw new IllegalStateException("Function " + key + " does not exist");
        }

        return functions.get(key);
    }
}
