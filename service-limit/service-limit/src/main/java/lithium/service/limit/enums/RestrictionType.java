package lithium.service.limit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum
RestrictionType {
    RESTRICTION_CASINO("CASINO", "Casino"),
    RESTRICTION_LOGIN("LOGIN", "Login"),
    RESTRICTION_DEPOSIT("DEPOSIT", "Deposit"),
    RESTRICTION_WITHDRAW("WITHDRAW", "Withdraw"),
    RESTRICTION_BET_PLACEMENT("BET_PLACEMENT", "BetPlacement"),
    RESTRICTION_COMPS("COMPS", "Comps"),
    RESTRICTION_F2P("F2P", "F2P");

    @Getter
    @Accessors(fluent=true)
    private String code;

    @Getter
    @Accessors(fluent=true)
    private String restrictionName;

    @JsonCreator
    public static RestrictionType fromCode(String code) {
        for (RestrictionType r: RestrictionType.values()) {
            if (r.code.equals(code)) {
                return r;
            }
        }
        return null;
    }

}
