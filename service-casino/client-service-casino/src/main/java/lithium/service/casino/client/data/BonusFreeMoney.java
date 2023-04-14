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
public class BonusFreeMoney {
	private Long id;
	private int version;
	private String currency;
	private Long amount;
	private Integer wagerRequirement;
	private Boolean immediateRelease;
}
