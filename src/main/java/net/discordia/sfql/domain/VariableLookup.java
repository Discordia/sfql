package net.discordia.sfql.domain;

import java.math.BigDecimal;

// TODO: rename to FunctionLookup - as that is more what it is?
public interface VariableLookup {
    BigDecimal lookup(String variable);
}
