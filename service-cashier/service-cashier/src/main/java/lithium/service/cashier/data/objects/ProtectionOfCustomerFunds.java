package lithium.service.cashier.data.objects;

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
public class ProtectionOfCustomerFunds {
	@Builder.Default
	private boolean enabled = true;
	private String currentDomainVersion;
	private String acceptedUserVersion;
}
