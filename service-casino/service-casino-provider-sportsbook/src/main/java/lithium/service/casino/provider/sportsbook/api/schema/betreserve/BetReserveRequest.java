package lithium.service.casino.provider.sportsbook.api.schema.betreserve;

import lithium.math.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetReserveRequest {
    String guid;
    Long reserveId;
    Long sessionId;
    Double amount;
    Long timestamp;
    String sha256;
    Long betsCount;

    public Long getAmountAsCents() {
        return CurrencyAmount.fromAmount((amount != null)? amount: 0).toCents();
    }
}
