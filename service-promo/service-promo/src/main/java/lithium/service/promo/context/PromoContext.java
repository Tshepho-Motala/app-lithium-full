package lithium.service.promo.context;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import lithium.service.promo.client.objects.PromoActivityBasic;
import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.User;
import lithium.service.promo.data.entities.UserPromotion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromoContext {

  private PromoActivityBasic promoActivityBasic;
  private Domain domain;
  private User user;
  private ZoneId userZoneId;
  private ZonedDateTime userZonedDateTime;

  @Builder.Default
  private Set<Promotion> promotions = new HashSet<>();

  @Builder.Default
  private List<UserPromotion> userPromotions = new ArrayList<>();

  private String statName;

  public String domainName() {
    return domain.getName();
  }

  public String playerGuid() {
    return user.guid();
  }

  public String prepStatName(String userPromotionChallengeRuleId)
  throws IOException
  {
    String name = "promo." + userPromotionChallengeRuleId + "." + promoActivityBasic.getCategory().getCategory() + "." + promoActivityBasic.getActivity().getActivity();
    return name.toLowerCase();
  }

  public void appendToStatName(String append) {
    setStatName(getStatName() + append);
  }

  public void addUserPromotion(UserPromotion userPromotion) {
    if (userPromotions == null) {
      userPromotions = new ArrayList<>();
    }
    userPromotions.add(userPromotion);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", PromoContext.class.getSimpleName() + "[", "]")
        .add("promoActivityBasic=" + promoActivityBasic)
        .add("domain=" + domain.getName())
        .add("user=" + user.getGuid())
        .add("userZoneId=" + userZoneId.getId())
        .add("promotions=" + ((promotions != null) ? promotions.size() : "0"))
        .add("userPromotions=" + ((userPromotions != null) ? userPromotions.size() : "0"))
        .add("statName='" + statName + "'")
        .toString();
  }
}
