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
public class Authentication {
	private String eci;
	private String cavv;
	private ThreeDEnrollment threeDEnrollment;
	private ThreeDResult threeDResult;
	private String signatureStatus;
	private String threeDSecureVersion;
	private String directoryServerTransactionId;

}
