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
public class MachineInitResponse {
	private Long requestTimestamp;
	private Long responseTimestamp;
	private String guid;
	private Status status;
}
