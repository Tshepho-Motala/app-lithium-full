package lithium.service.reward.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "player_reward_type_history_value", indexes = {
        @Index( name = "idx_player_reward_history_type_value_field", columnList = "player_reward_type_history_id, reward_type_field_id", unique = false ),
        @Index(name = "idx_player_reward_type_history_value_history_id", columnList = "player_reward_type_history_id")
})
public class PlayerRewardTypeHistoryValue implements Serializable {
  private static final long serialVersionUID = -7496739846010079467L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String value;

  @ManyToOne
  @JoinColumn(name = "reward_type_field_id", nullable = false)
  private RewardTypeField rewardTypeField;

  @ManyToOne
  @JoinColumn(name = "player_reward_type_history_id", nullable = false)
  private PlayerRewardTypeHistory playerRewardTypeHistory;
}
