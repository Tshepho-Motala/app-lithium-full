package lithium.service.casino.provider.livedealer.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BrandsConfigurationBrand {
	
	private String baseUrl = "";
	
	private String clientUser = "";
	
	private String clientPassword = "";

	private String currency = "USD";
}
