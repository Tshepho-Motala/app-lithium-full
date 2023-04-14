package lithium.service.casino.client;

import lithium.exceptions.Status405UserDisabledException;
import lithium.service.Response;
import lithium.service.casino.client.data.Domain;
import lithium.service.casino.client.data.Winner;
import lithium.service.casino.client.objects.request.BalanceRequest;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.request.BonusReleaseRequest;
import lithium.service.casino.client.objects.request.RefundRequest;
import lithium.service.casino.client.objects.request.RollbackTranRequest;
import lithium.service.casino.client.objects.response.AccountInfoResponse;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.client.objects.response.BetResponse;
import lithium.service.casino.client.objects.response.BonusReleaseResponse;
import lithium.service.casino.client.objects.response.RefundResponse;
import lithium.service.casino.client.objects.response.RollbackTranResponse;
import lithium.service.casino.exceptions.Status423InvalidBonusTokenException;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.exceptions.Status500UnhandledCasinoClientException;
import lithium.service.domain.client.exceptions.Status473DomainBettingDisabledException;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
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

import java.util.List;

@FeignClient(name="service-casino", path="/casino")
public interface CasinoClient {
	
	@RequestMapping("/getBalance")
	public BalanceResponse handleBalanceRequest(@RequestBody BalanceRequest request) throws Exception;
	@RequestMapping("/bet")
	public BetResponse handleBetRequest(@RequestBody BetRequest request) throws Exception;

	@RequestMapping("/settle/v2")
	Response<BetResponse> handleSettleRequestV2(
		@RequestBody BetRequest request,
		@RequestParam(name="locale", defaultValue="en_US") String locale
	) throws Status500UnhandledCasinoClientException;

	@RequestMapping("/bet/v2")
	Response<BetResponse> handleBetRequestV2(
		@RequestBody BetRequest request,
		@RequestParam(name="locale", defaultValue="en_US") String locale
	) throws
		Status405UserDisabledException,
		Status423InvalidBonusTokenException,
		Status424InvalidBonusTokenStateException,
		Status438PlayTimeLimitReachedException,
		Status473DomainBettingDisabledException,
		Status474DomainProviderDisabledException,
		Status478TimeSlotLimitException,
		Status484WeeklyLossLimitReachedException,
		Status485WeeklyWinLimitReachedException,
		Status490SoftSelfExclusionException,
		Status491PermanentSelfExclusionException,
		Status492DailyLossLimitReachedException,
		Status493MonthlyLossLimitReachedException,
		Status494DailyWinLimitReachedException,
		Status495MonthlyWinLimitReachedException,
		Status496PlayerCoolingOffException,
		Status500UnhandledCasinoClientException;

	@RequestMapping("/zerowin")
	public BetResponse handleZeroWinRequest(@RequestBody BetRequest request) throws Exception;
	@RequestMapping("/bonusRelease")
	public BonusReleaseResponse handleBonusReleaseRequest(@RequestBody BonusReleaseRequest request) throws Exception;
//	@RequestMapping("/bonusWin")
//	public BonusWinResponse handleBonusWinRequest(@RequestBody BonusWinRequest request) throws Exception;
	@RequestMapping("/refund")
	public RefundResponse handleRefundRequest(@RequestBody RefundRequest request) throws Exception;
	
	@RequestMapping("/authenticate")
	public Response<Boolean> authenticate(@RequestParam("guid") String guid, @RequestParam("userApiToken") String userApiToken) throws Exception;
	
	@RequestMapping("/rollbackTran")
	public RollbackTranResponse rollbackTran(@RequestBody RollbackTranRequest rollbackTranRequest) throws Exception;
	
	@RequestMapping("/accountInfo")
	public AccountInfoResponse handleAccountInfoRequest(@RequestParam("guid") String guid, @RequestParam(name="userApiToken", required=false) String userApiToken) throws Exception;
	
	
	@RequestMapping("/winner/list")
	public Response<List<Winner>> handleWinnerListRequest(@RequestParam("domainName") String domainName) throws Exception;
	
	@RequestMapping("/winner/add")
	public void handleWinnerAddRequest(@RequestParam("domainName") Domain domain, @RequestParam("username") String username, @RequestParam("amount") Long amount, @RequestParam("gameName") String gameName) throws Exception;

	@RequestMapping("/winner/v2/add")
	public Response<Boolean> handleWinnerAddRequest(@RequestBody lithium.service.casino.client.data.Winner winner) throws Exception;
}
