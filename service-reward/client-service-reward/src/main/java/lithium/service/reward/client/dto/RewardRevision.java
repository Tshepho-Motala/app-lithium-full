package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lithium.service.client.objects.Granularity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RewardRevision implements Serializable {

  private static final long serialVersionUID = -4674034648629362157L;

  private long id;
  private String name;
  private String code;
  private String description;
  private boolean enabled;
  private Integer validFor;
  private Granularity validForGranularity;
  private String activationNotificationName;
  @Singular
  @JsonManagedReference("rewardRevision")
  private List<RewardRevisionType> revisionTypes;
}