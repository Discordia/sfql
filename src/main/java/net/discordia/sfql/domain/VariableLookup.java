package net.discordia.sfql.domain;

import java.math.BigDecimal;
import java.util.Optional;

// TODO: rename to FunctionLookup - as that is more what it is?
public interface VariableLookup {
    Optional<BigDecimal> lookup(String variable);
}
