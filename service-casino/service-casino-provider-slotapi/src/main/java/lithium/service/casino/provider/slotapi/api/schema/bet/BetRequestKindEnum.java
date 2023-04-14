package lithium.service.casino.provider.slotapi.api.schema.bet;

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
}
