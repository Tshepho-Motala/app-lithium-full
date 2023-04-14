package lithium.service.access.provider.kycgbg.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BrandsConfigurationBrand {
	private String profileId = "";
	private String pepSancID = "";
	private String username = "";
	private String password = "";
	private String baseUrl = "";
	private Integer readTimeout;
	private Integer connectionTimeout;
}
