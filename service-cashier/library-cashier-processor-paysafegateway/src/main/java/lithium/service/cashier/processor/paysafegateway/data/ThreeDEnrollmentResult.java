package lithium.service.cashier.processor.paysafegateway.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class ThreeDEnrollmentResult {
    private String id;
    private String paymentToken;
    private Long timeToLiveSeconds;
    private ThreeDCard card;
    private DeviceFingerprinting deviceFingerprinting;
}
