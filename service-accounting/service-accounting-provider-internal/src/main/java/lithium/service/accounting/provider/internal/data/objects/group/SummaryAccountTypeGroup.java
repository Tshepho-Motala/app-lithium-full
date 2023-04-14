package lithium.service.accounting.provider.internal.data.objects.group;

import lithium.service.accounting.provider.internal.data.entities.AccountType;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.entities.User;
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
public class SummaryAccountTypeGroup {
	private Period period;
	private AccountType accountType;
	private Currency currency;
	private Long debitCents;
	private Long creditCents;
	private Long tranCount;
	private User owner;
}
