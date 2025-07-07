package net.discordia.sfql.eval;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import net.discordia.sfql.domain.VariableUniverse;
import static java.math.RoundingMode.HALF_UP;

public class ReversePolishNotationEvaluator {

    public boolean eval(List<String> tokens, VariableLookup variableLookup) {
        var stack = new Stack<String>();
        BigDecimal x, y;
        String result;
        boolean value;

        for (final String token : tokens) {
            if (!evalInternal(token, stack, variableLookup)) {
                switch (token) {
                    case ">":
                        x = variableLookup.lookup(stack.pop());
                        y = variableLookup.lookup(stack.pop());
                        value = y.compareTo(x) > 0;
                        result = String.valueOf(value);
                        stack.push(result);
                        break;
                    case "<":
                        x = variableLookup.lookup(stack.pop());
                        y = variableLookup.lookup(stack.pop());
                        value = y.compareTo(x) < 0;
                        result = String.valueOf(value);
                        stack.push(result);
                        break;
                }
            }
        }

        return Boolean.parseBoolean(stack.pop());
    }

    public Boolean verify(final List<String> tokens, final VariableUniverse variableUniverse) {
        for (String token : tokens) {
            if (isNotOperator(token) && isNotNumber(token)) {
                if (!variableUniverse.contains(token)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean evalInternal(String token, Stack<String> stack, VariableLookup variableLookup) {
        BigDecimal x, y;
        String result;
        BigDecimal value;

        if (isNotOperator(token)) {
            stack.push(token);
        } else {
            switch (token) {
                case "+":
                    x = variableLookup.lookup(stack.pop());
                    y = variableLookup.lookup(stack.pop());
                    value = x.add(y);
                    result = String.valueOf(value);
                    stack.push(result);
                    return true;
                case "-":
                    x = variableLookup.lookup(stack.pop());
                    y = variableLookup.lookup(stack.pop());
                    value = y.subtract(x);
                    result = String.valueOf(value);
                    stack.push(result);
                    return true;
                case "*":
                    x = variableLookup.lookup(stack.pop());
                    y = variableLookup.lookup(stack.pop());
                    value = x.multiply(y);
                    result = String.valueOf(value);
                    stack.push(result);
                    return true;
                case "/":
                    x = variableLookup.lookup(stack.pop());
                    y = variableLookup.lookup(stack.pop());
                    value = y.divide(x, 2, HALF_UP);
                    result = String.valueOf(value);
                    stack.push(result);
                    return true;
            }
        }

        return false;
    }

    private static boolean isNotOperator(final String token) {
        return !Objects.equals(token, "+") &&
               !Objects.equals(token, "-") &&
               !Objects.equals(token, "*") &&
               !Objects.equals(token, "/") &&
               !Objects.equals(token, ">") &&
               !Objects.equals(token, "<");
    }

    private boolean isNotNumber(final String token) {
        try {
            new BigDecimal(token);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
