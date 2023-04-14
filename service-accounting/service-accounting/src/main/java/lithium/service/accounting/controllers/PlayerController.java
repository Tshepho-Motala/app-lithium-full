package lithium.service.accounting.controllers;

import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.LithiumMetricsService;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.objects.PlayerBalanceResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
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
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/player/{domainName}")
@EnableCustomHttpErrorCodeExceptions
public class PlayerController {
	@Autowired LithiumMetricsService metrics;
	@Autowired BalanceController balanceController;
	@Autowired LimitInternalSystemService limits;
	@Autowired UserApiInternalClientService userApiInternalClientService;
	@Autowired MessageSource messageSource;
	@Autowired CachingDomainClientService domainClientService;

	// TODO: Handle locale
	private static final String LOCALE_EN_US = "en_US";

	@RequestMapping("/all/balances")
	public Response<List<PlayerBalanceResponse>> allBalances(
		@PathVariable("domainName") String domainName,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		if (!domainName.contentEquals(tokenUtil.domainName()))
			return Response.<List<PlayerBalanceResponse>>builder().status(Status.CONFLICT).message("Domain mismatch").build();
		try {
			userApiInternalClientService.performUserChecks(tokenUtil.guid(), LOCALE_EN_US, tokenUtil.sessionId(),
					true, true, false);

			limits.checkPlayerRestrictions(tokenUtil.guid(), LOCALE_EN_US);

			return balanceController.getAllByOwnerGuid(domainName, tokenUtil.guid());
		} catch (Status500UserInternalSystemClientException | Status500LimitInternalSystemClientException e) {
			throw new Status500InternalServerErrorException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "Internal server error.", LocaleContextHolder.getLocale()));
		} catch (Status491PermanentSelfExclusionException | Status490SoftSelfExclusionException |
				Status405UserDisabledException | Status496PlayerCoolingOffException e) {
			throw e;
		}
	}
	
	@RequestMapping("/balances/{currencyCode}")
	public Response<Map<String, Long>> balances(
		@PathVariable("domainName") String domainName,
		@PathVariable("currencyCode") String currencyCode,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		return metrics.timer(log).time("balances", (StopWatch sw) -> {
			log.debug("/player/{"+domainName+"}/balances/{"+currencyCode+"} " + tokenUtil.guid());

			userApiInternalClientService.performUserChecks(tokenUtil.guid(), LOCALE_EN_US, tokenUtil.sessionId(),
					true, true, false);

			limits.checkPlayerRestrictions(tokenUtil.guid(), LOCALE_EN_US);

			Map<String, Long> result = new HashMap<>();
			sw.start("PLAYER_BALANCE");
			Response<Long> playerBalance = balanceController.getByOwnerGuid(domainName, "PLAYER_BALANCE", "PLAYER_BALANCE", currencyCode, tokenUtil.guid());
			if (playerBalance.isSuccessful()) {
				if (Boolean.parseBoolean(domainClientService.getDomainSetting(domainName, DomainSettings.OVERRIDE_NEGATIVE_BALANCE_DISPLAY))) {
					result.put("PLAYER_BALANCE", playerBalance.getData() < 0 ? 0L : playerBalance.getData());
				} else {
					result.put("PLAYER_BALANCE", playerBalance.getData());
				}
			}
			sw.stop();

			sw.start("PLAYER_BALANCE_CASINO_BONUS");
			Response<Long> playerBalanceCasinoBonus = balanceController.getByOwnerGuid(domainName, "PLAYER_BALANCE_CASINO_BONUS", "PLAYER_BALANCE", currencyCode, tokenUtil.guid());
			if (playerBalanceCasinoBonus.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS", playerBalanceCasinoBonus.getData());
			sw.stop();

			sw.start("PLAYER_BALANCE_CASINO_BONUS_PENDING");
			Response<Long> playerBalanceCasinoBonusPending = balanceController.getByOwnerGuid(domainName, "PLAYER_BALANCE_CASINO_BONUS_PENDING", "PLAYER_BALANCE", currencyCode, tokenUtil.guid());
			if (playerBalanceCasinoBonusPending.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS_PENDING", playerBalanceCasinoBonusPending.getData());
			sw.stop();

			log.debug("/player/{"+domainName+"}/balances/{"+currencyCode+"} " + tokenUtil.guid() + " " + result);
			return Response.<Map<String, Long>>builder().data(result).status(Status.OK).build();
		});
	}

	@RequestMapping("/balances/v2/{currencyCode}")
	public Response<Map<String, BigDecimal>> balancesAmount(
		@PathVariable("domainName") String domainName,
		@PathVariable("currencyCode") String currencyCode,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		return metrics.timer(log).time("balances", (StopWatch sw) -> {
			log.debug("Balance request (/balances/v2/"+currencyCode+" for: "+tokenUtil.guid()+" on domain: "+domainName);

			userApiInternalClientService.performUserChecks(tokenUtil.guid(), LOCALE_EN_US, tokenUtil.sessionId(),
					true, true, false);

			limits.checkPlayerRestrictions(tokenUtil.guid(), LOCALE_EN_US);

			Map<String, BigDecimal> result = new HashMap<>();
			sw.start("PLAYER_BALANCE");
			Response<Long> playerBalance = balanceController.getByOwnerGuid(domainName, "PLAYER_BALANCE", "PLAYER_BALANCE", currencyCode, tokenUtil.guid());
			if (playerBalance.isSuccessful()) {
				if (Boolean.parseBoolean(domainClientService.getDomainSetting(domainName, DomainSettings.OVERRIDE_NEGATIVE_BALANCE_DISPLAY))) {
					result.put("PLAYER_BALANCE", CurrencyAmount.fromCents(playerBalance.getData()).toAmount().compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : CurrencyAmount.fromCents(playerBalance.getData()).toAmount());
				} else {
					result.put("PLAYER_BALANCE", CurrencyAmount.fromCents(playerBalance.getData()).toAmount());
				}
			}
			sw.stop();

			sw.start("PLAYER_BALANCE_CASINO_BONUS");
			Response<Long> playerBalanceCasinoBonus = balanceController.getByOwnerGuid(domainName, "PLAYER_BALANCE_CASINO_BONUS", "PLAYER_BALANCE", currencyCode, tokenUtil.guid());
			if (playerBalanceCasinoBonus.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS", CurrencyAmount.fromCents(playerBalanceCasinoBonus.getData()).toAmount());
			sw.stop();

			sw.start("PLAYER_BALANCE_CASINO_BONUS_PENDING");
			Response<Long> playerBalanceCasinoBonusPending = balanceController.getByOwnerGuid(domainName, "PLAYER_BALANCE_CASINO_BONUS_PENDING", "PLAYER_BALANCE", currencyCode, tokenUtil.guid());
			if (playerBalanceCasinoBonusPending.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS_PENDING", CurrencyAmount.fromCents(playerBalanceCasinoBonusPending.getData()).toAmount());
			sw.stop();

			log.debug("Balance response (/balances/v2/" + currencyCode + " for: "
					+ tokenUtil.guid() + " on domain: " + domainName + " " + result);
			return Response.<Map<String, BigDecimal>>builder().data(result).status(Status.OK).build();
		});
	}
}
