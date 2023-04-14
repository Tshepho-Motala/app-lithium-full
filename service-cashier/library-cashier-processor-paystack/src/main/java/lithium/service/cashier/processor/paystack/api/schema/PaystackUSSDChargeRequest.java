package lithium.service.cashier.processor.paystack.api.schema;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Data
@Builder
@ToString
public class PaystackUSSDChargeRequest {

    private String email;
    private int amount;
    private String reference;
    private Ussd ussd;
    private UssdDepositChargeRequestMetadata metadata;


}
