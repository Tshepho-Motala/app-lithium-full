package lithium.service.access.provider.sphonic.iban.config;

import lombok.Data;

@Data
public class Configuration {
	private String providerName;
	private String authenticationUrl;
	private String username;
	private String password;
	private String merchantId;
	private String ibanUrl;
	private String ibanWorkflowName;
	private String ibanMode;
	private Integer connectTimeout = 60000;
	private Integer connectionRequestTimeout = 60000;
	private Integer socketTimeout = 60000;
}
