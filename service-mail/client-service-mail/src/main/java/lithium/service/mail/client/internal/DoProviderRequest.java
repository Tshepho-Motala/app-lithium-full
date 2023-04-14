package lithium.service.mail.client.internal;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoProviderRequest {
	private Long mailId;
	private String from;
	private String to;
	private String subject;
	private String body;
	private String attachmentName;
	private byte[] attachmentData;
	private Map<String, String> properties = new HashMap<>();
	
	public String getProperty(String key) throws Exception {
		if (properties == null) throw new Exception("Provider properties missing");
		if (properties.get(key) == null) throw new Exception("Provider property " + key + " missing");
		return properties.get(key);
	}
}