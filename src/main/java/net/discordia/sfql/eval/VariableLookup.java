package net.discordia.sfql.eval;

import java.math.BigDecimal;

public interface VariableLookup {
    BigDecimal lookup(String variable);
}
