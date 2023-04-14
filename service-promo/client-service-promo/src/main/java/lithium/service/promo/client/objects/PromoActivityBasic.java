package lithium.service.promo.client.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import lithium.service.promo.client.dto.IActivity;
import lithium.service.promo.client.dto.ICategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.SneakyThrows;

@Data
@Builder
@AllArgsConstructor
public class PromoActivityBasic {

  private String ownerGuid;
  private String domainName;
  private ICategory category; // casino, xp, user
  private IActivity activity; // casino: [spin, win, bonusround], xp: [level, points], user: [login], raf: [referral, conversion]
  private Long value;
  private String timezone;
  @Default
  private Map<String, String> labelValues = new HashMap<>();

  private String provider;

  @Override
  @SneakyThrows
  public String toString() {
    return new StringJoiner(", ", PromoActivityBasic.class.getSimpleName() + "[", "]").add("ownerGuid='" + ownerGuid + "'")
        .add("domainName='" + domainName + "'")
        .add("category=" + category.getCategory())
        .add("activity=" + activity.getActivity())
        .add("value=" + value)
        .add("timezone='" + timezone + "'")
        .add("labelValues=" + labelValues)
        .add("provider="+ provider)
        .toString();
  }
}