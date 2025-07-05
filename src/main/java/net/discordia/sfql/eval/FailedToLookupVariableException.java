package net.discordia.sfql.eval;

public class FailedToLookupVariableException extends RuntimeException {
    public FailedToLookupVariableException(final String message) {
        super(message);
    }
}
