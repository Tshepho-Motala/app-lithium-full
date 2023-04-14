package lithium.service.cashier.processor.nuvei.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NuveiInitializeData {
    private String merchantId;
    private String merchantSiteId;
    private String sessionToken;
    private String env;
}
