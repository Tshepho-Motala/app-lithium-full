package lithium.service.casino.client;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.service.casino.client.objects.request.BalanceAdjustmentRequest;
import lithium.service.casino.client.objects.response.BalanceAdjustmentResponse;
import lithium.service.casino.exceptions.Status471InsufficientFundsException;
import lithium.service.casino.exceptions.Status511UpstreamServiceUnavailableException;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Provides endpoints that can be used for multiple adjustments in a single transaction.
 */
@FeignClient(name="service-casino")
public interface MultiBetClient {

	/**
	 * Process adjustment transactions based on a list of transaction components as opposed to the
	 * old method where no indication was given on the type of adjustment that is truly required.
	 * <p>
	 * The adjustment components will be handled in the order in which they were inserted.
	 *
	 * @param request
	 * @param locale
	 * @return BalanceAdjustmentResponse
	 * @throws
	 */
	//TODO: Add status code exceptions
	@RequestMapping("/system/casino/multi-bet/v1")
	@ResponseBody
	public BalanceAdjustmentResponse multiBetV1(
			@RequestBody BalanceAdjustmentRequest request,
			@RequestParam(name = "locale", defaultValue = "en_US") String locale
	) throws
			Status401UnAuthorisedException,
			Status405UserDisabledException,
			Status471InsufficientFundsException,
			Status473DomainBettingDisabledException,
			Status474DomainProviderDisabledException,
			Status484WeeklyLossLimitReachedException,
			Status485WeeklyWinLimitReachedException,
			Status490SoftSelfExclusionException,
			Status491PermanentSelfExclusionException,
			Status492DailyLossLimitReachedException,
			Status493MonthlyLossLimitReachedException,
			Status494DailyWinLimitReachedException,
			Status495MonthlyWinLimitReachedException,
			Status496PlayerCoolingOffException,
			Status511UpstreamServiceUnavailableException,
			Status550ServiceDomainClientException,
			Status478TimeSlotLimitException,
			Status438PlayTimeLimitReachedException;

	@RequestMapping("/system/casino/negative-balance-asjust/v1")
	@ResponseBody
	BalanceAdjustmentResponse negativeBalanceAdjust(
		@RequestBody BalanceAdjustmentRequest request,
		@RequestParam(name = "locale", defaultValue = "en_US") String locale
	) throws Status511UpstreamServiceUnavailableException;
}
