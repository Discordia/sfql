package net.discordia.sfql.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.discordia.sfql.domain.VariableUniverse;
import net.discordia.sfql.parse.InvalidExpressionException;

public class LogicReversePolishNotationEvaluator {
    private final ArrayList<List<String>> expr;

    public LogicReversePolishNotationEvaluator(final ArrayList<List<String>> expr) {
        this.expr = expr;
    }

    public Set<String> variables() {
        var variableExtractor = new VariableExtractor();
        return variableExtractor.extract(expr);
    }

    public boolean eval(VariableLookup variableLookup) {
        var stack = new Stack<Boolean>();
        var eval = new ReversePolishNotationEvaluator();

        for (List<String> tokens : expr) {
            if (!isLogicalOperator(tokens)) {
                var result = eval.eval(tokens, variableLookup);
                stack.push(result);
            } else if (isAnd(tokens)) {
                var right = stack.pop();
                var left = stack.pop();
                var result = left && right;
                stack.push(result);
            } else if (isOr(tokens)) {
                var right = stack.pop();
                var left = stack.pop();
                var result = left || right;
                stack.push(result);
            } else {
                throw new InvalidExpressionException("Invalid logical expression");
            }
        }

        return stack.pop();
    }

    public boolean verify(VariableUniverse variableUniverse) {
        var eval = new ReversePolishNotationEvaluator();
        var results = new ArrayList<Boolean>();

        for (List<String> tokens : expr) {
            if (!isLogicalOperator(tokens)) {
                var result = eval.verify(tokens, variableUniverse);
                results.add(result);
            } else if (!isAnd(tokens) && !isOr(tokens)) {
                return false;
            }
        }

        return results.stream().allMatch(p -> p == true);
    }

    private static boolean isLogicalOperator(final List<String> tokens) {
        return isAnd(tokens) || isOr(tokens);
    }

    private static boolean isOr(final List<String> tokens) {
        return tokens.equals(List.of("or"));
    }

    private static boolean isAnd(final List<String> tokens) {
        return tokens.equals(List.of("and"));
    }
}
