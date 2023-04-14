package lithium.service.casino.provider.supera.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

@Data
public class BrandsConfigurationBrand {
	
	private String baseUrl = "";

	private String currency ="USD";
	
	private String apiKey = "";
	
	private String imageUrl = "";
	
	private String saltKey = "";
	
	private String apiUser;
	
	private String apiPassword;
	
	private String gameListUrl = "";

	private boolean isMockActive = false;
	
	public String getReportsUrl() {
		return "/!v8/api/report.js";
	}
	
	public String getAdminUrl() {
		return "/!v8/api/admin.js";
	}
	
	public String getFreeroundsUrl() {
		return "/!v8/api/freerounds.js";
	}
	
	public Map<String, String> getAuthParamMap() {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		if (getApiUser() != null)
			map.put("usr", getApiUser());
		if (getApiPassword() != null)
			map.put("passw", getApiPassword());
		return map;
	}
}
