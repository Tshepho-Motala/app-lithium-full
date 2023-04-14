package lithium.service.casino.provider.sportsbook.api.schema.betdebitreserve;

import lithium.math.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetDebitReserveResponse {
    CurrencyAmount balance;
    String balanceCurrencyCode;
    Long transactionId;
}
