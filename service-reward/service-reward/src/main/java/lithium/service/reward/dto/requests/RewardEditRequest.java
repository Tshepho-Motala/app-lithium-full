package lithium.service.reward.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RewardEditRequest implements Serializable {
  @Serial
  private static final long serialVersionUID = -1925961287317345855L;

  @NotBlank
  @NotNull
  private String name;

  private String description;

  private Boolean enabled;

  @NotNull
  private Integer validFor;

  @NotNull
  private Integer validForGranularity;

  private String activationNotificationName;
}
