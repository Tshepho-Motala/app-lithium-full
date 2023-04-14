package lithium.service.reward.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString(exclude = {"rewardRevision"})
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reward_revision_type", indexes = {
    @Index( name = "idx_revision_reward_type", columnList = "reward_type_id, reward_revision_id", unique = false ),
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RewardRevisionType {

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private RewardType rewardType;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn(nullable = false)
  private RewardRevision rewardRevision;

  /**
   * if this is false, then player first needs to acknowledge the reward type.
   * if this is true, the call is made to external provider to award the reward.
   */
  private boolean instant;

  /**
   * This will be used as a message for a notification that the player will have to accept
   */
  private String notificationMessage;

  public String rewardTypeUrl() {
    return rewardType.getUrl();
  }
}
