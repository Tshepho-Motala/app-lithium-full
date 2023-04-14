package lithium.service.cashier.frontend.nuvei.cc.nuvei.cc.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Fingerprint {
    private String methodUrl;
    private String threeDSMethodData;
    private String transactionId;
}
