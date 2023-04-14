package lithium.service.kyc.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BOVerificationRequest {
	private String userGuid;
	private String bankCode;
	private String identifier;
	private String methodType;
}
