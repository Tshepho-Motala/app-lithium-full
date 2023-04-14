package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import lithium.service.promo.client.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Rule implements Serializable {

  @Serial
  private static final long serialVersionUID = 5123089183047649984L;
  private Long id;
  private int version;
  private Challenge challenge;

  private String category;
  private PromoProvider promoProvider;
  private Activity activity;
  private Operation operation;
  private Long value;

  @Singular
  private List<ActivityExtraFieldRuleValue> activityExtraFieldRuleValues;
}
