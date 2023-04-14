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
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table( name = "reward_revision_type_game", indexes = {
//    @Index( name = "idx_reward_type_revision_guid", columnList = "reward_type_id, reward_revision_id, guid", unique = true ),
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RewardRevisionTypeGame {

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;

//  @ManyToOne(fetch = FetchType.EAGER)
//  @JoinColumn(nullable = false)
//  private RewardType rewardType;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn(nullable = false)
  private RewardRevisionType rewardRevisionType;

  private String guid; // e.g. service-casino-provider-roxor_play-secrets-of-the-phoenix
  private String gameId; //play-secrets-of-the-phoenix
  private String gameName; //Secrets of the phoenix

  private boolean deleted = false;

  @Version
  private int version;
}
