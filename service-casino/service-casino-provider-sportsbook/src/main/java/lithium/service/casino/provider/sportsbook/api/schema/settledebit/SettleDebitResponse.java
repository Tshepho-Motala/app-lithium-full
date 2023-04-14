package lithium.service.casino.provider.sportsbook.api.schema.settledebit;

import lithium.math.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SettleDebitResponse {
    CurrencyAmount balance;
    String balanceCurrencyCode;
    Long transactionId;
}
