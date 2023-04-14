package lithium.service.cashier.processor.interswitch.api.schema;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class Initiation {
    private String amount;
    private String channel;
    private String currencyCode;
    private String paymentMethodCode;
}
