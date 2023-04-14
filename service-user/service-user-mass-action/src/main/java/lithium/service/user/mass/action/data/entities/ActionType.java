package lithium.service.user.mass.action.data.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ActionType {
    GRANT_BONUS,
    CHANGE_STATUS,
    CHANGE_VERIFICATION_STATUS,
    MARK_AS_TEST_PLAYER,
    ADD_PLAYER_TAGS,
    REPLACE_PLAYER_TAGS,
    REMOVE_ALL_PLAYER_TAGS,
    REMOVE_PLAYER_TAGS,
    ADD_NOTE,
    BALANCE_ADJUSTMENT,
    LIFT_PLAYER_RESTRICTIONS,
    PLACE_PLAYER_RESTRICTIONS,
    PROCESS_ACCESS_RULE,

    CHANGE_BIOMETRICS_STATUS;

    @JsonCreator
    public static ActionType fromName(String name) {
        for (ActionType s: ActionType.values()) {
            if (s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }
}
