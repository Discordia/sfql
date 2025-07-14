package net.discordia.sfql.function;

import java.math.BigDecimal;
import java.util.Optional;
import net.discordia.sfql.domain.VariableLookup;

public class DefaultVariableLookup implements VariableLookup {
    private final StockFrame stockFrame;
    private final VariableParser variableParser;
    private final FunctionUniverse functionUniverse;

    public DefaultVariableLookup(StockFrame stockFrame) {
        this.stockFrame = stockFrame;
        this.variableParser = new VariableParser();
        this.functionUniverse = new FunctionUniverse();
    }

    @Override
    public Optional<BigDecimal> lookup(final String variable) {
        var functionContext = variableParser.parse(variable);
        var function = functionUniverse.getFunction(functionContext.variableName());
        return function.apply(functionContext, stockFrame);
    }
}
