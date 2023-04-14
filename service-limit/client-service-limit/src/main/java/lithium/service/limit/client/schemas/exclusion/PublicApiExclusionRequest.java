package lithium.service.limit.client.schemas.exclusion;

import lithium.service.limit.client.schemas.PublicApiRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PublicApiExclusionRequest extends PublicApiRequest {
	private Integer periodInMonths;

	@Override
	public String payload() {
		String sPayload = super.payload();
		StringBuilder payload = new StringBuilder();
		if (periodInMonths != null) {
			payload.append(periodInMonths + "|");
		} else {
			payload.append("permanent|");
		}
		return sPayload + payload.toString();
	}
}
