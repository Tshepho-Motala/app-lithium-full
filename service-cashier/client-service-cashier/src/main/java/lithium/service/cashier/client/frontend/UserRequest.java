package lithium.service.cashier.client.frontend;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
	private String ipAddr;
	private Map<String, String> headers;
	
	public String userAgent() {
		return headers.get("user-agent");
	}
}
