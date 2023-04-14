package lithium.service.cashier.processor.hexopay.api.gateway;

import lithium.service.cashier.processor.hexopay.api.gateway.data.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class TransactionsResponse {
    private Transaction[] transactions;
}
