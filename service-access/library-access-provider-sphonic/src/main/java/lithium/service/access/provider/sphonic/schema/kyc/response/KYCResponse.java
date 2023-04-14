package lithium.service.access.provider.sphonic.schema.kyc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KYCResponse {
	private Summary summary;
	private AggregateKycResult aggregateKycResult;
	private VendorSpecificKycResult vendorSpecificKycResult;
}
