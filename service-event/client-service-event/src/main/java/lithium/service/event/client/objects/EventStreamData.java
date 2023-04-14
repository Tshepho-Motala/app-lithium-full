package lithium.service.event.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class EventStreamData  implements Serializable {
	public static final String EVENT_TYPE_ZERO_BALANCE = "zeroBalanceEvent";
	
	private static final long serialVersionUID = 1L;

	private String eventType;

	private String ownerGuid;
	
	private String domainName;
	
	private String currencyCode; //USD
	
	/**
	 * If left null or blank, the event service implementation of the event type will create a key if applicable
	 */
	private String duplicateEventPreventionKey; //dedup (used to identify previous events that means the current one should be discarded)

}
