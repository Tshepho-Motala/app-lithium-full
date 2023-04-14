package lithium.service.exchange.client.objects;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRequest {
	private String fromCurrencyCode;
	private String toCurrencyCode;
	private BigDecimal amount;
	private Integer deductPercentage;
}