package net.discordia.sfql.domain;

import java.util.Set;

public record ReducedQuery(
    String resultQuery,
    Set<String> unknownVariables
) {
}
