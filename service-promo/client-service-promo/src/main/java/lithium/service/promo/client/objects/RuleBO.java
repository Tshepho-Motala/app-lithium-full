package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RuleBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 160384838100493611L;
  private Long id;

  @Valid
  private PromoProviderBO promoProvider;

  private String category;

  @Valid
  @NotNull(message = "activity is a required field")
  private ActivityBO activity;

  @NotEmpty(message = "operation is a required field")
  private String operation;

  @NotNull(message = "value is a required field")
  private Long value;

  @Singular
  @Valid
  private List<ActivityExtraFieldRuleValueBO> activityExtraFieldRuleValues;
}
