package lithium.service.user.mass.action.data.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DataError {
    DEFAULT_AMOUNT_NOT_PROVIDED,
    INVALID_AMOUNT_PROVIDED,
    UNABLE_TO_GRANT_BONUS,
    USER_NOT_FOUND,
    USER_FOUND_ON_ANOTHER_DOMAIN,
    UNABLE_TO_RETRIEVE_USER, 
    INVALID_ACCOUNT_CODE;

    @JsonCreator
    public static DataError fromName(String name) {
        for (DataError s: DataError.values()) {
            if (s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }
}