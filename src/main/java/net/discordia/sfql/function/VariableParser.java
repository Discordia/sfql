package net.discordia.sfql.function;

import java.util.Arrays;
import java.util.regex.Pattern;
import net.discordia.sfql.domain.OHLCV;
import org.apache.commons.lang3.math.NumberUtils;

public class VariableParser {
    private static final String VARIABLE_REGEX = "([a-zA-Z]+)(\\d*)";

    private final Pattern variablePattern;

    public VariableParser() {
        this.variablePattern = Pattern.compile(VARIABLE_REGEX);
    }

    // TODO: input - list of known functions?

    public FunctionContext parse(String variable) {
        // If is number just return a number function
        if (NumberUtils.isParsable(variable)) {
            return new FunctionContext(variable, "digit", variable, 0, 0);
        }

        //
        // Parse variable (without parenthesis)
        //

        // 1. split on dot ".", the right of it is days ago,a nd if no one, its 0 days ago.
        var dotParts = variable.split("\\.");
        var fromDaysAgo = dotParts.length == 2 ? Integer.parseInt(dotParts[1]) : 0;

        // 2. Parse letters and digits out from variable
        var matcher = variablePattern.matcher(dotParts[0]);

        if (!matcher.matches()) {
            throw new VariableCouldNotBeParsedException("Regex could not parse the variable");
        }

        var letters = matcher.group(1);
        var digits = matcher.group(2);

        // 3. The digits in teh end is the period
        var period = parsePeriod(digits);

        // 4. In the letters there can be a numeric value (ohlcv) as the last letter
        //   - if so parse it out and the variableName is what is left
        //   - otherwise the variable name is all the letters and teh numeric value is set to empty string
        var variableName = letters;
        var numericValue = "";
        var lastChar = String.valueOf(letters.charAt(letters.length() - 1));
        if (isOHLCV(lastChar)) {
            numericValue = lastChar;
            variableName = letters.substring(0, letters.length() - 1);
        }

        if (variableName.isEmpty()) {
            variableName = numericValue;
        }

        // 5. Create and return FunctionContext
        return new FunctionContext(variable, variableName, numericValue, period, fromDaysAgo);
    }

    private boolean isOHLCV(final String lastChar) {
        return Arrays.stream(OHLCV.values()).sequential()
            .map(OHLCV::getName)
            .anyMatch(p -> p.equals(lastChar));
    }

    private int parsePeriod(final String digits) {
        if (!digits.trim().isEmpty()) {
            return NumberUtils.toInt(digits);
        }

        return 0;
    }
}
