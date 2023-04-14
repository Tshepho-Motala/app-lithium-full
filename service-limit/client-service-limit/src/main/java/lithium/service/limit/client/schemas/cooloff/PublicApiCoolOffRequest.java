package lithium.service.limit.client.schemas.cooloff;

import lithium.service.limit.client.schemas.PublicApiRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PublicApiCoolOffRequest extends PublicApiRequest {
	private int periodInDays;

	@Override
	public String payload() {
		String sPayload = super.payload();
		StringBuilder payload = new StringBuilder();
		payload.append(periodInDays + "|");
		return sPayload + payload.toString();
	}
}
