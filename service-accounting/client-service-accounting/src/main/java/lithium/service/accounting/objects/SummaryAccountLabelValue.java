package lithium.service.accounting.objects;

import lombok.Data;

@Data
public class SummaryAccountLabelValue {
	private long id;
	private int version;
	private Long tranCount;
	private Long debitCents;
	private Long creditCents;
	private LabelValue labelValue;
	private TransactionType transactionType;
	private Account account;
	private Period period;
	private boolean damaged;
}