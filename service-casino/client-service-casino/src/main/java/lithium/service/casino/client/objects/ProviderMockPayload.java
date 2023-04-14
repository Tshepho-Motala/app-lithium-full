package lithium.service.casino.client.objects;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
/**
 * The payload from the generic mock service to the provider implementation.
 * The primary action amount will be used in single action exections.
 * The secondary action amount will be used when a combination execution is done eg. betAndWin
 * @author Chris
 *
 */
public class ProviderMockPayload implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userGuid;
	private String transactionId;
	private long amountCentsPrimaryAction;
	private String authToken;
	private String providerGameId;
	private String currency;
	private String roundId;
	private boolean roundEnd;
	private long amountCentsSecondaryAction;
}