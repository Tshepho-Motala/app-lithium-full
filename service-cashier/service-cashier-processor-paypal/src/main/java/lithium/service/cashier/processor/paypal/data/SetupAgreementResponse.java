package lithium.service.cashier.processor.paypal.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetupAgreementResponse {
    private String iframeUrl;
    private String errorMessage;
}
