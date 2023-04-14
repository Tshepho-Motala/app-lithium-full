package lithium.service.access.client.objects;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResultContainer {
	private AuthorizationResult authorizationResult;
	private Map<String, String> ipAndUserAgentData;
	
	public boolean isSuccesful() {
		return (authorizationResult != null)? authorizationResult.isSuccessful(): true;
	}
}
