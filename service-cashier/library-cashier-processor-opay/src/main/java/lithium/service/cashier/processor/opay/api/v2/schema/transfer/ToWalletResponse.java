package lithium.service.cashier.processor.opay.api.v2.schema.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToWalletResponse {
    private String code;
    private String message;
    private TransactionData data;

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionData {
        private String reference;
        private String orderNo;
        private String amount;
        private String currency;
        private String fee;
        private String status;
        private String failureReason;
        private String type;
        private String phoneNumber;
        private String merchantId;
    }
}
