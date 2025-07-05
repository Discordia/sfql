package net.discordia.sfql.parse;

public class InvalidExpressionException extends RuntimeException {
    public InvalidExpressionException(final String message) {
        super(message);
    }
}
