package lithium.service.access.provider.sphonic.schema.kyc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseData {
	private KYCResult result;
	@JsonProperty("kyc")
	private KYCResponse kycResponse;
	//ignore for now
	//private VendorData vendorData
	//private PerformanceData performanceData
	private TransactionDetails transactionDetails;
}
