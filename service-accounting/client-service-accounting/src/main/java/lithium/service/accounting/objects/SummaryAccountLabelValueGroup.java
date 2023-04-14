package lithium.service.accounting.objects;

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
public class SummaryAccountLabelValueGroup {
	private Period period;
	private TransactionType transactionType;
	private LabelValue labelValue;
	private AccountCode accountCode;
	private Currency currency;
	private Long debitCents;
	private Long creditCents;
	private Long tranCount;
}