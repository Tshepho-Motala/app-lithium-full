package lithium.service.limit.client.schemas.dwh;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DwhRestrictionRequest {
	private String apiAuthorizationId;
	private String userGuid;
	private String timestamp;
	private String hash;
	private Long restrictionId;
	private Integer subType;

	public String payload() {
		StringBuilder payload = new StringBuilder();
		payload.append(this.apiAuthorizationId + "|");
		payload.append(this.userGuid + "|");
		payload.append(this.timestamp + "|");
		return payload.toString();
	}
}
