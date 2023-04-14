package lithium.service.accounting.objects;

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
public class TransactionEntryBODetails {
	private Long bonusRevisionId;
	private String bonusName;
	private String bonusCode;
	private String externalTranId;
	private String additionalTranId;
	private String gameGuid;
	private String gameName;
	private String roundId;
	private Long externalTimestamp;
	private Long playerBonusHistoryId;
	private Long playerRewardTypeHistoryId;
	private String processingMethod;
	private String providerGuid;
	private String accountingClientTranId;
	private String accountingClientExternalId;
	private String externalTransactionDetailUrl;
}
