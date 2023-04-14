package lithium.service.cashier.processor.nuvei.cc.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NuveiFrontendModel {
    private String merchantId;
    private String merchantSiteId;
    private String sessionToken;
    private String environment;
}
