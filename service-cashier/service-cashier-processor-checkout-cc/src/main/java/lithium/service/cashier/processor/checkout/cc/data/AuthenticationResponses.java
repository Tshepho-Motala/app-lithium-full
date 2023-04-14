package lithium.service.cashier.processor.checkout.cc.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum AuthenticationResponses {
    authenticated("Y", "Customer authenticated"),
    not_authenticated("N","Customer not authenticated"),
    authentication_not_completed("A","An authentication attempt occurred but could not be completed"),
    authentication_failed("U", "Unable to perform authentication");

    @Getter
    @Accessors(fluent = true)
    private String code;
    @Getter
    @Accessors(fluent = true)
    private String description;

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    @JsonCreator
    public static AuthenticationResponses fromCode(String code) {
        if (code!= null) {
            for (AuthenticationResponses a : AuthenticationResponses.values()) {
                if (a.getCode().equalsIgnoreCase(code)) {
                    return a;
                }
            }
            log.error("Unknown checkout authentication_response code: " + code);
        }
        return null;
    }

}
