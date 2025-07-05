package net.discordia.sfql.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import net.discordia.sfql.eval.LogicReversePolishNotationEvaluator;

public class LogicShuntingYardParser {
    public LogicReversePolishNotationEvaluator parse(String expr) {
        var parser = new ShuntingYardParser();
        var result = parser.parse(expr);
        return parse(result);
    }

    public LogicReversePolishNotationEvaluator parse(Stack<List<String>> terms) {
        var stack = new Stack<List<String>>();
        var output = new ArrayList<List<String>>();

        for (List<String> term : terms) {
            if (isExpr(term)) {
                output.add(term);
            } else if (term.equals(List.of("("))) {
                stack.push(term);
            } else if (term.equals(List.of(")"))) {
                while (!stack.isEmpty() && !stack.peek().equals(List.of("("))) {
                    output.add(stack.pop());
                }

                stack.pop();
            } else {
                while (!stack.isEmpty() && !stack.peek().equals(List.of("("))) {
                    output.add(stack.pop());
                }

                stack.push(term);
            }
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals(List.of("("))) {
                throw new InvalidExpressionException("This expression is invalid");
            }

            output.add(stack.pop());
        }

        return new LogicReversePolishNotationEvaluator(output);
    }

    private boolean isExpr(final List<String> term) {
        return !term.equals(List.of("(")) && !term.equals(List.of(")")) &&
               !term.equals(List.of("and")) && !term.equals(List.of("or"));
    }
}
