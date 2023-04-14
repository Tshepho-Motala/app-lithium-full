package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerBalanceResponse {
	private DomainCurrency domainCurrency;
	private Map<String, Long> balances;
}
