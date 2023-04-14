package lithium.service.casino.client.data;

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
public class BonusAllocate {
	private String bonusCode;
	private String playerGuid;
	private Double customFreeMoneyAmountDecimal;
}
