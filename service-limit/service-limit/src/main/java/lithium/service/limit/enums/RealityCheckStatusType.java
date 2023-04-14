package lithium.service.limit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RealityCheckStatusType {
    KEEP_PLAYING("Keep Playing"),
    LOGOUT("Logout");

    private final String name;
}
