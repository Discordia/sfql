package net.discordia.sfql.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShuntingYardTokenizer {
    public List<String> tokenize(final String expr) {
        List<String> output = new ArrayList<>();

        var parts = expr.toLowerCase().split("\\s+");
        for (String part : parts) {
            var isNumberOrVariable = false;

            for (char c : part.toCharArray()) {
                if (isPartOfNumberOrVariable(c)) {
                    if (isNumberOrVariable) {
                        var last = output.get(output.size() - 1);
                        output.set(output.size() - 1, last + c);
                    } else {
                        output.add(String.valueOf(c));
                    }

                    isNumberOrVariable = true;
                } else  {
                    output.add(String.valueOf(c));
                    isNumberOrVariable = false;
                }
            }
        }

        var fixedOutput = new ArrayList<String>();
        boolean canBeInfixMinus = true;
        for (int i = 0; i < output.size(); i++) {
            var part1 = output.get(i);
            fixedOutput.add(part1);

            if (canBeInfixMinus && part1.equals("-") && (i+1) < output.size()) {
                fixedOutput.remove(i);
                i++;
                var part2 = output.get(i);
                fixedOutput.add(part1 + part2);
               canBeInfixMinus = false;
            } else {
                canBeInfixMinus = isNonVariable(part1);
            }
        }

        return fixedOutput;
    }

    private boolean isNonVariable(final String part) {
        return part.equals("-") || part.equals("+") ||
               part.equals("*") || part.equals("/") ||
               part.equals(">") || part.equals("<") ||
               part.equals("and") || part.equals("or");
    }

    private static boolean isPartOfNumberOrVariable(final char c) {
        return Character.isLetterOrDigit(c) || c == '.';
    }
}
