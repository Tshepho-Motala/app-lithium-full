package lithium.service.promo.pr.sportsbook.sbt.dto;

import java.util.stream.Stream;

public enum TransactionType {
    SPORTS_WIN("SPORTS_WIN"),
    SPORTS_RESERVE("SPORTS_RESERVE");

    public String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TransactionType fromType(String type) {
        return Stream.of(values()).filter(t -> t.type.equalsIgnoreCase(type)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return type;
    }
}
