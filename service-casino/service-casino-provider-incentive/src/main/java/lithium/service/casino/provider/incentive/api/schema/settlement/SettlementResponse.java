package lithium.service.casino.provider.incentive.api.schema.settlement;

import lithium.math.CurrencyAmount;
import lombok.Data;

@Data
public class SettlementResponse {
    CurrencyAmount playerBalance;
    long lithiumSettlementId;
}
