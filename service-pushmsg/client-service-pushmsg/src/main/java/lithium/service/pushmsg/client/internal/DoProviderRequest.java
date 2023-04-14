package lithium.service.pushmsg.client.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoProviderRequest {
	private List<String> includePlayerIds;
	private String templateId;
	@Default
	private Map<String, String> placeholders = new HashMap<>();
	@Default
	private Map<String, String> properties = new HashMap<>();
	
	public String getProperty(String key) throws Exception {
		if (properties == null) throw new Exception("Provider properties missing");
		if (properties.get(key) == null) throw new Exception("Provider property " + key + " missing");
		return properties.get(key);
	}
}