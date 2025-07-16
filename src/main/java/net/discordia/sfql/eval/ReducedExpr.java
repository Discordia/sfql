package net.discordia.sfql.eval;

import java.util.List;
import java.util.Set;
import java.util.Stack;
import net.discordia.sfql.domain.ReducedQuery;
import net.discordia.sfql.parse.InvalidExpressionException;
import static net.discordia.sfql.eval.EvalUtil.isAnd;
import static net.discordia.sfql.eval.EvalUtil.isInvalid;
import static net.discordia.sfql.eval.EvalUtil.isNotLogicalOperator;
import static net.discordia.sfql.eval.EvalUtil.isNotOperator;
import static net.discordia.sfql.eval.EvalUtil.isOr;

public class ReducedExpr {
    private static final String AND_EXPR = "(%s AND %s)";
    private static final String OR_EXPR = "(%s OR %s)";
    private static final String GT_EXPR = "(%s > %s)";
    private static final String LT_EXPR = "(%s < %s)";
    private static final String ADD_EXPR = "(%s + %s)";
    private static final String SUB_EXPR = "(%s - %s)";
    private static final String MULT_EXPR = "(%s * %s)";
    private static final String DIV_EXPR = "(%s / %s)";
    private static final String SINGLE_EXPR = "(%s)";

    private final List<List<String>> expr;
    private final Set<String> unknownVariables;

    public ReducedExpr(final List<List<String>> expr, final Set<String> unknownVariables) {
        this.expr = expr;
        this.unknownVariables = unknownVariables;
    }

    public ReducedQuery toReducedQuery() {
        Stack<String> stack = new Stack<>();

        for (List<String> tokens : expr) {
            if (isInvalid(tokens)) {
                stack.push(tokens.get(0));
            } else if (isNotLogicalOperator(tokens)) {
                var result = expressionToInfix(tokens);
                stack.push(result);
            } else if (isAnd(tokens)) {
                var right = stack.pop();
                var left = stack.pop();
                var result = createAndExpr(left, right);
                stack.push(result);
            } else if (isOr(tokens)) {
                var right = stack.pop();
                var left = stack.pop();
                var result = createORExpr(left, right);
                stack.push(result);
            } else {
                throw new InvalidExpressionException("Invalid logical expression");
            }
        }

        return new ReducedQuery(stack.pop(), unknownVariables);
    }

    private static String createAndExpr(final String left, final String right) {
        if (left.equals("invalid") &&  right.equals("invalid")) {
            return "";
        } else if (left.equals("invalid")) {
            return SINGLE_EXPR.formatted(right);
        } else if (right.equals("invalid")) {
            return SINGLE_EXPR.formatted(left);
        }

        return AND_EXPR.formatted(left, right);
    }

    private static String createORExpr(final String left, final String right) {
        if (left.equals("invalid") &&  right.equals("invalid")) {
            return "";
        } else if (left.equals("invalid")) {
            return SINGLE_EXPR.formatted(right);
        } else if (right.equals("invalid")) {
            return SINGLE_EXPR.formatted(left);
        }

        return OR_EXPR.formatted(left, right);
    }

    private String expressionToInfix(final List<String> tokens) {
        var stack = new Stack<String>();
        String x, y;
        String result;

        for (final String token : tokens) {
            if (!evalInternal(token, stack)) {
                switch (token) {
                    case ">":
                        x = stack.pop();
                        y = stack.pop();
                        result = GT_EXPR.formatted(y, x);
                        stack.push(result);
                        break;
                    case "<":
                        x = stack.pop();
                        y = stack.pop();
                        result = LT_EXPR.formatted(y, x);
                        stack.push(result);
                        break;
                }
            }
        }

        return stack.pop();
    }

    private boolean evalInternal(String token, Stack<String> stack) {
        String x, y;
        String result;

        if (isNotOperator(token)) {
            stack.push(token);
        } else {
            switch (token) {
                case "+":
                    x = stack.pop();
                    y = stack.pop();
                    result = ADD_EXPR.formatted(y, x);
                    stack.push(result);
                    return true;
                case "-":
                    x = stack.pop();
                    y = stack.pop();
                    result = SUB_EXPR.formatted(y, x);
                    stack.push(result);
                    return true;
                case "*":
                    x = stack.pop();
                    y = stack.pop();
                    result = MULT_EXPR.formatted(y, x);
                    stack.push(result);
                    return true;
                case "/":
                    x = stack.pop();
                    y = stack.pop();
                    result = DIV_EXPR.formatted(y, x);
                    stack.push(result);
                    return true;
            }
        }

        return false;
    }
}
