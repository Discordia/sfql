package net.discordia.sfql;

import java.math.BigDecimal;

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
    public void testEvalValeOnArmStockData() {
        var sfql = new SFQL();

        var armData = StockDataLoader.load("arm.json");
        var stockFrame = new StockFrame(armData.entries(), 0);
        var variableLookup = new DefaultVariableLookup(stockFrame);

        var result = sfql.evalValueMulti(DefaultVariables.defaultVariables(), variableLookup);

        assertThat(result.get("maxc126")).isEqualTo(new BigDecimal("179.93"));
        assertThat(result.get("h94")).isEqualTo(new BigDecimal("144.41"));
        assertThat(result.get("avgc10")).isEqualTo(new BigDecimal("149.48"));
        assertThat(result.get("l94")).isEqualTo(new BigDecimal("130.15"));
        assertThat(result.get("avgc7")).isEqualTo(new BigDecimal("146.96"));
        assertThat(result.get("avgc185")).isEqualTo(new BigDecimal("135.04"));
        assertThat(result.get("minl94")).isEqualTo(new BigDecimal("80.00"));
        assertThat(result.get("maxh94")).isEqualTo(new BigDecimal("168.31"));
        assertThat(result.get("avgv252")).isEqualTo(new BigDecimal("6089862"));
        assertThat(result.get("atr21")).isEqualTo(new BigDecimal("5.59"));
        assertThat(result.get("maxh252")).isEqualTo(new BigDecimal("183.74"));
        assertThat(result.get("avgv21")).isEqualTo(new BigDecimal("4741332"));
        assertThat(result.get("avgc50")).isEqualTo(new BigDecimal("138.26"));
        assertThat(result.get("c504")).isNull();
        assertThat(result.get("maxh5")).isEqualTo(new BigDecimal("152.78"));
        assertThat(result.get("c252")).isEqualTo(new BigDecimal("173.19"));
        assertThat(result.get("o1")).isEqualTo(new BigDecimal("145.94"));
        assertThat(result.get("o2")).isEqualTo(new BigDecimal("146.68"));
        assertThat(result.get("o3")).isEqualTo(new BigDecimal("149.00"));
        assertThat(result.get("h63")).isEqualTo(new BigDecimal("105.67"));
        assertThat(result.get("o5")).isEqualTo(new BigDecimal("148.93"));
        assertThat(result.get("h21")).isEqualTo(new BigDecimal("140.63"));
        assertThat(result.get("maxc252")).isEqualTo(new BigDecimal("181.18"));
        assertThat(result.get("adr21")).isEqualTo(new BigDecimal("3.68"));
        assertThat(result.get("l63")).isEqualTo(new BigDecimal("98.80"));
        assertThat(result.get("l21")).isEqualTo(new BigDecimal("137.21"));
        assertThat(result.get("minl5")).isEqualTo(new BigDecimal("140.70"));
        assertThat(result.get("minc21")).isEqualTo(new BigDecimal("135.55"));
        assertThat(result.get("minc126")).isEqualTo(new BigDecimal("85.82"));
        assertThat(result.get("c1")).isEqualTo(new BigDecimal("144.54"));
        assertThat(result.get("c2")).isEqualTo(new BigDecimal("145.94"));
        assertThat(result.get("c3")).isEqualTo(new BigDecimal("148.55"));
        assertThat(result.get("avgc200")).isEqualTo(new BigDecimal("135.82"));
        assertThat(result.get("minc252")).isEqualTo(new BigDecimal("85.82"));
        assertThat(result.get("c5")).isEqualTo(new BigDecimal("147.79"));
        assertThat(result.get("o252")).isEqualTo(new BigDecimal("186.84"));
        assertThat(result.get("minc5")).isEqualTo(new BigDecimal("144.54"));
        assertThat(result.get("minc63")).isEqualTo(new BigDecimal("96.83"));
        assertThat(result.get("avgv10")).isEqualTo(new BigDecimal("4614686"));
        assertThat(result.get("maxc5")).isEqualTo(new BigDecimal("148.55"));
        assertThat(result.get("c94")).isEqualTo(new BigDecimal("130.34"));
        assertThat(result.get("minl252")).isEqualTo(new BigDecimal("80.00"));
        assertThat(result.get("xavgc9")).isEqualTo(new BigDecimal("148.43"));
        assertThat(result.get("l1")).isEqualTo(new BigDecimal("140.70"));
        assertThat(result.get("l2")).isEqualTo(new BigDecimal("144.28"));
        assertThat(result.get("l3")).isEqualTo(new BigDecimal("145.62"));
        assertThat(result.get("h1")).isEqualTo(new BigDecimal("145.96"));
        assertThat(result.get("l5")).isEqualTo(new BigDecimal("147.05"));
        assertThat(result.get("h2")).isEqualTo(new BigDecimal("151.10"));
        assertThat(result.get("h3")).isEqualTo(new BigDecimal("149.00"));
        assertThat(result.get("o94")).isEqualTo(new BigDecimal("142.01"));
        assertThat(result.get("maxh21")).isEqualTo(new BigDecimal("168.31"));
        assertThat(result.get("h5")).isEqualTo(new BigDecimal("150.00"));
        assertThat(result.get("maxh63")).isEqualTo(new BigDecimal("168.31"));
        assertThat(result.get("l252")).isEqualTo(new BigDecimal("173.03"));
        assertThat(result.get("o126")).isEqualTo(new BigDecimal("140.80"));
        assertThat(result.get("maxc94")).isEqualTo(new BigDecimal("165.46"));
        assertThat(result.get("avgv40")).isEqualTo(new BigDecimal("4517759"));
        assertThat(result.get("h252")).isEqualTo(new BigDecimal("187.28"));
        assertThat(result.get("minc94")).isEqualTo(new BigDecimal("85.82"));
        assertThat(result.get("c126")).isEqualTo(new BigDecimal("140.49"));
        assertThat(result.get("c63")).isEqualTo(new BigDecimal("103.99"));
        assertThat(result.get("avgc25")).isEqualTo(new BigDecimal("148.59"));
        assertThat(result.get("minl21")).isEqualTo(new BigDecimal("134.57"));
        assertThat(result.get("c")).isEqualTo(new BigDecimal("147.00"));
        assertThat(result.get("c21")).isEqualTo(new BigDecimal("138.62"));
        assertThat(result.get("avgc65")).isEqualTo(new BigDecimal("130.91"));
        assertThat(result.get("h")).isEqualTo(new BigDecimal("150.15"));
        assertThat(result.get("xavgc21")).isEqualTo(new BigDecimal("147.51"));
        assertThat(result.get("maxh126")).isEqualTo(new BigDecimal("182.88"));
        assertThat(result.get("avgc150")).isEqualTo(new BigDecimal("133.41"));
        assertThat(result.get("l")).isEqualTo(new BigDecimal("146.35"));
        assertThat(result.get("minl63")).isEqualTo(new BigDecimal("95.32"));
        assertThat(result.get("o63")).isEqualTo(new BigDecimal("100.00"));
        assertThat(result.get("o")).isEqualTo(new BigDecimal("148.38"));
        assertThat(result.get("o21")).isEqualTo(new BigDecimal("137.44"));
        assertThat(result.get("minl126")).isEqualTo(new BigDecimal("80.00"));
        assertThat(result.get("maxc21")).isEqualTo(new BigDecimal("165.46"));
        assertThat(result.get("avgc100")).isEqualTo(new BigDecimal("126.35"));
        assertThat(result.get("maxc63")).isEqualTo(new BigDecimal("165.46"));
        assertThat(result.get("minc150")).isEqualTo(new BigDecimal("85.82"));
        assertThat(result.get("xavgh4")).isEqualTo(new BigDecimal("149.63"));
        assertThat(result.get("v")).isEqualTo(new BigDecimal("3797252"));
        assertThat(result.get("l126")).isEqualTo(new BigDecimal("138.02"));
        assertThat(result.get("avgc20")).isEqualTo(new BigDecimal("151.05"));
        assertThat(result.get("v1")).isEqualTo(new BigDecimal("5882783"));
        assertThat(result.get("maxc150")).isEqualTo(new BigDecimal("179.93"));
        assertThat(result.get("v2")).isEqualTo(new BigDecimal("3954206"));
        assertThat(result.get("v3")).isEqualTo(new BigDecimal("3652380"));
        assertThat(result.get("h126")).isEqualTo(new BigDecimal("141.88"));
    }

    @Test
    public void testEvalValeOnAnyStockData() {
        var sfql = new SFQL();

        var armData = StockDataLoader.load("any.json");
        var stockFrame = new StockFrame(armData.entries(), 0);
        var variableLookup = new DefaultVariableLookup(stockFrame);

        var result = sfql.evalValueMulti(DefaultVariables.defaultVariables(), variableLookup);

        assertThat(result.get("maxc126")).isEqualTo(new BigDecimal("1.09"));
        assertThat(result.get("h94")).isEqualTo(new BigDecimal("0.6735"));
        assertThat(result.get("avgc10")).isEqualTo(new BigDecimal("0.6406"));
        assertThat(result.get("l94")).isEqualTo(new BigDecimal("0.6019"));
        assertThat(result.get("avgc7")).isEqualTo(new BigDecimal("0.6552"));
        assertThat(result.get("avgc185")).isEqualTo(new BigDecimal("0.8379"));
        assertThat(result.get("minl94")).isEqualTo(new BigDecimal("0.3610"));
        assertThat(result.get("maxh94")).isEqualTo(new BigDecimal("1.04"));
        assertThat(result.get("avgv252")).isEqualTo(new BigDecimal("583556"));
        assertThat(result.get("atr21")).isEqualTo(new BigDecimal("0.07"));
        assertThat(result.get("maxh252")).isEqualTo(new BigDecimal("1.90"));
        assertThat(result.get("avgv21")).isEqualTo(new BigDecimal("1095317"));
        assertThat(result.get("avgc50")).isEqualTo(new BigDecimal("0.7027"));
        assertThat(result.get("c504")).isEqualTo(new BigDecimal("2.61"));
        assertThat(result.get("maxh5")).isEqualTo(new BigDecimal("0.8500"));
        assertThat(result.get("c252")).isEqualTo(new BigDecimal("1.02"));
        assertThat(result.get("o1")).isEqualTo(new BigDecimal("0.7600"));
        assertThat(result.get("o2")).isEqualTo(new BigDecimal("0.7000"));
        assertThat(result.get("o3")).isEqualTo(new BigDecimal("0.6107"));
        assertThat(result.get("h63")).isEqualTo(new BigDecimal("0.4689"));
        assertThat(result.get("o5")).isEqualTo(new BigDecimal("0.6000"));
        assertThat(result.get("h21")).isEqualTo(new BigDecimal("0.7500"));
        assertThat(result.get("maxc252")).isEqualTo(new BigDecimal("1.75"));
        assertThat(result.get("adr21")).isEqualTo(new BigDecimal("12.17"));
        assertThat(result.get("l63")).isEqualTo(new BigDecimal("0.4400"));
        assertThat(result.get("l21")).isEqualTo(new BigDecimal("0.7100"));
        assertThat(result.get("minl5")).isEqualTo(new BigDecimal("0.5900"));
        assertThat(result.get("minc21")).isEqualTo(new BigDecimal("0.5200"));
        assertThat(result.get("minc126")).isEqualTo(new BigDecimal("0.3970"));
        assertThat(result.get("c1")).isEqualTo(new BigDecimal("0.7619"));
        assertThat(result.get("c2")).isEqualTo(new BigDecimal("0.7000"));
        assertThat(result.get("c3")).isEqualTo(new BigDecimal("0.6461"));
        assertThat(result.get("avgc200")).isEqualTo(new BigDecimal("0.8425"));
        assertThat(result.get("minc252")).isEqualTo(new BigDecimal("0.3970"));
        assertThat(result.get("c5")).isEqualTo(new BigDecimal("0.6070"));
        assertThat(result.get("o252")).isEqualTo(new BigDecimal("1.04"));
        assertThat(result.get("minc5")).isEqualTo(new BigDecimal("0.6210"));
        assertThat(result.get("minc63")).isEqualTo(new BigDecimal("0.4456"));
        assertThat(result.get("avgv10")).isEqualTo(new BigDecimal("1793075"));
        assertThat(result.get("maxc5")).isEqualTo(new BigDecimal("0.7619"));
        assertThat(result.get("c94")).isEqualTo(new BigDecimal("0.6120"));
        assertThat(result.get("minl252")).isEqualTo(new BigDecimal("0.3610"));
        assertThat(result.get("xavgc9")).isEqualTo(new BigDecimal("0.6571"));
        assertThat(result.get("l1")).isEqualTo(new BigDecimal("0.7200"));
        assertThat(result.get("l2")).isEqualTo(new BigDecimal("0.6618"));
        assertThat(result.get("l3")).isEqualTo(new BigDecimal("0.6101"));
        assertThat(result.get("h1")).isEqualTo(new BigDecimal("0.8500"));
        assertThat(result.get("l5")).isEqualTo(new BigDecimal("0.5802"));
        assertThat(result.get("h2")).isEqualTo(new BigDecimal("0.8168"));
        assertThat(result.get("h3")).isEqualTo(new BigDecimal("0.7200"));
        assertThat(result.get("o94")).isEqualTo(new BigDecimal("0.6735"));
        assertThat(result.get("maxh21")).isEqualTo(new BigDecimal("0.8500"));
        assertThat(result.get("h5")).isEqualTo(new BigDecimal("0.6130"));
        assertThat(result.get("maxh63")).isEqualTo(new BigDecimal("1.04"));
        assertThat(result.get("l252")).isEqualTo(new BigDecimal("1.01"));
        assertThat(result.get("o126")).isEqualTo(new BigDecimal("1.06"));
        assertThat(result.get("maxc94")).isEqualTo(new BigDecimal("0.9118"));
        assertThat(result.get("avgv40")).isEqualTo(new BigDecimal("804746"));
        assertThat(result.get("h252")).isEqualTo(new BigDecimal("1.05"));
        assertThat(result.get("minc94")).isEqualTo(new BigDecimal("0.3970"));
        assertThat(result.get("c126")).isEqualTo(new BigDecimal("1.09"));
        assertThat(result.get("c63")).isEqualTo(new BigDecimal("0.4590"));
        assertThat(result.get("avgc25")).isEqualTo(new BigDecimal("0.6403"));
        assertThat(result.get("minl21")).isEqualTo(new BigDecimal("0.5200"));
        assertThat(result.get("c")).isEqualTo(new BigDecimal("0.6552"));
        assertThat(result.get("c21")).isEqualTo(new BigDecimal("0.7229"));
        assertThat(result.get("avgc65")).isEqualTo(new BigDecimal("0.6569"));
        assertThat(result.get("h")).isEqualTo(new BigDecimal("0.7538"));
        assertThat(result.get("xavgc21")).isEqualTo(new BigDecimal("0.6506"));
        assertThat(result.get("maxh126")).isEqualTo(new BigDecimal("1.12"));
        assertThat(result.get("avgc150")).isEqualTo(new BigDecimal("0.7409"));
        assertThat(result.get("l")).isEqualTo(new BigDecimal("0.6000"));
        assertThat(result.get("minl63")).isEqualTo(new BigDecimal("0.4311"));
        assertThat(result.get("o63")).isEqualTo(new BigDecimal("0.4400"));
        assertThat(result.get("o")).isEqualTo(new BigDecimal("0.7482"));
        assertThat(result.get("o21")).isEqualTo(new BigDecimal("0.7500"));
        assertThat(result.get("minl126")).isEqualTo(new BigDecimal("0.3610"));
        assertThat(result.get("maxc21")).isEqualTo(new BigDecimal("0.7619"));
        assertThat(result.get("avgc100")).isEqualTo(new BigDecimal("0.6114"));
        assertThat(result.get("maxc63")).isEqualTo(new BigDecimal("0.9118"));
        assertThat(result.get("minc150")).isEqualTo(new BigDecimal("0.3970"));
        assertThat(result.get("xavgh4")).isEqualTo(new BigDecimal("0.7680"));
        assertThat(result.get("v")).isEqualTo(new BigDecimal("2065997"));
        assertThat(result.get("l126")).isEqualTo(new BigDecimal("1.03"));
        assertThat(result.get("avgc20")).isEqualTo(new BigDecimal("0.6167"));
        assertThat(result.get("v1")).isEqualTo(new BigDecimal("3864754"));
        assertThat(result.get("maxc150")).isEqualTo(new BigDecimal("1.52"));
        assertThat(result.get("v2")).isEqualTo(new BigDecimal("6631183"));
        assertThat(result.get("v3")).isEqualTo(new BigDecimal("2276316"));
        assertThat(result.get("h126")).isEqualTo(new BigDecimal("1.13"));
    }
}
