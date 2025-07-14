package net.discordia.sfql;

import java.math.BigDecimal;
import java.util.Set;

import net.discordia.sfql.domain.DefaultVariables;
import net.discordia.sfql.domain.ReducedVariableUniverse;
import net.discordia.sfql.domain.VariableLookupStub;
import net.discordia.sfql.function.DefaultVariableLookup;
import net.discordia.sfql.function.StockFrame;
import net.discordia.sfql.parse.LogicShuntingYardParser;
import net.discordia.sfql.util.StockDataLoader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SFQLUseCaseTest {

    //
    // Eval with stub variable lookup
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
    // Eval with real variable lookup
    //

    @Test
    public void testEvalOnArmStockData() {
        var sfql = new SFQL();

        var armData = StockDataLoader.load("arm.json");
        var stockFrame = new StockFrame(armData.entries(), 0);
        var variableLookup = new DefaultVariableLookup(stockFrame);

        var query = "c > avgc200 AND avgv10 > 200000 AND c > 100";
        var result = sfql.evalLogic(query, variableLookup);
        assertTrue(result);
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
        assertThat(result).isEqualTo("(((c + 1) > (h1 + 2)) AND (((v > v1)) AND ((c + c) > 10)))");
    }

    //
    // Eval value
    //

    @Test
    public void testEvalValue() {
        String query = "c + 1";
        var sfql = new SFQL();
        var result = sfql.evalValue(query, new VariableLookupStub());
        assertThat(result).isEqualTo(new BigDecimal("101.00"));
    }

    @Test
    public void testEvalValuePercentFromOpen() {
        String query = "((c - o) / o)";
        var sfql = new SFQL();
        var result = sfql.evalValue(query, new VariableLookupStub());
        assertThat(result).isEqualTo(new BigDecimal("0.11"));
    }

    @Test
    public void testEvalVAleOnArmStockData() {
        var sfql = new SFQL();

        var armData = StockDataLoader.load("arm.json");
        var stockFrame = new StockFrame(armData.entries(), 0);
        var variableLookup = new DefaultVariableLookup(stockFrame);

        var result = sfql.evalValueMulti(DefaultVariables.defaultVariables(), variableLookup);

        assertThat(result.get("maxc126")).isEqualTo(new BigDecimal("179.93"));
        assertThat(result.get("h94")).isEqualTo(new BigDecimal("139.50"));
        assertThat(result.get("avgc10")).isEqualTo(new BigDecimal("153.04"));
        assertThat(result.get("l94")).isEqualTo(new BigDecimal("133.16"));
        assertThat(result.get("avgc7")).isEqualTo(new BigDecimal("149.56"));
        assertThat(result.get("avgc185")).isEqualTo(new BigDecimal("135.15"));
        assertThat(result.get("minl94")).isEqualTo(new BigDecimal("80.00"));
        assertThat(result.get("maxh94")).isEqualTo(new BigDecimal("168.31"));
        assertThat(result.get("avgv252")).isEqualTo(new BigDecimal("6121477"));
        assertThat(result.get("atr21")).isEqualTo(new BigDecimal("5.68"));
        assertThat(result.get("maxh252")).isEqualTo(new BigDecimal("187.47"));
        assertThat(result.get("avgv21")).isEqualTo(new BigDecimal("4605967"));
        assertThat(result.get("avgc50")).isEqualTo(new BigDecimal("137.02"));
        assertThat(result.get("c504")).isNull();
        assertThat(result.get("maxh5")).isEqualTo(new BigDecimal("154.00"));
        assertThat(result.get("c252")).isEqualTo(new BigDecimal("182.28"));
        assertThat(result.get("o1")).isEqualTo(new BigDecimal("149.00"));
        assertThat(result.get("o2")).isEqualTo(new BigDecimal("149.59"));
        assertThat(result.get("o3")).isEqualTo(new BigDecimal("148.93"));
        assertThat(result.get("h63")).isEqualTo(new BigDecimal("107.94"));
        assertThat(result.get("o5")).isEqualTo(new BigDecimal("155.86"));
        assertThat(result.get("h21")).isEqualTo(new BigDecimal("141.58"));
        assertThat(result.get("maxc252")).isEqualTo(new BigDecimal("186.46"));
        assertThat(result.get("adr21")).isEqualTo(new BigDecimal("3.68"));
        assertThat(result.get("l63")).isEqualTo(new BigDecimal("85.19"));
        assertThat(result.get("l21")).isEqualTo(new BigDecimal("135.87"));
        assertThat(result.get("minl5")).isEqualTo(new BigDecimal("144.28"));
        assertThat(result.get("minc21")).isEqualTo(new BigDecimal("135.55"));
        assertThat(result.get("minc126")).isEqualTo(new BigDecimal("85.82"));
        assertThat(result.get("c1")).isEqualTo(new BigDecimal("148.55"));
        assertThat(result.get("c2")).isEqualTo(new BigDecimal("148.02"));
        assertThat(result.get("c3")).isEqualTo(new BigDecimal("147.79"));
        assertThat(result.get("avgc200")).isEqualTo(new BigDecimal("135.79"));
        assertThat(result.get("minc252")).isEqualTo(new BigDecimal("85.82"));
        assertThat(result.get("c5")).isEqualTo(new BigDecimal("155.09"));
        assertThat(result.get("o252")).isEqualTo(new BigDecimal("186.10"));
        assertThat(result.get("minc5")).isEqualTo(new BigDecimal("145.94"));
        assertThat(result.get("minc63")).isEqualTo(new BigDecimal("96.83"));
        assertThat(result.get("avgv10")).isEqualTo(new BigDecimal("4949242"));
        assertThat(result.get("maxc5")).isEqualTo(new BigDecimal("148.55"));
        assertThat(result.get("c94")).isEqualTo(new BigDecimal("136.70"));
        assertThat(result.get("minl252")).isEqualTo(new BigDecimal("80.00"));
        assertThat(result.get("xavgc9")).isEqualTo(new BigDecimal("149.85"));
        assertThat(result.get("l1")).isEqualTo(new BigDecimal("145.62"));
        assertThat(result.get("l2")).isEqualTo(new BigDecimal("147.80"));
        assertThat(result.get("l3")).isEqualTo(new BigDecimal("147.05"));
        assertThat(result.get("h1")).isEqualTo(new BigDecimal("149.00"));
        assertThat(result.get("l5")).isEqualTo(new BigDecimal("154.16"));
        assertThat(result.get("h2")).isEqualTo(new BigDecimal("152.78"));
        assertThat(result.get("h3")).isEqualTo(new BigDecimal("150.00"));
        assertThat(result.get("o94")).isEqualTo(new BigDecimal("139.32"));
        assertThat(result.get("maxh21")).isEqualTo(new BigDecimal("168.31"));
        assertThat(result.get("h5")).isEqualTo(new BigDecimal("157.42"));
        assertThat(result.get("maxh63")).isEqualTo(new BigDecimal("168.31"));
        assertThat(result.get("l252")).isEqualTo(new BigDecimal("179.11"));
        assertThat(result.get("o126")).isEqualTo(new BigDecimal("147.84"));
        assertThat(result.get("maxc94")).isEqualTo(new BigDecimal("165.46"));
        assertThat(result.get("avgv40")).isEqualTo(new BigDecimal("4631570"));
        assertThat(result.get("h252")).isEqualTo(new BigDecimal("188.75"));
        assertThat(result.get("minc94")).isEqualTo(new BigDecimal("85.82"));
        assertThat(result.get("c126")).isEqualTo(new BigDecimal("144.38"));
        assertThat(result.get("c63")).isEqualTo(new BigDecimal("106.59"));
        assertThat(result.get("avgc25")).isEqualTo(new BigDecimal("147.44"));
        assertThat(result.get("minl21")).isEqualTo(new BigDecimal("134.57"));
        assertThat(result.get("c")).isEqualTo(new BigDecimal("145.94"));
        assertThat(result.get("c21")).isEqualTo(new BigDecimal("140.63"));
        assertThat(result.get("avgc65")).isEqualTo(new BigDecimal("129.38"));
        assertThat(result.get("h")).isEqualTo(new BigDecimal("151.10"));
        assertThat(result.get("xavgc21")).isEqualTo(new BigDecimal("147.87"));
        assertThat(result.get("maxh126")).isEqualTo(new BigDecimal("182.88"));
        assertThat(result.get("avgc150")).isEqualTo(new BigDecimal("133.35"));
        assertThat(result.get("l")).isEqualTo(new BigDecimal("144.28"));
        assertThat(result.get("minl63")).isEqualTo(new BigDecimal("95.32"));
        assertThat(result.get("o63")).isEqualTo(new BigDecimal("85.62"));
        assertThat(result.get("o")).isEqualTo(new BigDecimal("146.68"));
        assertThat(result.get("o21")).isEqualTo(new BigDecimal("138.80"));
        assertThat(result.get("minl126")).isEqualTo(new BigDecimal("80.00"));
        assertThat(result.get("maxc21")).isEqualTo(new BigDecimal("165.46"));
        assertThat(result.get("avgc100")).isEqualTo(new BigDecimal("126.58"));
        assertThat(result.get("maxc63")).isEqualTo(new BigDecimal("165.46"));
        assertThat(result.get("minc150")).isEqualTo(new BigDecimal("85.82"));
        assertThat(result.get("xavgh4")).isEqualTo(new BigDecimal("151.50"));
        assertThat(result.get("v")).isEqualTo(new BigDecimal("3952696"));
        assertThat(result.get("l126")).isEqualTo(new BigDecimal("140.17"));
        assertThat(result.get("avgc20")).isEqualTo(new BigDecimal("150.18"));
        assertThat(result.get("v1")).isEqualTo(new BigDecimal("3652380"));
        assertThat(result.get("maxc150")).isEqualTo(new BigDecimal("179.93"));
        assertThat(result.get("v2")).isEqualTo(new BigDecimal("3848507"));
        assertThat(result.get("v3")).isEqualTo(new BigDecimal("4150262"));
        assertThat(result.get("h126")).isEqualTo(new BigDecimal("148.45"));
    }
}
