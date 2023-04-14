package lithium.service.casino.provider.slotapi.api.schema.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetSummaryFE {
	private Long id;
	private String betRoundGuid;
	private Date date;
	private Double stake;
	private Double won;
	private Double loss;
	private String transactionType;
	private Long amountCents;
	private String transactionTypeDisplay;
	private String provider;
	private String providerTranId;
	private String gameName;
}
