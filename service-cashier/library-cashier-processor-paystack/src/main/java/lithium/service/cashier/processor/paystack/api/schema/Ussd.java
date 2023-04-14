package lithium.service.cashier.processor.paystack.api.schema;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class Ussd {
    private String type;
}
