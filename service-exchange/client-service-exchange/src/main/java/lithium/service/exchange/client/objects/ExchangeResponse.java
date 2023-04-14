package lithium.service.exchange.client.objects;

import java.math.BigDecimal;

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
public class ExchangeResponse {
	private String fromCurrencyCode;
	private String toCurrencyCode;
	private BigDecimal originalAmount;
	private BigDecimal convertedAmount;
	private BigDecimal convertedAmountAfterDeduction;
}