package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SummaryAccountTransactionType {
	private Long id;
	private Long tranCount;
	private Long debitCents;
	private Long creditCents;
	private TransactionType transactionType;
	
	private Account account;
	private Period period;
	private boolean damaged;

	public static Long getBalance(SummaryAccountTransactionType type) {
		return Optional.ofNullable(type.getDebitCents()).orElse(0L) - Optional.ofNullable(type.getCreditCents()).orElse(0L);
	}
}
