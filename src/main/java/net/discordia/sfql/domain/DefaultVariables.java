package net.discordia.sfql.domain;

import java.util.HashSet;
import java.util.Set;

public class DefaultVariables {
    public static Set<String> defaultVariables() {
        Set<String> defaultVariables = new HashSet<>();
        defaultVariables.add("c");
        // TODO: add all standard
        return defaultVariables;
    }
}
