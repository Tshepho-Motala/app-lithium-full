package lithium.service.reward.provider.casino.roxor;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum RewardTypeFieldName {
    VALUE_IN_CENTS("valueInCents"),
    NUMBER_OF_ROUNDS("numberOfRounds"),
    ROUND_VALUE_IN_CENTS("roundValueInCents");

    @Getter
    private String name;
}
