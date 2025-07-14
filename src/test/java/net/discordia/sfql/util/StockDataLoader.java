package net.discordia.sfql.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.discordia.sfql.domain.StockData;

import java.io.IOException;

public class StockDataLoader {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.create();

    public static StockData load(String fileName) {
        try {
            var resourceStream = StockDataLoader.class.getClassLoader().getResourceAsStream(fileName);
            return OBJECT_MAPPER.readValue(resourceStream, StockData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
