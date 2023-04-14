package lithium.service.accounting.domain.v2.context;

import lithium.service.accounting.domain.v2.storage.entities.AccountCode;
import lithium.service.accounting.domain.v2.storage.entities.Currency;
import lithium.service.accounting.domain.v2.storage.entities.Domain;
import lithium.service.accounting.domain.v2.storage.entities.LabelValue;
import lithium.service.accounting.domain.v2.storage.entities.Period;
import lithium.service.accounting.domain.v2.storage.entities.TransactionType;
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
public class SummaryContext {
	private Domain domain;
	private Period period;
	private AccountCode accountCode;
	private Currency currency;
	private LabelValue labelValue;
	private TransactionType transactionType;
}
