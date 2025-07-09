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

    public String getName() {
        return name;
    }
}
