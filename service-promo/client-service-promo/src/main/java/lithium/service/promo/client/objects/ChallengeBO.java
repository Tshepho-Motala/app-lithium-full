package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 3902548492590902209L;
  private Long id;

  @NotEmpty(message = "description is a required field")
  private String description;

  @Valid
  private RewardBO reward;
  private Integer sequenceNumber;

  @Valid
  @NotEmpty(message = "rules is a required field")
  private List<RuleBO> rules;

  @Builder.Default
  private Boolean requiresAllRules = Boolean.FALSE;
}
