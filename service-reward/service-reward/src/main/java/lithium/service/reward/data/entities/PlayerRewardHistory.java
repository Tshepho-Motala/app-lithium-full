package lithium.service.reward.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.client.dto.RewardSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "player_reward_history", indexes = {
    @Index( name = "idx_player_reward_revision", columnList = "player_id, reward_revision_id", unique = false ),
    @Index( name = "idx_player_reward_revision_status", columnList = "player_id, reward_revision_id, status", unique = false )} )
@JsonIgnoreProperties( {"hibernateLazyInitializer", "handler"} )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class PlayerRewardHistory implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;

  @Version
  int version;

  @Column( nullable = false )
  @Default
  private Date createdDate = new Date();
  @Column
  private Date awardedDate;
  @Column
  private Date redeemedDate;
  @Column
  private Date expiryDate; // calculated from awarded date + validFor

  @Enumerated(EnumType.STRING)
  private RewardSource rewardSource;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  private User player;

  @JoinColumn
  @ManyToOne( fetch = FetchType.EAGER )
  private RewardRevision rewardRevision;

  @Enumerated( EnumType.STRING )
  private PlayerRewardHistoryStatus status;

  @Override
  public String toString() {
    return new StringJoiner(", ", PlayerRewardHistory.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("createdDate=" + createdDate)
        .add("awardedDate=" + awardedDate)
        .add("redeemedDate=" + redeemedDate)
        .add("expiryDate=" + expiryDate)
        .add("rewardSource=" + rewardSource.name())
        .add("player=" + player.guid())
        .add("rewardRevision=" + rewardRevision.toString())
        .add("status=" + status.name())
        .toString();
  }
}
