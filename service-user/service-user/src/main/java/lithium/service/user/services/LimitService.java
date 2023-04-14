package lithium.service.user.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.LimitInternalSystemClient;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.LimitType;
import lithium.service.limit.client.PlayerLimitsClient;
import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status476DomainBalanceLimitDisabledException;
import lithium.service.limit.client.exceptions.Status479DomainAgeLimitException;
import lithium.service.limit.client.exceptions.Status480PendingDepositLimitException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.limit.client.stream.PromotionRestrictionTriggerStream;
import lithium.service.user.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Service
public class LimitService {

  @Autowired
  private LithiumServiceClientFactory services;
  @Autowired
  private ModelMapper modelMapper;
  @Autowired
  private LimitInternalSystemService limitInternalSystemService;
  @Autowired
  private PromotionRestrictionTriggerStream promotionRestrictionTriggerStream;

	public Response<PlayerLimit> findPlayerLimitMonthWin(String domainName, String playerGuid, Principal principal) {
		return findPlayerLimit(domainName, playerGuid, Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_WIN_LIMIT.type(), principal);
	}
	public Response<PlayerLimit> findPlayerLimitMonthLoss(String domainName, String playerGuid, Principal principal) {
		return findPlayerLimit(domainName, playerGuid, Granularity.GRANULARITY_MONTH.granularity(), LimitType.TYPE_LOSS_LIMIT.type(), principal);
	}
  public Response<PlayerLimit> findPlayerLimitWeekWin(String domainName, String playerGuid, Principal principal) {
    return findPlayerLimit(domainName, playerGuid, Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_WIN_LIMIT.type(), principal);
  }
  public Response<PlayerLimit> findPlayerLimitWeekLoss(String domainName, String playerGuid, Principal principal) {
    return findPlayerLimit(domainName, playerGuid, Granularity.GRANULARITY_WEEK.granularity(), LimitType.TYPE_LOSS_LIMIT.type(), principal);
  }

  public Response<PlayerLimit> findPlayerLimitDayWin(String domainName, String playerGuid, Principal principal) {
		return findPlayerLimit(domainName, playerGuid, Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_WIN_LIMIT.type(), principal);
	}
	public Response<PlayerLimit> findPlayerLimitDayLoss(String domainName, String playerGuid, Principal principal) {
		return findPlayerLimit(domainName, playerGuid, Granularity.GRANULARITY_DAY.granularity(), LimitType.TYPE_LOSS_LIMIT.type(), principal);
	}

	public List<PlayerLimit> findPlayerLimit(String domainName, String playerGuid, Principal principal) {
		List<PlayerLimit> playerLimits = new ArrayList<>();
		Response<PlayerLimit> playerLimitMonthWin = findPlayerLimitMonthWin(domainName, playerGuid, principal);
		Response<PlayerLimit> playerLimitMonthLoss = findPlayerLimitMonthLoss(domainName, playerGuid, principal);
    Response<PlayerLimit> playerLimitWeekWin = findPlayerLimitWeekWin(domainName, playerGuid, principal);
    Response<PlayerLimit> playerLimitWeekLoss = findPlayerLimitWeekLoss(domainName, playerGuid, principal);
    Response<PlayerLimit> playerLimitDayWin = findPlayerLimitDayWin(domainName, playerGuid, principal);
		Response<PlayerLimit> playerLimitDayLoss = findPlayerLimitDayLoss(domainName, playerGuid, principal);
		if (playerLimitMonthWin.isSuccessful()) playerLimits.add(playerLimitMonthWin.getData());
		if (playerLimitMonthLoss.isSuccessful()) playerLimits.add(playerLimitMonthLoss.getData());
    if (playerLimitWeekWin.isSuccessful()) playerLimits.add(playerLimitWeekWin.getData());
    if (playerLimitWeekLoss.isSuccessful()) playerLimits.add(playerLimitWeekLoss.getData());
    if (playerLimitDayWin.isSuccessful()) playerLimits.add(playerLimitDayWin.getData());
		if (playerLimitDayLoss.isSuccessful()) playerLimits.add(playerLimitDayLoss.getData());
		return playerLimits;
	}

  public Response<PlayerLimit> findPlayerLimit(String domainName, String playerGuid, Integer granularity, Integer type, Principal principal) {
    Response<PlayerLimit> findPlayerLimit = getLimitInternalSystemClient().findPlayerLimit(playerGuid, domainName, granularity, type, principal);
    return findPlayerLimit;
  }

  PlayerLimit saveDepositLimit(
      @RequestParam(name = "playerGuid") String playerGuid,
      @RequestParam(name = "granularity") Integer granularity,
      @RequestParam(name = "amount") String amount
  ) throws
      Status100InvalidInputDataException,
      Status480PendingDepositLimitException,
      Status481DomainDepositLimitDisabledException,
      Status500LimitInternalSystemClientException,
      Status550ServiceDomainClientException {
    return getLimitInternalSystemClient().saveDepositLimit(playerGuid, granularity, amount);
  }

  PlayerLimit saveBalanceLimit(String playerGuid, String amount) throws
      Status100InvalidInputDataException,
      Status480PendingDepositLimitException,
      Status481DomainDepositLimitDisabledException,
      Status500LimitInternalSystemClientException,
      Status550ServiceDomainClientException,
          Status476DomainBalanceLimitDisabledException {
    return getLimitInternalSystemClient().saveBalanceLimit(playerGuid, amount);
  }

  void setPlayerAgeLimit(User user) throws Status479DomainAgeLimitException {

    try {
      //We need to forward this on, even if there are no ages provided in order to apply global limits

      getPlayerLimitSystemClient().setPlayerLimit(modelMapper.map(user, lithium.service.user.client.objects.User.class));

    } catch (Exception e) {
      throw new Status479DomainAgeLimitException(e.getMessage());
    }
  }

  public boolean triggerAutoRestrictions(String userGuid) {
    boolean autoRestrictionCheckSuccessful = false;
    try {
      limitInternalSystemService.autoRestrictionTrigger(userGuid);
      autoRestrictionCheckSuccessful = true;
    }
    catch (Exception e) {
      log.error(String.format("An error occurred while triggering auto restrictions for user %s", userGuid), e);
    }

    return autoRestrictionCheckSuccessful;
  }

//	public Response<String> allowedToTransactCheck(String domainName, String playerGuid, Locale locale) throws Exception {
//		Domain domain = domainService.retrieveDomainFromDomainService(domainName);
//		Response<String> allowedToTransactResult = getLimitService().allowedToTransact(domainName, playerGuid, domain.getCurrency(), locale.toString());
//		return allowedToTransactResult;
//	}

  private LimitInternalSystemClient getLimitInternalSystemClient() {
    LimitInternalSystemClient cl = null;
    try {
      cl = services.target(LimitInternalSystemClient.class, "service-limit", true);
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Problem getting limit internal system service", e);
    }
    return cl;
  }

  private PlayerLimitsClient getPlayerLimitSystemClient() {
    PlayerLimitsClient cl = null;
    try {
      cl = services.target(PlayerLimitsClient.class, "service-limit", true);
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Problem getting player limit system service", e);
    }
    return cl;
  }
}
