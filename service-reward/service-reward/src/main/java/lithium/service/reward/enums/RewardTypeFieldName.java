package lithium.service.reward.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Arrays;

@ToString
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum RewardTypeFieldName {
    VALUE_IN_CENTS("valueInCents"),
    CURRENCY_CODE("currencyCode");

    @Getter
    @Accessors(fluent = true)
    private String rewardTypeFieldName;

    @JsonCreator
    public static RewardTypeFieldName fromType(String type) {
        return Arrays.stream(RewardTypeFieldName.values()).filter(rt -> rt.rewardTypeFieldName().equalsIgnoreCase(type))
                .findFirst().orElse(null);
    }
}
