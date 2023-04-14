package lithium.service.promo.client.objects.frontend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserPromotionFE {
	private Long id;
	private String playerGuid;
	private String promoEventStarted;
	private boolean completed;
	private String promoEventCompleted;
	private double percentage;
	private String promoEventStart;
	private String promoEventEnd;
	private PromotionFE promotion;
	private List<UserChallengeGroupFE> userChallengeGroups;
}