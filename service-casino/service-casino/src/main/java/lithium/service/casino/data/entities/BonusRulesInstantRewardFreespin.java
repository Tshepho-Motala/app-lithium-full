package lithium.service.casino.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lithium.service.casino.data.enums.Volatility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class BonusRulesInstantRewardFreespin implements Serializable {
  private static final long serialVersionUID = -8371613166378637063L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String provider;

  private Integer numberOfUnits;

  private Long instantRewardUnitValue;

  @Enumerated(EnumType.STRING)
  private Volatility volatility;

  @Transient
  @OneToMany(fetch = FetchType.LAZY)
  @JsonManagedReference("bonusRulesInstantRewardFreespinGames")
  private List<BonusRulesInstantRewardFreespinGames> bonusRulesInstantRewardFreespinGames;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable=false)
  @JsonIgnore
  private BonusRevision bonusRevision;

}

