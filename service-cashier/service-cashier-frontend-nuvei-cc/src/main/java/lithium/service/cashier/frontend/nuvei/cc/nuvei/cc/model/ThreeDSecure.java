package lithium.service.cashier.frontend.nuvei.cc.nuvei.cc.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThreeDSecure {
    private String acsUrl;
    private String paRequest;
    private String creq;
    private String tempUrl;
}
