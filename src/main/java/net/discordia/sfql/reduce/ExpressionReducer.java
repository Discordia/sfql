package net.discordia.sfql.reduce;

import net.discordia.sfql.domain.VariableUniverse;
import net.discordia.sfql.parse.LogicShuntingYardParser;

public class ExpressionReducer {
    private final LogicShuntingYardParser parser = new LogicShuntingYardParser();

    public String reduce(String expr, VariableUniverse variableUniverse) {
        // parse
        // verify
        // go through and remove all expression that do have varialbes not supported in reduced mode
        // translate back to infix notation
        return null;
    }
}
