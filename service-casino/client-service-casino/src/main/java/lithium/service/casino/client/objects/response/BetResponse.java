package lithium.service.casino.client.objects.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BetResponse extends Response {
	private String extSystemTransactionId;
	private Long balanceCents;
	private Long bonusBet;
	private Long bonusWin;
}