package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SummaryAccountLabelValueType {
	private Period period;
	private Account account;
	private TransactionType transactionType;
	private LabelValue labelValue;
	private Currency currency;
	private Long debitCents;
	private Long creditCents;
	private Long tranCount;
}
