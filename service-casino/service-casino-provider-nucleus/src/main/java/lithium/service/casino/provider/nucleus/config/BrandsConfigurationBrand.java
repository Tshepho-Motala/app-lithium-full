package lithium.service.casino.provider.nucleus.config;

import lombok.Data;

@Data
public class BrandsConfigurationBrand {
	
	private String baseUrl = "";
	
	private String hashPassword = "";

	private String bankId = "";

	private String currency ="USD";
	
	private String apiKey = "";
	
	private String imageUrl = "";

	private boolean isMockActive = false;
}
