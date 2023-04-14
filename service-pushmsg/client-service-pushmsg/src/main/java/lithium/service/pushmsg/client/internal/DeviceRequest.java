package lithium.service.pushmsg.client.internal;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRequest {
	private String uuid;
	
	@Builder.Default
	private Map<String, String> properties = new HashMap<>();
	
	public String getProperty(String key) throws Exception {
		if (properties == null) throw new Exception("Provider properties missing");
		if (properties.get(key) == null) throw new Exception("Provider property " + key + " missing");
		return properties.get(key);
	}
}