package lithium.service.cashier.processor.nuvei.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NuveiGetPayoutStatusRequest {
     private String merchantId;
     private String merchantSiteId;
     private String clientRequestId;
     private String timeStamp;
     private String checksum;
}
