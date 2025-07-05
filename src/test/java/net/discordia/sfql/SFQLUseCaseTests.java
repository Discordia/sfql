package net.discordia.sfql;

import net.discordia.sfql.parse.LogicShuntingYardParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SFQLUseCaseTests {

    //
    // Eval
    //

    @Test
    public void testEvalComplexLogicalExpression() {
        String query = "(c + 1 > (h1 + 2)) AND ((v > v1 OR avgv10 > 200000) AND c + c > 10)";
        var eval = new SFQL();
        var result = eval.eval(query, new VariableLookupStub());
        assertTrue(result);
    }

    // https://stackoverflow.com/questions/16380234/handling-extra-operators-in-shunting-yard/16392115#16392115
    @Test
    public void testEvalComplexLogicalExpressionWithPrefixMinus() {
        String query = "v > v1 AND ((o - c1) /c1) < -0.06";
        var eval = new SFQL();
        var result = eval.eval(query, new VariableLookupStub());
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
    // Verify
    //




    //
    // Reduce
    //
}
