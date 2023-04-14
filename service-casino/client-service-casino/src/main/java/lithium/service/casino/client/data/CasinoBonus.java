package lithium.service.casino.client.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CasinoBonus {
	String bonusCode;
	String playerGuid;
	Long userEventId;
	Long bonusId;
	Boolean override = Boolean.FALSE;
	
	// required for auto bonus allocation
	String token;
	Long amountCents;
}