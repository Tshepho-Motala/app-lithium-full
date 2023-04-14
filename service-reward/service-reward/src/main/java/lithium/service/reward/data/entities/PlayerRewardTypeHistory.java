package lithium.service.reward.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.service.reward.client.dto.PlayerRewardComponentStatus;
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
@Table(name = "player_reward_type_history", indexes = {
    @Index( name = "idx_history_revision_type", columnList = "player_reward_history_id, reward_revision_type_id", unique = true )
})
@JsonIgnoreProperties( {"hibernateLazyInitializer", "handler"} )
@JsonIdentityInfo( generator = ObjectIdGenerators.PropertyGenerator.class, property = "id" )
public class PlayerRewardTypeHistory implements Serializable {

  private static final long serialVersionUID = -5006212497350987643L;
  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private Long id;

  @Version
  int version;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  private PlayerRewardHistory playerRewardHistory;

  @Default
  private Date createdDate = new Date();

  private Date updatedDate;

  @Column
  private Date awardedDate;

  @ManyToOne( fetch = FetchType.EAGER )
  @JoinColumn( nullable = false )
  private RewardRevisionType rewardRevisionType;

  @Enumerated( EnumType.STRING )
  private PlayerRewardComponentStatus status;

  private BigDecimal valueUsed; // e.g. how much of the freespins/instant rewards etc. has been used.
  private BigDecimal valueGiven; // e.g. the given amount of freespins
  private BigDecimal valueInCents; // e.g. the value of each freespin in cents, for cash rewards then this will be the value of the total cash reward

  private String referenceId; //if external party returns an id/reference nr for the reward.

  private Date redeemedDate;

  @PreUpdate()
  void beforeUpdate() {
    updatedDate = new Date();
  }
}
