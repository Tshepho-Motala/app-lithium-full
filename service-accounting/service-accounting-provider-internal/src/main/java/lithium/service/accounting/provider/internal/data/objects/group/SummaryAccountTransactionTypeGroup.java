package lithium.service.accounting.provider.internal.data.objects.group;

import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SummaryAccountTransactionTypeGroup {
	private Period period;
	private TransactionType transactionType;
	private AccountCode accountCode;
	private Currency currency;
	private Long debitCents;
	private Long creditCents;
	private Long tranCount;
}
