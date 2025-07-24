package net.discordia.sfql.eval;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;
import net.discordia.sfql.domain.VariableLookup;
import net.discordia.sfql.domain.VariableUniverse;
import net.discordia.sfql.parse.InvalidExpressionException;

import static java.math.RoundingMode.HALF_UP;
import static net.discordia.sfql.eval.EvalUtil.isNotNumber;
import static net.discordia.sfql.eval.EvalUtil.isNotOperator;

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
                        x = lookupVariable(variableLookup, stack.pop());
                        y = lookupVariable(variableLookup, stack.pop());
                        value = y.compareTo(x) > 0;
                        result = String.valueOf(value);
                        stack.push(result);
                        break;
                    case "<":
                        x = lookupVariable(variableLookup, stack.pop());
                        y = lookupVariable(variableLookup, stack.pop());
                        value = y.compareTo(x) < 0;
                        result = String.valueOf(value);
                        stack.push(result);
                        break;
                    // TODO: "="  ?
                }
            }
        }

        return Boolean.parseBoolean(stack.pop());
    }

    public BigDecimal evalValue(List<String> tokens, VariableLookup variableLookup) {
        var stack = new Stack<String>();

        for (final String token : tokens) {
            evalInternal(token, stack, variableLookup);
        }

        return variableLookup.lookup(stack.pop()).orElse(null);
    }

    public String verify(final List<String> tokens, final VariableUniverse variableUniverse) {
        for (String token : tokens) {
            if (isNotOperator(token) && isNotNumber(token)) {
                if (!variableUniverse.contains(token)) {
                    return token;
                }
            }
        }

        return null;
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
                    x = lookupVariable(variableLookup, stack.pop());
                    y = lookupVariable(variableLookup, stack.pop());
                    value = x.add(y);
                    result = String.valueOf(value);
                    stack.push(result);
                    return true;
                case "-":
                    x = lookupVariable(variableLookup, stack.pop());
                    y = lookupVariable(variableLookup, stack.pop());
                    value = y.subtract(x);
                    result = String.valueOf(value);
                    stack.push(result);
                    return true;
                case "*":
                    x = lookupVariable(variableLookup, stack.pop());
                    y = lookupVariable(variableLookup, stack.pop());
                    value = x.multiply(y);
                    result = String.valueOf(value);
                    stack.push(result);
                    return true;
                case "/":
                    x = lookupVariable(variableLookup, stack.pop());
                    y = lookupVariable(variableLookup, stack.pop());
                    value = y.divide(x, 2, HALF_UP);
                    result = String.valueOf(value);
                    stack.push(result);
                    return true;
            }
        }

        return false;
    }

    private static BigDecimal lookupVariable(VariableLookup variableLookup, String value) {
        return variableLookup.lookup(value)
                .orElseThrow(() -> new InvalidExpressionException("Failed to lookup value: " + value));
    }
}
