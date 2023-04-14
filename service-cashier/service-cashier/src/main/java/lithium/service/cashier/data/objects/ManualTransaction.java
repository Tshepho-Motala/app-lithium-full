package lithium.service.cashier.data.objects;

import java.util.List;

import lithium.service.cashier.client.objects.transaction.dto.DomainMethodProcessor;
import lithium.service.user.client.objects.User;
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
public class ManualTransaction {
	private String transactionType;
	private User user;
	private DomainMethodProcessor domainMethodProcessor;
	private Long bonusId;
	private String processorReference;
	private String amount;
	private List<ManualTransactionFieldValue> fields;
}