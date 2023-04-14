package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DomainCurrency {
	private Long id;
	private int version;
	private Domain domain;
	private Currency currency;
	private Boolean isDefault;
	private String name;
	private String symbol;
	private Integer divisor;
	private String description;
}
