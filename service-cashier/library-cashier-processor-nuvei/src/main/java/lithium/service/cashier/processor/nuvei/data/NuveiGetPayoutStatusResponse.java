package lithium.service.cashier.processor.nuvei.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class NuveiGetPayoutStatusResponse {
     private String merchantId;
     private String merchantSiteId;
     private String userTokenId;
     private String clientRequestId;
     private String internalRequestId;
     private String transactionId;
     private String status;
     private String amount;
     private String currency;
     private String transactionStatus;
     private String userPaymentOptionId;
     private Integer errCode;
     private String reason;
     private String gwErrorCode;
     private String gwErrorReason;
     private Integer gwExtendedErrorCode;
     private String version;
}
