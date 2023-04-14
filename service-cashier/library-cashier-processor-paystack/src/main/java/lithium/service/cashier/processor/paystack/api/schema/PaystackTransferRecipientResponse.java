package lithium.service.cashier.processor.paystack.api.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaystackTransferRecipientResponse {
    private String status;
    private String message;
    private TransferRecipientResponseData data;
}
