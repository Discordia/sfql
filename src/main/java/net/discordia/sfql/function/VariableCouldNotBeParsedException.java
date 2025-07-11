package net.discordia.sfql.function;

public class VariableCouldNotBeParsedException extends RuntimeException {
    public VariableCouldNotBeParsedException() {
    }

    public VariableCouldNotBeParsedException(final String message) {
        super(message);
    }

    public VariableCouldNotBeParsedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public VariableCouldNotBeParsedException(final Throwable cause) {
        super(cause);
    }

    public VariableCouldNotBeParsedException(
        final String message,
        final Throwable cause,
        final boolean enableSuppression,
        final boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
