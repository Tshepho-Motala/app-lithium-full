package lithium.service.cashier.processor.hexopay.api.gateway.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionData {
    @JsonProperty("receipt_text")
    private String[] receiptText;
    private String[] contract;
    @JsonProperty("avs_cvc_verification")
    private AvsCvcVerificationRequest avsCvcVerification;
}
