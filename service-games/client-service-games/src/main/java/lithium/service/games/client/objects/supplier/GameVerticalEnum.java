package lithium.service.games.client.objects.supplier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum GameVerticalEnum {

    LIVE("live"),
    RNG("rng"),
    SLOTS("slots");

    String value;

    GameVerticalEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static GameVerticalEnum decode(final String value) {
        return Stream.of(GameVerticalEnum.values()).filter(gameVerticalEnum -> gameVerticalEnum.value.equals(value)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return value;
    }
}
