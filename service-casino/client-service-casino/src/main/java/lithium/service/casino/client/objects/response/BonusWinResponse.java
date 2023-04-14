package lithium.service.casino.client.objects.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BonusWinResponse extends Response {
	private Long balanceCents;
}