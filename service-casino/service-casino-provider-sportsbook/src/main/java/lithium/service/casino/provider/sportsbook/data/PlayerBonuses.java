package lithium.service.casino.provider.sportsbook.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerBonuses {
    String playerBonusId;
    String status;
    String dateGiven;
    String dateExpiration;
    String dateStatusChanged;
    String amountGiven;
    Bonus bonus;
}
