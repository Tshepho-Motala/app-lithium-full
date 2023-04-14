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
public class SummaryDomainLabelValue {
	private long id;
	int version;
	private Long tranCount;
	private Long debitCents;
	private Long creditCents;
	private LabelValue labelValue;
	private TransactionType transactionType;
	private AccountCode accountCode;
	private Currency currency;
	private Period period;
	private int tag;
}
