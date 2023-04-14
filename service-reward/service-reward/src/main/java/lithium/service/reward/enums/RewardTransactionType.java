package lithium.service.reward.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum RewardTransactionType {
    REWARD_ACTIVATE("REWARD_ACTIVATE"),
    PLAYER_BALANCE("PLAYER_BALANCE"),
    PLAYER_BALANCE_REWARD("PLAYER_BALANCE_REWARD"),
    REWARD_BALANCE ("REWARD_BALANCE"),
    REWARD_BET ("REWARD_BET"),
    REWARD_LOSS ("REWARD_LOSS"),
    REWARD_WIN ("REWARD_WIN"),
    REWARD_BET_ROLLBACK("REWARD_BET_ROLLBACK"),
    REWARD_WIN_ROLLBACK("REWARD_WIN_ROLLBACK"),
    TRANSFER_FROM_REWARD("TRANSFER_FROM_REWARD");

    @Setter
    @Getter
    @Accessors(fluent = true)
    private String value;

    public String toString() {
        return value;
    }
}
