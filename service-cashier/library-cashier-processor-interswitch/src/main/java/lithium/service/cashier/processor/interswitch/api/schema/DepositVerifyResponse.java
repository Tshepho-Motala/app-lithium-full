package lithium.service.cashier.processor.interswitch.api.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositVerifyResponse {
    private String status;
    private String transactionReference;
    private String paymentReference;
    private String responseCode;
    private String responseDescription;
}
