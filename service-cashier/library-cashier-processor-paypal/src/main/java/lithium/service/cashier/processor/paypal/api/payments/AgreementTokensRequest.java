package lithium.service.cashier.processor.paypal.api.payments;

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
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgreementTokensRequest {
    private String description;
    private Payer payer;
    private Plan plan;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Payer {
        @JsonProperty("payment_method")
        private String paymentMethod;
    }
}
