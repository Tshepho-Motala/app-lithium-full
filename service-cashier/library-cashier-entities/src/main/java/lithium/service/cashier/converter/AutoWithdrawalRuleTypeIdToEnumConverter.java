package lithium.service.cashier.converter;

import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class AutoWithdrawalRuleTypeIdToEnumConverter implements Converter<String, AutoWithdrawalRuleType> {

  @Override
  public AutoWithdrawalRuleType convert(String stringId) {
    return Optional.ofNullable(stringId)
        .map(Integer::valueOf)
        .map(id -> {
          for (AutoWithdrawalRuleType type : AutoWithdrawalRuleType.values()) {
            if (type.id() == id) {
              return type;
            }
          }
          return null;
        })
        .orElse(null);
  }
}
