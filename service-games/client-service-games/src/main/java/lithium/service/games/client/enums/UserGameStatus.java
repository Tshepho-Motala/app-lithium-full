package lithium.service.games.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum UserGameStatus {
    LOCKED("locked"),
    UNLOCKED("unlocked");

    private String status;

     UserGameStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static UserGameStatus fromStatus(String status) {
        return Arrays.stream(values()).filter(v -> v.status.equalsIgnoreCase(status))
                .findFirst().orElse(null);
    }
}
