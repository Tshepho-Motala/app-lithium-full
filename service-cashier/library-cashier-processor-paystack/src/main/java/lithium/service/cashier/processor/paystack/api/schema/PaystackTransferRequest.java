package lithium.service.cashier.processor.paystack.api.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaystackTransferRequest {
    private String source;
    private Integer amount;
    private String recipient;
    private String reference;
}
