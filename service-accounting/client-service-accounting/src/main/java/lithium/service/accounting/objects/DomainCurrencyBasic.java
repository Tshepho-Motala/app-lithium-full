package lithium.service.accounting.objects;

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
public class DomainCurrencyBasic {
	private String code;
	private String name;
	private String description;
	private String symbol;
	private Integer divisor;
}
