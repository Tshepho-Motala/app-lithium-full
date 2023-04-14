package lithium.service.cashier.processor.paystack.api.schema.deposit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paystack.api.schema.AuthorizationData;
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
public class PaystackAuthorizationResponse {
    private String amount;
    private String currency;
    private String transaction_date;
    private String status;
    private String reference;
    private String domain;
    private Object metadata;
    private String gateway_response;
    private String message;
    private String channel;
    private String ip_address;
    private Object log;
    private String fees;
    private AuthorizationData authorization;
}
