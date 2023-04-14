package lithium.service.accounting.objects.frontend;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"date", "amount", "amountCent", "transactionType", "tranEntryAccountCode"})
public class FrontendTransaction implements Serializable {
	private Long id;
	private Date date;
	private Long amountCents;
	private BigDecimal amount;
	private String transactionType;
	private String transactionTypeDisplay;
//	@JsonProperty("account_type")
//	private String tranEntryAccountType;
	@JsonProperty("accountCode")
	private String tranEntryAccountCode;
	private Long postEntryAccountBalanceCents;
	private BigDecimal postEntryAccountBalance;
	@JsonProperty("provider")
	private String providerGuid;
	private Long bonusRevisionId;
	@JsonProperty("providerTranId")
	private String externalTranId;
	@JsonProperty("game")
	private String gameGuid;
	private String processingMethod;
	private String processorReference;
	private String processorDescription;
	private String accountingClientExternalId;
}
