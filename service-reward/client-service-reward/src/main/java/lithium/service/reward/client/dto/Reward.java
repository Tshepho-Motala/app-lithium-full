package lithium.service.reward.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString( exclude = {"edit"} )
@EqualsAndHashCode( exclude = "edit" )
public class Reward implements Serializable {

  private static final long serialVersionUID = 8622339402837301807L;

  private long id;

  private User editUser;

  private Domain domain;

  @JsonManagedReference("reward")
  private RewardRevision current;

  @JsonManagedReference("reward")
  private RewardRevision edit;
}