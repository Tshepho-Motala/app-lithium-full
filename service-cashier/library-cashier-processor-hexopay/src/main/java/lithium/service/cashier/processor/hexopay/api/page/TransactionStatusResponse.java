package lithium.service.cashier.processor.hexopay.api.page;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatusResponse {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Checkout {
        private String token;
        @JsonProperty("redirect_url")
        private String redirectUrl;
    }
    private Checkout checkout;
}
