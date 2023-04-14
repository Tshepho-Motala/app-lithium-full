
package lithium.service.casino.provider.slotapi.api.schema.balance;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class BalanceResponse {

    private double balance;
    private String currencyCode;

}
