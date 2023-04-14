package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString( exclude = {"setupFields"} )
public class RewardType implements Serializable {

  private static final long serialVersionUID = -1964570798847477009L;
  private long id;
  private String url;
  private String name; // IRewardTypeName // freespins / instant rewards / freebets
  private String code; // Shortname for the provider RX = roxor / IF = iforium / BP = blueprint

  @Default
  @JsonManagedReference("rewardType")
  private List<RewardTypeField> setupFields = new ArrayList<>();
  @Default
  private boolean displayGames = true; // If true (default), this will add a display of available games for this provider to be added to the setup of this reward.
}
