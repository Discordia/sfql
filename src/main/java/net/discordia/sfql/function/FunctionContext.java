package net.discordia.sfql.function;

public record FunctionContext(
    String variable,
    String variableName,
    String numericValue,
    int period,
    int fromDaysAgo
) {
}
