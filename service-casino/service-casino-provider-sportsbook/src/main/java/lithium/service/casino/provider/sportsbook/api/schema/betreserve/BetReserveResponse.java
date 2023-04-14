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
public class BetReserveResponse {
    CurrencyAmount balance;
    String balanceCurrencyCode;
    CurrencyAmount bonusUsedAmount;
    Long transactionId;
}
