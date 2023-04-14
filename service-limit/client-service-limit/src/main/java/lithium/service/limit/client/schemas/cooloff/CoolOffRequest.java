package lithium.service.limit.client.schemas.cooloff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoolOffRequest {
	private String playerGuid;
	private int periodInDays;
}
