package lithium.service.casino.client.objects.response;

import lithium.math.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse extends Response {
	private Long balanceCents;

	public BigDecimal getBalance() {
		return CurrencyAmount.fromCents(balanceCents).toAmount();
	}
}