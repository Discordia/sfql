package net.discordia.sfql.functions;

import net.discordia.sfql.function.FunctionContext;
import net.discordia.sfql.function.VariableParser;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class VariableParserTest {
    private final VariableParser variableParser = new VariableParser();

    @Test
    public void testParse_avg() {
        var avgc21Result = variableParser.parse("avgc21");
        var avgc21Expected = new FunctionContext("avgc21", "avg", "c", 21, 0);
        assertThat(avgc21Result).isEqualTo(avgc21Expected);

        var avgc21Dot1Result = variableParser.parse("avgc20.1");
        var avgc21Dot1Expected = new FunctionContext("avgc20.1", "avg", "c", 20, 1);
        assertThat(avgc21Dot1Result).isEqualTo(avgc21Dot1Expected);
    }

    @Test
    public void testParse_xavg() {
        var avgc21Result = variableParser.parse("xavgh4");
        var avgc21Expected = new FunctionContext("xavgh4", "xavg", "h", 4, 0);
        assertThat(avgc21Result).isEqualTo(avgc21Expected);

        var avgc21Dot1Result = variableParser.parse("xavgo9.2");
        var avgc21Dot1Expected = new FunctionContext("xavgo9.2", "xavg", "o", 9, 2);
        assertThat(avgc21Dot1Result).isEqualTo(avgc21Dot1Expected);
    }

    @Test
    public void testParse_adr() {
        var avgc21Result = variableParser.parse("adr21");
        var avgc21Expected = new FunctionContext("adr21", "adr", "", 21, 0);
        assertThat(avgc21Result).isEqualTo(avgc21Expected);

        var avgc21Dot1Result = variableParser.parse("adr42.3");
        var avgc21Dot1Expected = new FunctionContext("adr42.3", "adr", "", 42, 3);
        assertThat(avgc21Dot1Result).isEqualTo(avgc21Dot1Expected);
    }
    
    @Test
    public void testParse_atr() {
        var avgc21Result = variableParser.parse("atr5");
        var avgc21Expected = new FunctionContext("atr5", "atr", "", 5, 0);
        assertThat(avgc21Result).isEqualTo(avgc21Expected);

        var avgc21Dot1Result = variableParser.parse("atr10.3");
        var avgc21Dot1Expected = new FunctionContext("atr10.3", "atr", "", 10, 3);
        assertThat(avgc21Dot1Result).isEqualTo(avgc21Dot1Expected);
    }
}
