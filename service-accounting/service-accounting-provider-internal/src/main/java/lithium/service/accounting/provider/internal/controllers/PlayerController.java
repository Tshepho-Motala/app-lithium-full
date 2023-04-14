package lithium.service.accounting.provider.internal.controllers;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.PlayerBalanceResponse;
import lithium.service.accounting.provider.internal.services.BalanceService;
import lithium.service.accounting.provider.internal.services.PlayerService;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/player/{domainName}")
public class PlayerController {
	@Autowired Environment environment;
	@Autowired BalanceService balanceService;
	@Autowired PlayerService playerService;
	@Autowired TokenStore tokenStore;
	@Autowired LimitInternalSystemService limits;
	@Autowired UserApiInternalClientService userApiInternalClientService;
	@Autowired MessageSource messageSource;

	@RequestMapping("/all/balances")
	public Response<List<PlayerBalanceResponse>> allBalances(
		@PathVariable("domainName") String domainName,
		@RequestParam(name = "locale", defaultValue = "en_US") String locale,
		LithiumTokenUtil tokenUtil
	) throws Status401UnAuthorisedException, Status491PermanentSelfExclusionException,
			Status490SoftSelfExclusionException, Status405UserDisabledException,
			Status496PlayerCoolingOffException, Status500InternalServerErrorException {
		if (!domainName.contentEquals(tokenUtil.domainName()))
			return Response.<List<PlayerBalanceResponse>>builder().status(Status.CONFLICT).message("Domain mismatch").build();

		try {
			userApiInternalClientService.performUserChecks(tokenUtil.guid(), locale, tokenUtil.sessionId(),
					true, true, false);

			limits.checkPlayerRestrictions(tokenUtil.guid(), locale);

			return Response.<List<PlayerBalanceResponse>>builder().data(balanceService.allBalances(domainName, tokenUtil.guid())).status(Status.OK).build();
		} catch (Status500UserInternalSystemClientException | Status500LimitInternalSystemClientException e) {
			throw new Status500InternalServerErrorException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "Internal server error.", LocaleContextHolder.getLocale()), e.getStackTrace());
		} catch (Status401UnAuthorisedException | Status491PermanentSelfExclusionException |
				Status490SoftSelfExclusionException | Status405UserDisabledException |
				Status496PlayerCoolingOffException  e) {
			throw e; //This is being translated in another method call.
		}
	}

	@RequestMapping("/balances/{currencyCode}")
	public Response<HashMap<String, Long>> balances(
		@PathVariable("domainName") String domainName,
		@PathVariable("currencyCode") String currencyCode,
		@RequestParam(name = "locale", defaultValue = "en_US") String locale,
		LithiumTokenUtil tokenUtil
	) throws    Status500LimitInternalSystemClientException, Status401UnAuthorisedException,
				Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException,
				Status405UserDisabledException, Status500UserInternalSystemClientException,
				Status496PlayerCoolingOffException {
		log.debug("/player/{"+domainName+"}/balances/{"+currencyCode+"}");

		userApiInternalClientService.performUserChecks(tokenUtil.guid(), locale, tokenUtil.sessionId(),
				true, true, false);

		limits.checkPlayerRestrictions(tokenUtil.guid(), locale);

		return Response.<HashMap<String, Long>>builder().data(balanceService.getBalances(domainName, tokenUtil.guid(), currencyCode)).status(Status.OK).build();
	}

	@RequestMapping("/balances/v2/{currencyCode}")
	public Response<Map<String, BigDecimal>> balancesAmount(
		@PathVariable("domainName") String domainName,
		@PathVariable("currencyCode") String currencyCode,
		@RequestParam(name = "locale", defaultValue = "en_US") String locale,
		LithiumTokenUtil tokenUtil
	) throws    Status500LimitInternalSystemClientException, Status401UnAuthorisedException,
				Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException,
				Status405UserDisabledException, Status500UserInternalSystemClientException,
				Status496PlayerCoolingOffException {
		log.debug("Balance request (/player/{"+domainName+"}/balances/v2/"+currencyCode+" for: "+tokenUtil.guid());

		userApiInternalClientService.performUserChecks(tokenUtil.guid(), locale, tokenUtil.sessionId(),
				true, true, false);

		limits.checkPlayerRestrictions(tokenUtil.guid(), locale);

		return Response.<Map<String, BigDecimal>>builder().data(balanceService.getBalancesAmount(domainName, tokenUtil.guid(), currencyCode)).status(Status.OK).build();
	}

	@RequestMapping("/findNetLossToHouse")
	public Response<Long> findNetLossToHouse(@PathVariable("domainName") String domainName, @RequestParam("periodId") Long periodId, @RequestParam("currency") String currency, @RequestParam("playerGuid") String playerGuid) throws Exception {
		return playerService.findNetLossForPlayer(domainName, periodId, currency, playerGuid);
	}
}