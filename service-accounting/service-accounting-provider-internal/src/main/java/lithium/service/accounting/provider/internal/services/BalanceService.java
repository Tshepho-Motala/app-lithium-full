package lithium.service.accounting.provider.internal.services;

import lithium.service.Response;
import lithium.service.accounting.objects.PlayerBalanceResponse;
import lithium.service.accounting.provider.internal.data.entities.DomainCurrency;
import lithium.math.CurrencyAmount;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// FIXME: This should not exist. Deprecate it once the casino balance endpoints have been completed. We don't want business logic in accounting.
@Service
public class BalanceService {
	@Autowired DomainCurrencyService domainCurrencyService;
	@Autowired TransactionServiceWrapper transactionServiceWrapper;
	@Autowired ModelMapper modelMapper;
	@Autowired CachingDomainClientService domainClientService;

	public List<PlayerBalanceResponse> allBalances(String domainName, String playerGuid) {
		List<PlayerBalanceResponse> balances = new ArrayList<>();
		List<DomainCurrency> currencies = domainCurrencyService.findByDomain(domainName);
		for (DomainCurrency currency: currencies) {
			lithium.service.accounting.objects.DomainCurrency objDomainCurrency = lithium.service.accounting.objects.DomainCurrency.builder().build();
			modelMapper.map(currency, objDomainCurrency);
			balances.add(
				PlayerBalanceResponse.builder()
				.domainCurrency(objDomainCurrency)
				.balances(getBalances(domainName, playerGuid, currency.getCurrency().getCode()))
				.build()
			);
		}
		return balances;
	}

	public HashMap<String, Long> getBalances(String domainName, String playerGuid, String currencyCode) {
		HashMap<String, Long> result = new HashMap<>();

		Response<Long> playerBalance = transactionServiceWrapper.getBalance(domainName, "PLAYER_BALANCE", "PLAYER_BALANCE", currencyCode, playerGuid);
		if (Boolean.parseBoolean(domainClientService.getDomainSetting(domainName, DomainSettings.OVERRIDE_NEGATIVE_BALANCE_DISPLAY))) {
			result.put("PLAYER_BALANCE", playerBalance.getData() < 0 ? 0L : playerBalance.getData());
		} else {
			result.put("PLAYER_BALANCE", playerBalance.getData());
		}

		Response<Long> playerBalanceCasinoBonus = transactionServiceWrapper.getBalance(domainName, "PLAYER_BALANCE_CASINO_BONUS", "PLAYER_BALANCE", currencyCode, playerGuid);
		if (playerBalanceCasinoBonus.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS", playerBalanceCasinoBonus.getData());

		Response<Long> playerBalanceCasinoBonusPending = transactionServiceWrapper.getBalance(domainName, "PLAYER_BALANCE_CASINO_BONUS_PENDING", "PLAYER_BALANCE", currencyCode, playerGuid);
		if (playerBalanceCasinoBonusPending.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS_PENDING", playerBalanceCasinoBonusPending.getData());

		return result;
	}
	private HashMap<String, Long> getBalancesHashMap(String domainName, String playerGuid, String currencyCode, HashMap<String, Long> result) {
		Response<Long> playerBalanceCasinoBonus = transactionServiceWrapper.getBalance(domainName, "PLAYER_BALANCE_CASINO_BONUS", "PLAYER_BALANCE", currencyCode, playerGuid);
		if (playerBalanceCasinoBonus.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS", playerBalanceCasinoBonus.getData());

		Response<Long> playerBalanceCasinoBonusPending = transactionServiceWrapper.getBalance(domainName, "PLAYER_BALANCE_CASINO_BONUS_PENDING", "PLAYER_BALANCE", currencyCode, playerGuid);
		if (playerBalanceCasinoBonusPending.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS_PENDING", playerBalanceCasinoBonusPending.getData());

		return result;
	}


	public Map<String, BigDecimal> getBalancesAmount(String domainName, String playerGuid, String currencyCode) {
		Map<String, BigDecimal> result = new HashMap<>();

		Response<Long> playerBalance = transactionServiceWrapper.getBalance(domainName, "PLAYER_BALANCE", "PLAYER_BALANCE", currencyCode, playerGuid);
		if (playerBalance.isSuccessful()) result.put("PLAYER_BALANCE", CurrencyAmount.fromCents(playerBalance.getData()).toAmount());

		Response<Long> playerBalanceCasinoBonus = transactionServiceWrapper.getBalance(domainName, "PLAYER_BALANCE_CASINO_BONUS", "PLAYER_BALANCE", currencyCode, playerGuid);
		if (playerBalanceCasinoBonus.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS", CurrencyAmount.fromCents(playerBalanceCasinoBonus.getData()).toAmount());

		Response<Long> playerBalanceCasinoBonusPending = transactionServiceWrapper.getBalance(domainName, "PLAYER_BALANCE_CASINO_BONUS_PENDING", "PLAYER_BALANCE", currencyCode, playerGuid);
		if (playerBalanceCasinoBonusPending.isSuccessful()) result.put("PLAYER_BALANCE_CASINO_BONUS_PENDING", CurrencyAmount.fromCents(playerBalanceCasinoBonusPending.getData()).toAmount());

		return result;
	}
}
