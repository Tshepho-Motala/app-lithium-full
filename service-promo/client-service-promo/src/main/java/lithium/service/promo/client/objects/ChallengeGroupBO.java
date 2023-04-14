package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeGroupBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 7244364198204560363L;
  private Long id;
  private boolean sequenced;

  @Builder.Default

  @NotEmpty(message = "challenges is a required field")
  @Valid
  private List<ChallengeBO> challenges = new ArrayList<>();

  @Builder.Default
  private Boolean requiresAllChallenges = Boolean.FALSE;
}
