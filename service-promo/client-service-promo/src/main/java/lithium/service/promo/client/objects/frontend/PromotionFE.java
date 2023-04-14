package lithium.service.promo.client.objects.frontend;

import lithium.service.promo.client.objects.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionFE {
	private Long id;
	private int version;
	private String name;
	private String description;
	private String startDate;
	private String endDate;
	private Long rewardId;
	private Integer xpLevel;
	private boolean exclusive;
	private Long dependsOnPromotion;
	private Integer redeemableInTotal;
	private Integer redeemableInEvent;
	private String eventDuration;

	private List<ChallengeGroupFE> challengeGroups;

}
