package lithium.service.casino.cms.api.schema.lobby.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Jackpot {
	private String title;
	private String description;
	private BigDecimal balance;
	private BigDecimal targetBalance;
	private String balanceCurrency;
}
