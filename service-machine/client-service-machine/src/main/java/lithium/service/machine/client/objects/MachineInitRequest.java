package lithium.service.machine.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineInitRequest {
	private String domain;
	private String machineGuid;
	private Long requestTimestamp;
	private String gatewayQueue;
	private String socketSessionId;
}
