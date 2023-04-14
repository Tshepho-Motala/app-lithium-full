package lithium.service.casino.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude="bonusRulesInstantReward")
@JsonIgnoreProperties({ "bonusRulesInstantReward", "hibernateLazyInitializer", "handler" })
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class BonusRulesInstantRewardFreespinGames implements Serializable {
  private static final long serialVersionUID = -6147434073072073985L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String gameId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable=false)
  @JsonBackReference("bonusRulesInstantRewardFreespinGames")
  private BonusRulesInstantRewardFreespin bonusRulesInstantRewardFreespin;
}
