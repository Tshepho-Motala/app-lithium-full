package lithium.service.accounting.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SummaryDomainTransactionType implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	int version;

	private Long tranCount;

	private Long debitCents;

	private Long creditCents;

	private TransactionType transactionType;

	private AccountCode accountCode;
	
	private Currency currency;
	
	private Period period;

	private int tag;

}
