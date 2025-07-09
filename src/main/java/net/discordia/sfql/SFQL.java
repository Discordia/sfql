package net.discordia.sfql;

import net.discordia.sfql.domain.VariableLookup;
import net.discordia.sfql.parse.InvalidExpressionException;
import net.discordia.sfql.parse.LogicShuntingYardParser;
import net.discordia.sfql.domain.VariableUniverse;

public class SFQL {
    private final LogicShuntingYardParser parser = new LogicShuntingYardParser();

    /**
     * Check if an expression can be parsed.
     *
     * @param expr the expression to check if it is parsable
     * @return true if the expression is parsable, false otherwise
     */
    public boolean parsable(String expr, VariableUniverse variableUniverse) {
        try {
            var evaluator = parser.parse(expr);
            return evaluator.verify(variableUniverse);
        } catch (InvalidExpressionException e) {
            return false;
        }
    }

     /**
     * Reduces a complex expression to only the parts that uses default variables.
     * <p>
     * This is so that it can be used as a pre-query to shrink the space of stocks needed to be tested.
     * </p>
     * @param expr the expression to reduce
     * @param variableUniverse the supported variables
     * @return the reduced expression
     */
    public String reduceToDefaultQuery(String expr, VariableUniverse variableUniverse) {
        var evaluator = parser.parse(expr);
        var infixExpr = evaluator.reduce(variableUniverse);
        return infixExpr.toInfixString();
    }

    /**
     * Eval expression with the provided variable lookup.
     *
     * @param expr the expression to be evaluated
     * @param variableLookup the variable lookup to find values for variables
     * @return true if expression evaluate to true with the provided variable lookup
     */
    public boolean eval(String expr, VariableLookup variableLookup) {
        var evaluator = parser.parse(expr);
        return evaluator.eval(variableLookup);
    }
}
