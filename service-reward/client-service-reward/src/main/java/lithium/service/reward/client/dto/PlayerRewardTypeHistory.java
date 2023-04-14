package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties( {"hibernateLazyInitializer", "handler"} )
public class PlayerRewardTypeHistory implements Serializable {

  private static final long serialVersionUID = -4076805767618134482L;

  private Long id;

  private PlayerRewardHistory playerRewardHistory;

  private RewardRevisionType rewardRevisionType;

  private PlayerRewardComponentStatus status;

  private BigDecimal typeCounter; // e.g. how much of the freespins/instant rewards/ etc has been used.

  private String referenceId; //if external party returns an id/reference nr for the reward.
}
