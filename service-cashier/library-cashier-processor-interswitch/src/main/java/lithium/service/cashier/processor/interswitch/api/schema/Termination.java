package lithium.service.cashier.processor.interswitch.api.schema;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class Termination {
    private String amount;
    private String countryCode;
    private String currencyCode;
    private String entityCode;
    private String paymentMethodCode;
    private AccountReceivable accountReceivable;
}
