package lithium.service.cashier.client.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoResponseSection {
	private String sectionHeader;
	private String sectionText;
}
