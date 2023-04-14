package lithium.service.accounting.provider.internal.data.objects.group;

import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Period;
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
public class SummaryAccountGroup {
	private Period period;
	private AccountCode accountCode;
	private Currency currency;
	private Long openingBalanceCents;
	private Long debitCents;
	private Long creditCents;
	private Long closingBalanceCents;
	private Long tranCount;
}
