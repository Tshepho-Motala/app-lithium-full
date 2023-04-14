package lithium.service.access.provider.sphonic.cruks.config;

import lombok.Data;

@Data
public class Configuration {
	private String authenticationUrl;
	private String username;
	private String password;
	private String merchantId;
	private String cruksUrl;
	private String cruksRegistrationWorkflowName;
	private String cruksLoginWorkflowName;
	private String cruksMode;
	private Integer connectTimeout = 60000;
	private Integer connectionRequestTimeout = 60000;
	private Integer socketTimeout = 60000;
}
