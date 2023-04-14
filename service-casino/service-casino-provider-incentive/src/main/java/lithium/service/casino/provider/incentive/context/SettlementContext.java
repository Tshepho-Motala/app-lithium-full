package lithium.service.casino.provider.incentive.context;

import lithium.service.casino.provider.incentive.api.schema.settlement.SettlementResultEnum;
import lithium.service.casino.provider.incentive.storage.entities.Bet;
import lithium.service.casino.provider.incentive.storage.entities.BetSelection;
import lithium.service.casino.provider.incentive.storage.entities.Settlement;
import lithium.math.CurrencyAmount;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SettlementContext {
    private String domainName;
    private Bet bet;
    private Settlement settlement;
    private CurrencyAmount balance;
    private List<BetSelection> betSelections;
    private SettlementResultEnum result;
}
