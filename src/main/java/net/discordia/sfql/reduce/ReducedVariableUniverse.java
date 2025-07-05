package net.discordia.sfql.reduce;

import net.discordia.sfql.domain.DefaultVariables;
import net.discordia.sfql.domain.VariableUniverse;

import java.util.HashSet;
import java.util.Set;

public class ReducedVariableUniverse implements VariableUniverse {
    private final Set<String> supportedVariables;

    public ReducedVariableUniverse(Set<String> supportedVariables) {
        this.supportedVariables = new HashSet<>(supportedVariables);
    }

    public VariableUniverse create() {
        Set<String> reducedSupportedVariables = new HashSet<>(DefaultVariables.defaultVariables());
        return new ReducedVariableUniverse(reducedSupportedVariables);
    }

    public VariableUniverse create(Set<String> extraSupportedVariables) {
        Set<String> reducedSupportedVariables = new HashSet<>(extraSupportedVariables);
        reducedSupportedVariables.addAll(DefaultVariables.defaultVariables());
        return new ReducedVariableUniverse(reducedSupportedVariables);
    }

    @Override
    public boolean contains(String variable) {
        return supportedVariables.contains(variable);
    }
}
