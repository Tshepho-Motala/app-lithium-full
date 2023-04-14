package lithium.service.casino.provider.roxor.api.schema.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCategory {
    EC_400(400, "Bad Request. The caller should not repeat the request without modification."),
    EC_401(401, "Not Logged In. Provided credentials may be invalid or have expired."),
    EC_402(402, "Insufficient Funds."),
    EC_404(404, "Not Found."),
    EC_440(440, "Loss Limit."),
    EC_441(441, "Turnover Limit."),
    EC_442(442, "Lifetime Deposit."),
    EC_443(443, "Time Limit."),
    EC_444(444, "Deposit Limit."),
    EC_445(445, "Geolocation Error."),
    EC_500(500, "Runtime Error. An unexpected error occured.");

    @Setter
    @Getter
    @Accessors(fluent = true)
    private Integer category;
    @Getter
    @Setter
    @Accessors(fluent = true)
    private String displayMessage;

    @JsonCreator
    public static ErrorCategory fromCategory(int category) {
        for (ErrorCategory ec : ErrorCategory.values()) {
            if (ec.category == category) {
                return ec;
            }
        }
        return null;
    }
}
