package net.discordia.sfql.domain;

public enum OHLCV {
    OPEN("o"),
    HIGH("h"),
    LOW("l"),
    CLOSE("c"),
    VOLUME("v");

    private final String name;

    OHLCV(String name) {
        this.name = name;
    }

    public static OHLCV fromName(String name) {
        for (OHLCV value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }

        throw new IllegalArgumentException("Unknown OHLCV name: " + name);
    }

    public String getName() {
        return name;
    }

    public String fromEntry(StockDataEntry entry) {
        if (this == OPEN) {
            return entry.open();
        } else if (this == HIGH) {
            return entry.high();
        } else if (this == LOW) {
            return entry.low();
        } else if (this == CLOSE) {
            return entry.close();
        } else if (this == VOLUME) {
            return entry.volume();
        }

        throw new IllegalArgumentException("Unknown OHLCV value: " + this);
    }
}
