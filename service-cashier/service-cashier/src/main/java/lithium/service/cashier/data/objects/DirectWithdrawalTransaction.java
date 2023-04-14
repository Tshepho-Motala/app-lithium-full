package lithium.service.cashier.data.objects;

import lithium.service.cashier.client.objects.transaction.dto.DomainMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DirectWithdrawalTransaction {
	private DomainMethod domainMethod;
	private String userGuid;
	private String processorAccountId;
	private String amount;
	private String comment;
	private Boolean balanceLimitEscrow;
}
