package lithium.service.cashier.processor.paystack.api.schema;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackUSSDChargeResponse {
    private boolean status;
    private String message;
    private UssdTransaction data;
}
