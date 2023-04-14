package lithium.service.promo.pr.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;

@ToString
@AllArgsConstructor
public enum ExtraFieldType {
    REFERRER_GUID("referrerGuid"),
    PROMO_CODE("promoCode"),
    DAYS_OF_WEEK("daysOfWeek"),
    GRANULARITY("granularity"),
    CONSECUTIVE_LOGINS("consecutiveLogins");

    @Getter
    @Setter
    private String type;

    public static ExtraFieldType fromType(String type) {
        return Arrays.stream(ExtraFieldType.values())
                .filter(f -> f.type.equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
