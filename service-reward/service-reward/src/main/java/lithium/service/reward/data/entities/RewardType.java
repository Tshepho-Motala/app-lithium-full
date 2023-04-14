package lithium.service.reward.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Data
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"setupFields"})
@Table(name = "reward_type", indexes = {
    @Index( name = "idx_reward_type_url_name", columnList = "url, name", unique = true ),
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RewardType {

  @Id
  @GeneratedValue( strategy = GenerationType.AUTO )
  private long id;

  private String url;
  private String name; // freespins / instant rewards / freebets
  private String code; // Shortname for the provider RX = roxor / IF = iforium / BP = blueprint
  @Singular
  @OneToMany( fetch = FetchType.EAGER, mappedBy = "rewardType", cascade = CascadeType.ALL )
  private List<RewardTypeField> setupFields;

  @Default
  private boolean displayGames = true; // If true (default), this will add a display of available games for this provider to be added to the setup of this reward.
}