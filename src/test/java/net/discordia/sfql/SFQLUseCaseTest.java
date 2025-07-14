package net.discordia.sfql;

import net.discordia.sfql.domain.ReducedVariableUniverse;
import net.discordia.sfql.parse.LogicShuntingYardParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SFQLUseCaseTest {

    //
    // Eval
    //

    @Test
    public void testEvalComplexLogicalExpression() {
        String query = "(c + 1 > (h1 + 2)) AND ((v > v1 OR avgv10 > 200000) AND c + c > 10)";
        var sfql = new SFQL();
        var result = sfql.evalLogic(query, new VariableLookupStub());
        assertTrue(result);
    }

    // https://stackoverflow.com/questions/16380234/handling-extra-operators-in-shunting-yard/16392115#16392115
    @Test
    public void testEvalComplexLogicalExpressionWithPrefixMinus() {
        String query = "v > v1 AND ((o - c1) /c1) < -0.06";
        var sfql = new SFQL();
        var result = sfql.evalLogic(query, new VariableLookupStub());
        assertTrue(result);
    }

    @Test
    public void testExtractVariablesComplexLogicalExpression() {
        String query = "(c + 1 > (h1 + 2)) AND ((v > v1 OR avgv10 > 200000) AND c + c > 10)";
        var eval = new LogicShuntingYardParser().parse(query);
        var variables = eval.variables();
        assertThat(variables).containsExactlyInAnyOrder("c", "h1", "v", "v1", "avgv10");
    }


    //
    // Parsable
    //

    @Test
    public void testParsableQuery() {
        String query = "(c + 1 > (h1 + 2)) AND ((v > v1 OR avgv10 > 200000) AND c + c > 10)";
        var eval = new SFQL();
        var result = eval.parsable(query, ReducedVariableUniverse.create());
        assertTrue(result);
    }

    @Test
    public void testNonParsableQuery_missingParenthesis() {
        String query = "(c + 1 > (h1 + 2) AND ((v > v1 OR avgv10 > 200000) AND c + c > 10)";
        var eval = new SFQL();
        var result = eval.parsable(query, ReducedVariableUniverse.create());
        assertFalse(result);
    }

    @Test
    public void testNonParsableQuery_unknowVariable() {
        String query = "(c + 1 > (h1 + 2)) AND ((v > v1 OR ph > 200000) AND c + c > 10)";
        var eval = new SFQL();
        var result = eval.parsable(query, ReducedVariableUniverse.create());
        assertFalse(result);
    }


    //
    // Reduce
    //

    @Test
    public void testReduceQuery() {
        String query = "(c + 1 > (h1 + 2)) AND ((v > v1 OR ph > 200000) AND c + c > 10)";
        var eval = new SFQL();
        var result = eval.reduceToDefaultQuery(query, ReducedVariableUniverse.create());
        System.out.println(result);
    }
}
