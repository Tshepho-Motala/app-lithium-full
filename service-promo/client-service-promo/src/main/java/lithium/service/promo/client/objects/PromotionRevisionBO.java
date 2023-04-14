package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lithium.service.promo.client.validation.constraints.ValidDateFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRevisionBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 5478826612954418188L;
  private Long id;

  @Valid
  @NotNull(message = "domain is a required field")
  private DomainBO domain;

  @NotEmpty(message = "name is a required field")
  private String name;

  private String description;

  @NotEmpty(message = "startDate is a required field")
  @ValidDateFormat
  private String startDate;
  private String endDate;

  @Valid
  private RewardBO reward;
  private Integer xpLevel;
  private boolean exclusive;

  private List<UserCategoryBO> userCategories;

  @Valid
  private List<ChallengeGroupBO> challengeGroups;
  private Set<User> exclusivePlayers;

  private Long dependsOnPromotion;

  @NotEmpty( message = "recurrencePattern is required" )
  private String recurrencePattern;

  @NotNull(message = "redeemableInTotal is a required field")
  private Integer redeemableInTotal; //This is for the lifetime of the promotion

  @NotNull(message = "redeemableInEvent is a required field")
  private Integer redeemableInEvent; //This is for each occurrence of the promotion

  @NotNull(message = "eventDuration is a required field")
  private Integer eventDuration;

  private Integer eventDurationGranularity; //TODO: populate granularity table on startup, and link to relevant row.

  @Builder.Default
  private Boolean requiresAllChallengeGroups = Boolean.FALSE;

}
