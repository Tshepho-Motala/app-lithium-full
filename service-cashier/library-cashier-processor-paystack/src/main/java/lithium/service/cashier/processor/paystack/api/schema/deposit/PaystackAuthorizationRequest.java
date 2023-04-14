package lithium.service.cashier.processor.paystack.api.schema.deposit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paystack.api.schema.Metadata;
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
public class PaystackAuthorizationRequest {
    private String amount;
    private String email;
    @JsonProperty("authorization_code")
    private String  authorizationCode;
    private String reference;
    private String currency;
    Metadata metadata;
}
