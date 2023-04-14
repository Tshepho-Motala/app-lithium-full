package lithium.service.promo.client.objects.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeFE {
	private Long id;
	private Long rewardId;
	private String rewardCode;
	private List<RuleFE> rules;
}