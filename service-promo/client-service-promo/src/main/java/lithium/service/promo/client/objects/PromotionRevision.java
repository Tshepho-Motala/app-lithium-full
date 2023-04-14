package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRevision implements Serializable {

	@Serial
	private static final long serialVersionUID = 4453686899700062728L;
	private Long id;
	private int version;

	@Valid
	private Domain domain;

	@NotNull
	private String name;

	private String description;
	private Date startDate;
	private Date endDate;
	private Reward reward;
	private Integer xpLevel;
	private boolean exclusive;

	private List<UserCategory> userCategories;
	private List<ChallengeGroup> challengeGroups;
	private Set<User> exclusivePlayers;

	private Promotion dependsOnPromotion;

	@NotNull(message = "recurrencePattern is required")
	@NotEmpty(message = "recurrencePattern is required")
	private String recurrencePattern;
	private Integer redeemableInTotal; //This is for the lifetime of the promotion
	private Integer redeemableInEvent; //This is for each occurrence of the promotion
	private Integer eventDuration;
	private Integer eventDurationGranularity; //TODO: populate granularity table on startup, and link to relevant row.

}
