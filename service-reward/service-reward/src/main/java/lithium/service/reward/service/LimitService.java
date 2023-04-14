package lithium.service.reward.service;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.exception.Status511UpstreamServiceUnavailableException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status416PlayerPromotionsBlockedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.reward.client.dto.GiveRewardContext;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LimitService {

  @Autowired
  private LimitInternalSystemService limitInternalSystemService;
  @Autowired
  private UserApiInternalClientService userApiInternalClientService;

  /**
   * This was copied from svc-casino, and will do all the access checks to determine if player is eligible for the bonus.
   *
   * @param
   */
  public void performAllAccessChecks(GiveRewardContext context, String locale)
  throws Status401UnAuthorisedException, Status405UserDisabledException,
      //      Status473DomainBettingDisabledException,
      //      Status474DomainProviderDisabledException, Status484WeeklyLossLimitReachedException, Status485WeeklyWinLimitReachedException,
      Status490SoftSelfExclusionException, Status491PermanentSelfExclusionException,
      //      Status492DailyLossLimitReachedException,
      //      Status493MonthlyLossLimitReachedException, Status494DailyWinLimitReachedException, Status495MonthlyWinLimitReachedException,
      Status496PlayerCoolingOffException, Status511UpstreamServiceUnavailableException,
          Status416PlayerPromotionsBlockedException
  //      Status550ServiceDomainClientException,
  //      Status478TimeSlotLimitException,
  //      Status438PlayTimeLimitReachedException
  {
    try {

      limitInternalSystemService.checkPlayerRestrictions(context.playerGuid(), context.getLocale());
      limitInternalSystemService.checkPromotionsAllowed(context.playerGuid());
      //TODO: Not needed? Only relevant if betting
      //      limitInternalSystemService.checkLimits(context.domainName(), context.playerGuid(), currency, request.getTotalWageredBetAmountCents(), locale);
      //TODO: Discuss with PO
      //      limitInternalSystemService.checkPlayerBetPlacementAllowed(request.getUserGuid());
    } catch (
      //        Status438PlayTimeLimitReachedException | Status478TimeSlotLimitException |
      //             Status484WeeklyLossLimitReachedException |
      //             Status485WeeklyWinLimitReachedException |
        Status490SoftSelfExclusionException | Status491PermanentSelfExclusionException | Status416PlayerPromotionsBlockedException |
            //             Status492DailyLossLimitReachedException |
            //             Status493MonthlyLossLimitReachedException |
            //             Status494DailyWinLimitReachedException |
            //             Status495MonthlyWinLimitReachedException |
        Status496PlayerCoolingOffException errorCodeException) {
      throw errorCodeException;
    } catch (Exception ex) {
      log.error("Problem performing player restriction and limit checks for context: " + context, ex);
      throw new Status511UpstreamServiceUnavailableException(ex.getMessage() + " for limit service");
    }

    //TODO: Discuss with PO
    //    cachingDomainClientService.checkBettingEnabled(request.getDomainName(), locale);
    //TODO: Implement?
    //    providerClientService.checkProviderEnabled(request.getDomainName(), request.getProviderGuid().split("/")[1], locale);
    try {
      userApiInternalClientService.performUserChecks(context.playerGuid(), locale, null, true, false, false);
    } catch (Status500UserInternalSystemClientException e) {
      throw new Status511UpstreamServiceUnavailableException(e.getMessage() + " for user service");
    }
  }
}
