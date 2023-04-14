package lithium.service.user.provider.threshold.services;


import lithium.service.accounting.objects.Currency;
import lithium.service.accounting.objects.Period;
import lithium.service.user.provider.threshold.data.entities.User;

public interface AccountingService {
  public static final String PLAYER_BALANCE_TYPE_CODE_NAME = "PLAYER_BALANCE";
  Long findNetLossToPlayer(User user, Period period, Currency currency) throws Exception;
}
