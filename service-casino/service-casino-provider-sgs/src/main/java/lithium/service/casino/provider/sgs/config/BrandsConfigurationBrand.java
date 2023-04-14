package lithium.service.casino.provider.sgs.config;

import lombok.Data;

@Data
public class BrandsConfigurationBrand {
	
	private String baseUrl = "";
	
	private String customerId = "";

	private String language = "en";

	private String currency ="USD";
	
	private String apiKey = "";
	
	private String imageUrl = "";
	
	private String apiLogin = "";
	
	private String apiPassword = "";

	private boolean mockActive = false;
}
