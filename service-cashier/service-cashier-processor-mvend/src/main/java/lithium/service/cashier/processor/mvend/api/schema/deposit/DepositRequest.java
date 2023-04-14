package lithium.service.cashier.processor.mvend.api.schema.deposit;

import lombok.Data;

@Data
public class DepositRequest {

    private String reference;
    private String currency;
    private String amount;
    private String processor;

}
