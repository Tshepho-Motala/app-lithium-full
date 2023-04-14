package lithium.service.user.mass.action.data.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UploadType {
    PLAYER_INFO,
    BONUS_CASH,
    BONUS_INSTANT,
    BONUS_CASINOCHIP,
    BONUS_FREESPIN;

    @JsonCreator
    public static UploadType fromName(String name) {
        for (UploadType s: UploadType.values()) {
            if (s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }
}
