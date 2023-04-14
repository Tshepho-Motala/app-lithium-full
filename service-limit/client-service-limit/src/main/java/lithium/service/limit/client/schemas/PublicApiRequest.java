package lithium.service.limit.client.schemas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PublicApiRequest {
	private String apiAuthorizationId;
	private String groupRef;
	private String msisdn;
	private String timestamp;
	private String hash;

	public String payload() {
		StringBuilder payload = new StringBuilder();
		payload.append(this.apiAuthorizationId + "|");
		payload.append(this.groupRef + "|");
		payload.append(this.msisdn + "|");
		payload.append(this.timestamp + "|");
		return payload.toString();
	}
}
