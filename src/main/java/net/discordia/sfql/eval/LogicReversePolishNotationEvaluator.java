package net.discordia.sfql.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.discordia.sfql.domain.VariableUniverse;
import net.discordia.sfql.parse.InvalidExpressionException;
import static net.discordia.sfql.eval.EvalUtil.isAnd;
import static net.discordia.sfql.eval.EvalUtil.isNotLogicalOperator;
import static net.discordia.sfql.eval.EvalUtil.isOr;

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
            if (isNotLogicalOperator(tokens)) {
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
            if (isNotLogicalOperator(tokens)) {
                var result = eval.verify(tokens, variableUniverse);
                results.add(result);
            } else if (!isAnd(tokens) && !isOr(tokens)) {
                return false;
            }
        }

        return results.stream().allMatch(p -> p == true);
    }

    public InfixExpr reduce(VariableUniverse variableUniverse) {
        var eval = new ReversePolishNotationEvaluator();
       List<List<String>> reduced = new ArrayList<>();

        for (List<String> tokens : expr) {
            if (isNotLogicalOperator(tokens)) {
                if (eval.verify(tokens, variableUniverse)) {
                    reduced.add(tokens);
                } else {
                    reduced.add(List.of("invalid"));
                }
            } else if (isAnd(tokens) || isOr(tokens)) {
                reduced.add(tokens);
            } else {
                throw new InvalidExpressionException("Invalid logical expression");
            }
        }

        return new InfixExpr(reduced);
    }
}
