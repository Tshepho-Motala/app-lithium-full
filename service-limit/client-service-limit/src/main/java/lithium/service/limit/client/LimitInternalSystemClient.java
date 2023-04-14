package lithium.service.limit.client;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status100InvalidInputDataException;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.exceptions.Status476DomainBalanceLimitDisabledException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status480PendingDepositLimitException;
import lithium.service.limit.client.exceptions.Status481DomainDepositLimitDisabledException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.LossLimitsVisibility;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.limit.client.objects.PlayerLimitSummaryFE;
import lithium.service.limit.client.objects.PlayerLimitV2Dto;
import lithium.service.limit.client.objects.Restrictions;
import lithium.service.limit.client.objects.UserRestrictionSet;
import lithium.service.limit.client.objects.UserRestrictionsRequest;
import lithium.service.limit.client.objects.VerificationStatusDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@FeignClient(name="service-limit")
public interface LimitInternalSystemClient {
	@RequestMapping(method = RequestMethod.GET, path = "/system/player-limit/{domainName}/check-limits")
	void checkLimits(@RequestParam("domainName") String domainName,
					 @RequestParam("playerGuid") String playerGuid,
					 @RequestParam("currency") String currency,
					 @RequestParam(name = "betAmountCents", required = false) Long betAmountCents,
					 @RequestParam("locale") String locale)
			throws
			Status484WeeklyLossLimitReachedException, Status485WeeklyWinLimitReachedException,
			Status493MonthlyLossLimitReachedException, Status492DailyLossLimitReachedException,
			Status495MonthlyWinLimitReachedException, Status494DailyWinLimitReachedException,
			Status478TimeSlotLimitException;

	@RequestMapping(method = RequestMethod.GET, path = "/system/player-limit/{domainName}/find-player-limit")
	Response<PlayerLimit> findPlayerLimit(@RequestParam("playerGuid") String playerGuid,
										  @PathVariable("domainName") String domainName,
										  @RequestParam("granularity") Integer granularity,
										  @RequestParam("type") Integer type, Principal principal);


	@RequestMapping(method = RequestMethod.POST, path = "/system/player-limit/{domainName}/v2/find-limit-with-net-loss")
	PlayerLimitV2Dto findPlayerLimitV2WithNetLoss(
			@PathVariable("domainName") String domainName,
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("granularity") Integer granularity,
			@RequestParam("type") Integer type);

	// Player Deposit Limit
	@RequestMapping(method = RequestMethod.POST, path = "/system/depositlimit")
	PlayerLimit saveDepositLimit(
			@RequestParam(name = "playerGuid") String playerGuid,
			@RequestParam(name = "granularity") Integer granularity,
			@RequestParam(name = "amount") String amount
	) throws
			Status100InvalidInputDataException,
			Status480PendingDepositLimitException,
			Status481DomainDepositLimitDisabledException,
			Status500LimitInternalSystemClientException,
			Status550ServiceDomainClientException;

	@RequestMapping(method = RequestMethod.POST, path = "/system/balance-limit")
	PlayerLimit saveBalanceLimit(@RequestParam(name = "playerGuid") String playerGuid,
								 @RequestParam(name = "amount") String amount) throws
			Status100InvalidInputDataException,
			Status480PendingDepositLimitException,
			Status481DomainDepositLimitDisabledException,
			Status500LimitInternalSystemClientException,
			Status550ServiceDomainClientException,
			Status476DomainBalanceLimitDisabledException;

	@RequestMapping(method = RequestMethod.GET, path = "/system/restrictions")
	Restrictions lookupRestrictions(@RequestParam("playerGuid") String playerGuid, @RequestParam("locale") String locale);

	@RequestMapping(method = RequestMethod.GET, path = "/system/restrictions/checkAccess")
	Access checkAccess(@RequestParam("playerGuid") String playerGuid);

	@RequestMapping(method = RequestMethod.POST, path = "/system/restrictions/check-access-localized")
	Access checkAccessLocalized(@RequestParam("playerGuid") String playerGuid, @RequestParam("locale") String locale);

	@RequestMapping(method = RequestMethod.GET, path = "/system/restrictions/getVerificationStatusCode")
	String getVerificationStatusCode(@RequestParam("verificationStatusId") Long verificationStatusId);

	@RequestMapping(method = RequestMethod.GET, path = "/system/restrictions/getVerificationStatusLevel")
	Integer getVerificationStatusLevel(@RequestParam("verificationStatusId") Long verificationStatusId);

	@RequestMapping(method = RequestMethod.GET, path = "/system/restrictions/get-verification-status-level-age-override")
	Integer getVerificationStatusLevelAgeOverride(@RequestParam("verificationStatusId") Long verificationStatusId,
												@RequestParam("domainName") String domainName);

	@RequestMapping(method = RequestMethod.GET, path = "/system/restrictions/get-all-verification-status")
	List<VerificationStatusDto> getVerificationStatuses();

	@RequestMapping(method = RequestMethod.GET, path = "/system/limits-summary/find-player-summary-limits")
	PlayerLimitSummaryFE getPlayerLimitSummary(@RequestParam("playerGuid") String playerGuid);

	@RequestMapping(value = "/system/restrictions/{domainName}/set-promotions-opt-out", method = RequestMethod.POST)
	void setPromotionsOptOut(@PathVariable("domainName") String domainName, @RequestParam("playerGuid") String playerGuid, @RequestParam("optOut") boolean optOut,  @RequestParam("userId") Long userId) throws Status403PlayerRestrictionDeniedException,
			Status409PlayerRestrictionConflictException, Status500InternalServerErrorException;

	@RequestMapping(value = "/system/restrictions/set-many", method = RequestMethod.POST)
	Response<List<UserRestrictionSet>> setMany(@RequestBody UserRestrictionsRequest userRestrictionsRequest);

	@RequestMapping(value = "/system/restrictions/lift-many", method = RequestMethod.DELETE)
	Response<List<UserRestrictionSet>> liftMany(@RequestBody UserRestrictionsRequest userRestrictionsRequest);

	@RequestMapping(value = "/system/restrictions/auto-restriction/trigger", method = RequestMethod.POST)
	void autoRestrictionTrigger(@RequestParam("playerGuid") String playerGuid);

	@RequestMapping(value = "/system/player-limit/get-loss-limit-visibility", method = RequestMethod.POST)
	lithium.service.limit.client.objects.User getLossLimitVisibility(
			@RequestParam("playerGuid") String playerGuid
	);
	@RequestMapping(value = "/system/player-limit/set-loss-limit-visibility", method = RequestMethod.POST)
	lithium.service.limit.client.objects.User setLossLimitVisibility(
			@RequestParam("playerGuid") String playerGuid,
			@RequestParam("visibility") LossLimitsVisibility visibility
	);

}
