package lithium.service.cashier.processor.smartcash.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.commons.nullanalysis.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmartcashPaymentRequest {
    @NotNull
    private Payer payer;
    private String reference;
    @JsonProperty("authentication_medium")
    private String authenticationMedium;
    @NotNull
    private TransactionRequestData transaction;
}
