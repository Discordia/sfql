package net.discordia.sfql.eval;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class EvalUtil {
    public static boolean isNotLogicalOperator(final List<String> tokens) {
        return !isAnd(tokens) && !isOr(tokens);
    }

    public static boolean isOr(final List<String> tokens) {
        return tokens.equals(List.of("or"));
    }

    public static boolean isAnd(final List<String> tokens) {
        return tokens.equals(List.of("and"));
    }

    public static boolean isInvalid(final List<String> tokens) {
        return tokens.equals(List.of("invalid"));
    }

    public static boolean isNotOperator(final String token) {
        return !Objects.equals(token, "+") &&
               !Objects.equals(token, "-") &&
               !Objects.equals(token, "*") &&
               !Objects.equals(token, "/") &&
               !Objects.equals(token, ">") &&
               !Objects.equals(token, "<");
    }

    public static boolean isNotNumber(final String token) {
        try {
            new BigDecimal(token);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
