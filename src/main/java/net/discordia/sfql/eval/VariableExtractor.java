package net.discordia.sfql.eval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class VariableExtractor {
    public Set<String> extract(final ArrayList<List<String>> expr) {
        var variables = new HashSet<String>();
        for (List<String> part : expr) {
            for (String token : part) {
                if (isVariable(token)) {
                    variables.add(token);
                }
            }
        }

        return variables;
    }

    private boolean isVariable(final String token) {
        return !isLogicalOperator(token) && !isMathOperator(token) && !isNumeric(token);
    }

    private boolean isLogicalOperator(final String token) {
        return Objects.equals(token, "and") || Objects.equals(token, "or");
    }

    private boolean isMathOperator(String token) {
        return Objects.equals(token, "+") ||
               Objects.equals(token, "-") ||
               Objects.equals(token, "*") |
               Objects.equals(token, "/") ||
               Objects.equals(token, ">") ||
               Objects.equals(token, "<");
    }

    private boolean isNumeric(final String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
