package lithium.service.accounting.provider.internal.events;

import java.util.UUID;

import lithium.service.accounting.provider.internal.data.entities.TransactionEntry;
import lithium.service.accounting.provider.internal.data.entities.User;
import lombok.Data;

@Data
public class BalanceAdjustEvent {

	public BalanceAdjustEvent() {
		eventId = UUID.randomUUID();
	}
	
	private UUID eventId;
	private User author;
	private TransactionEntry tranEntry;
	private TransactionEntry tranContraEntry;
	
}
