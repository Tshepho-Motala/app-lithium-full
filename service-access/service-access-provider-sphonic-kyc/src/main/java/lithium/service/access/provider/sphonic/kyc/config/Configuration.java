package lithium.service.access.provider.sphonic.kyc.config;

import lombok.Data;

@Data
public class Configuration {
	private String authenticationUrl;
	private String username;
	private String password;
	private String merchantId;
	private String kycUrl;
	private String kycWorkflowName;
	private Boolean partialVerification;
	private Integer connectTimeout = 60000;
	private Integer connectionRequestTimeout = 60000;
	private Integer socketTimeout = 60000;
	private Boolean skipOnAddressVerified;
}
