package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PromotionBO implements Serializable {

  @Serial
  private static final long serialVersionUID = -9009040322588354288L;
  private Long id;
  private User editor;

  @Valid
  private PromotionRevisionBO current;

  @Valid
  @NotNull(message = "The edit field is required when creating a promotion")
  private PromotionRevisionBO edit;
  private boolean enabled;
}
