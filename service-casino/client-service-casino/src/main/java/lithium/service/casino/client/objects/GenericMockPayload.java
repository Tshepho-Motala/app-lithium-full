package lithium.service.casino.client.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
/**
 * The payload from the ui client when requesting a mock method execution
 * amountCentsPrimaryAction is used for single action executions
 * amountCentsSecondaryAction is used for the second action in cases like combined betAndWin
 * @author Chris
 *
 */
public class GenericMockPayload implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long mockSessionId;
	private int resendCount;
	private long delayBetweenResendMs;
	private long amountCentsPrimaryAction;
	private long amountCentsSecondaryAction;
}