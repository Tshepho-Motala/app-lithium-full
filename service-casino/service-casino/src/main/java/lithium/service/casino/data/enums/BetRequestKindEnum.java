package lithium.service.casino.data.enums;

import lithium.service.casino.CasinoTranType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum BetRequestKindEnum {
    BET ("BET"),
    FREE_BET ("FREE_BET");

    @Setter
    @Getter
    @Accessors(fluent = true)
    private String value;

    public String toString() {
        return value;
    }

    public static BetRequestKindEnum fromCasinoTranType(CasinoTranType type) {
        switch (type) {
            case CASINO_BET:
                return BetRequestKindEnum.BET;
            case REWARD_BET:
            case CASINO_BET_FREESPIN:
            case CASINO_BET_FREEGAME:
                return BetRequestKindEnum.FREE_BET;
            default: return null;
        }
    }
}
