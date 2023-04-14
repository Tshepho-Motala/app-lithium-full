package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties( {"hibernateLazyInitializer", "handler"} )
public class PlayerRewardHistory implements Serializable {

  private static final long serialVersionUID = 184688146670768807L;
  private Long id;

  private Date awardedDate;
  private Date redeemedDate;
  private Date expiryDate; // calculated from awarded date + validFor

  private RewardSource rewardSource;

  private User player;

  private RewardRevision rewardRevision;

  private PlayerRewardHistoryStatus status;
}
