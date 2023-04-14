package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ActivityExtraFieldRuleValueBO implements Serializable {

  @Serial
  private static final long serialVersionUID = 3308768892557317693L;
  private long id;

  @Builder.Default
  private List<String> value = new ArrayList<>();
  private ActivityExtraFieldBO activityExtraField;
}
