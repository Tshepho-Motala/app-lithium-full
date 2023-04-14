package lithium.service.cashier.processor.paypal.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AgreementTokenResponse {
    private String token;
    private String errorMessage;
}
