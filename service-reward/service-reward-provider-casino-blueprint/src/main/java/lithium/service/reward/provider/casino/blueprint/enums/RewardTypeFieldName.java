package lithium.service.reward.provider.casino.blueprint.enums;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum RewardTypeFieldName {
    ROUND_VALUE_IN_CENTS("roundValueInCents"),
    NUMBER_OF_ROUNDS("numberOfRounds");

    @Getter
    private String name;
}
