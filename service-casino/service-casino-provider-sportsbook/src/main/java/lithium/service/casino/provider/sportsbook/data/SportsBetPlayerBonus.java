package lithium.service.casino.provider.sportsbook.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SportsBetPlayerBonus {
    String playerBonusId;
    String status;
    String dateGiven;
    String dateExpiration;
    String dateStatusChanged;
    String amountGiven;
    String bonusId;
    String bonusName;
    String bonusGroup;
    String bonusCode;
}
