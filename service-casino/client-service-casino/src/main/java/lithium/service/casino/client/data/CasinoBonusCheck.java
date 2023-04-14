package lithium.service.casino.client.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CasinoBonusCheck {
	private String domainName;
 	private String bonusCode;
 	private Long bonusId;
 	private String playerGuid;
 	private Long depositCents;
}