package net.discordia.sfql.function;

import net.discordia.sfql.domain.OHLCV;
import org.apache.commons.lang3.math.NumberUtils;

public class VariableParser {
    public VariableParser() {
    }

    // TODO: input - list of known functions?

    public static FunctionContext parse(String variable) {
        // If is number just return a number function
        if (NumberUtils.isParsable(variable)) {
            return new FunctionContext(variable, "digit", "digit", 0, 0);
        }

        //
        // Parse variable (without parenthesis)
        //

        // 1. split on dot ".", the right of it is days ago,a nd if no one, its 0 days ago.
        var dotParts = variable.split("\\.");
        var fromDaysAgo = dotParts.length == 2 ? Integer.parseInt(dotParts[1]) : 0;

        // 2. Parse from behind a character at the time as long as they are digits, this is the period
        var variableLeft = dotParts[0];
        var position = variableLeft.length() - 1;
        var period = 0;

        for (int i = variableLeft.length() - 1; i >= 0; i--) {
            var c = variableLeft.charAt(i);

        }

        // 3. Check one character back, this should be the numeric variable (ohlcv)
        var numericValue = "c";

        // 4. The beginning is the name of the function
        var variableName = "";
        // If nothing left, variableName == numericValue

        // Create and return FunctionContext
        return new FunctionContext(variable, variableName, numericValue, period, fromDaysAgo);
    }
}
