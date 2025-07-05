package net.discordia.sfql.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ShuntingYardParser {
    private final ShuntingYardTokenizer tokenizer;

    public ShuntingYardParser() {
        this.tokenizer = new ShuntingYardTokenizer();
    }

    public Stack<List<String>> parse(String expr) {
        var tokens = tokenizer.tokenize(expr);
        return parse(tokens);
    }

    public Stack<List<String>> parse(List<String> tokens) {
        var stack = new Stack<String>();
        var output = new ArrayList<String>();
        var logicOutput = new Stack<List<String>>();

        for (String t : tokens) {
            if (isAndOr(t)) {
                while (!stack.empty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }

                if (!output.isEmpty()) {
                    logicOutput.push(new ArrayList<>(output));
                }
                logicOutput.push(List.of(t));
                output.clear();
            } else if (isNumberOrVariable(t)) {
                    output.add(t);
            } else if (t.equals("(")) {
                stack.push(t);
                logicOutput.push(List.of("("));
            } else if (t.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }

                stack.pop();
                if (logicOutput.peek().equals(List.of("("))) {
                    logicOutput.pop();
                } else {
                    logicOutput.push(new ArrayList<>(output));
                    output.clear();
                    logicOutput.push(List.of(")"));
                }
            } else {
                while (!stack.isEmpty() &&
                       getPrecedence(t) <= getPrecedence(stack.peek()) && hasLeftAssociativity(t) &&
                       !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }

                stack.push(t);
            }
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) {
                throw new InvalidExpressionException("This expression is invalid");
            }

            output.add(stack.pop());
        }

        if (!output.isEmpty()) {
            logicOutput.push(output);
        }

        return logicOutput;
    }

    private boolean isAndOr(final String token) {
        return token.equals("and") || token.equals("or");
    }

    private int getPrecedence(String token)
    {
        return switch (token) {
            case "´", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> -1;
        };
    }

    private boolean hasLeftAssociativity(String token) {
        return token.equals("+") || token.equals("-") ||
               token.equals("/") || token.equals("*") ||
               token.equals(">") || token.equals("<");
    }

    private static boolean isNumberOrVariable(final String token) {
        return Character.isLetterOrDigit(token.charAt(0));
    }
}

