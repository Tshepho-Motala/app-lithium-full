package lithium.service.casino.provider.roxor.data.response.evolution;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameProvider {
    @JsonProperty("evolution")
    EVOLUTION("evolution"),
    ;

    private final String value;

    GameProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
