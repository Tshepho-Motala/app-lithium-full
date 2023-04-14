package lithium.service.access.provider.iovation.data;

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
public class Mlvalue1 {
	/**
	 * A score ranging from -10000 to 10000. -10000 indicates iovation's prediction that the
	 * transaction will be fraudulent while 10000 indicates iovation's prediction that the transaction will be trustworthy.
	 */
	private Integer value;
}
