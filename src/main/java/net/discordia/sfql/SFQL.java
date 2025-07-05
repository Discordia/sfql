package net.discordia.sfql;

import net.discordia.sfql.eval.VariableLookup;
import net.discordia.sfql.parse.InvalidExpressionException;
import net.discordia.sfql.parse.LogicShuntingYardParser;
import net.discordia.sfql.reduce.ExpressionReducer;
import net.discordia.sfql.domain.VariableUniverse;

public class SFQL {
    private final LogicShuntingYardParser parser = new LogicShuntingYardParser();
    private final ExpressionReducer reducer = new ExpressionReducer();

    /**
     * Check if an expression can be parsed.
     *
     * @param expr the expression to check if it is parsable
     * @return true if the expression is parsable, false otherwise
     */
    public boolean parsable(String expr, VariableUniverse variableUniverse) {
        try {
            var eval = parser.parse(expr);
            return eval.verify(variableUniverse);
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
        return reducer.reduce(expr, variableUniverse);
    }

    /**
     * Eval expression with the provided variable lookup.
     *
     * @param expr the expression to be evaluated
     * @param variableLookup the variable lookup to find values for variables
     * @return true if expression evaluate to true with the provided variable lookup
     */
    public boolean eval(String expr, VariableLookup variableLookup) {
        var eval = parser.parse(expr);
        return eval.eval(variableLookup);
    }
}
