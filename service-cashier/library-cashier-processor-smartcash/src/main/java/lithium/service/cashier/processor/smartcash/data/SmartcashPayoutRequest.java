package lithium.service.cashier.processor.smartcash.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmartcashPayoutRequest {
    private Payer payee;
    private String pin;
    private String reference;
    private TransactionRequestData transaction;
}
