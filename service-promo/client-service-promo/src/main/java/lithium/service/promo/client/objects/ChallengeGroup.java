package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeGroup implements Serializable {

  @Serial
  private static final long serialVersionUID = 8079749807352497290L;
  @NotNull
  private Long id;
  private PromotionRevision promotionRevision;
  private boolean sequenced;

  @Builder.Default
  private List<Challenge> challenges = new ArrayList<>();
}
