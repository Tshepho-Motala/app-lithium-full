package lithium.service.casino.provider.rival.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BrandsConfigurationBrand {
	
	private String baseUrl = "";
	
	private String hashPassword = "";

	private String currency = "USD";

	private boolean mockActive = false;
}
