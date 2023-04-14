package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RewardBO implements Serializable {

  @Serial
  private static final long serialVersionUID = -5952857192592782572L;
  private Long id;

  @Min(value = 1, message = "Provided an invalid value for rewardId")
  private Long rewardId;
}
