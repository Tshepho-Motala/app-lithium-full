package lithium.service.casino.service;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.casino.api.frontend.schema.BetHistorySummary;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BetHistoryService {
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private LithiumServiceClientFactory serviceClientFactory;

	public BetHistorySummary getBetHistorySummary(String domainName, String playerGuid, Integer last, Integer granularity)
			throws Status550ServiceDomainClientException, Status510AccountingProviderUnavailableException {
		String currencyCode = cachingDomainClientService.getDefaultDomainCurrency(domainName);

		Long totalBetCents = 0L;
		Long totalBetCount = 0L;
		Long totalWinCents = 0L;
		Long totalWinCount = 0L;

		try {
			// Bet data
			Response<List<SummaryTransactionType>> bets = getAccountingSummaryTransactionTypeClient().get().findLastByOwnerGuid(
				domainName,
				playerGuid,
				last,
				granularity,
				"PLAYER_BALANCE",
				"CASINO_BET",
				currencyCode
			);
			Response<List<SummaryTransactionType>> freeSpinBets = getAccountingSummaryTransactionTypeClient().get().findLastByOwnerGuid(
					domainName,
					playerGuid,
					last,
					granularity,
					"PLAYER_BALANCE_CASINO_BONUS",
					"CASINO_BET_FREESPIN",
					currencyCode
			);
			List<SummaryTransactionType> betsData = bets.getData();
			betsData.addAll(freeSpinBets.getData());
			for (SummaryTransactionType betData : betsData) {
				totalBetCents += (betData == null) ? 0L : betData.getDebitCents() - betData.getCreditCents();
				totalBetCount += (betData == null) ? 0L : betData.getTranCount();
			}
			Response<List<SummaryTransactionType>> bonusBets = getAccountingSummaryTransactionTypeClient().get().findLastByOwnerGuid(
					domainName,
					playerGuid,
					last,
					granularity,
					"PLAYER_BALANCE_CASINO_BONUS",
					"CASINO_BET",
					currencyCode
			);
			List<SummaryTransactionType> bonusBetsData = bonusBets.getData();
			for (SummaryTransactionType bonusBetData : bonusBetsData) {
				totalBetCents += (bonusBetData == null) ? 0L : bonusBetData.getDebitCents() - bonusBetData.getCreditCents();
				totalBetCount += (bonusBetData == null) ? 0L : bonusBetData.getTranCount();
			}

			// Win data
			Response<List<SummaryTransactionType>> wins = getAccountingSummaryTransactionTypeClient().get().findLastByOwnerGuid(
				domainName,
				playerGuid,
				last,
				granularity,
				"PLAYER_BALANCE",
				"CASINO_WIN",
				currencyCode
			);
			Response<List<SummaryTransactionType>> freeSpinWins = getAccountingSummaryTransactionTypeClient().get().findLastByOwnerGuid(
					domainName,
					playerGuid,
					last,
					granularity,
					"PLAYER_BALANCE",
					"CASINO_WIN_FREESPIN",
					currencyCode
			);
			List<SummaryTransactionType> winsData = wins.getData();
			winsData.addAll(freeSpinWins.getData());
			for (SummaryTransactionType winData : winsData) {
				totalWinCents += (winData == null) ? 0L : winData.getCreditCents() - winData.getDebitCents();
				totalWinCount += (winData == null) ? 0L : winData.getTranCount();
			}
			Response<List<SummaryTransactionType>> bonusWins = getAccountingSummaryTransactionTypeClient().get().findLastByOwnerGuid(
				domainName,
				playerGuid,
				last,
				granularity,
				"PLAYER_BALANCE_CASINO_BONUS",
				"CASINO_WIN",
				currencyCode
			);
			List<SummaryTransactionType> bonusWinsData = bonusWins.getData();
			for (SummaryTransactionType bonusWinData : bonusWinsData) {
				totalWinCents += (bonusWinData == null) ? 0L : bonusWinData.getCreditCents() - bonusWinData.getDebitCents();
				totalWinCount += (bonusWinData == null) ? 0L : bonusWinData.getTranCount();
			}
		} catch (Exception e) {
			log.error("Failed to retrieve summary data from accounting [domainName="+domainName+", playerGuid="+playerGuid
				+", last="+last+", granularity="+granularity+"]");
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}

		return BetHistorySummary.builder()
			.totalBetCents(totalBetCents)
			.totalBetCount(totalBetCount)
			.totalWinCents(totalWinCents)
			.totalWinCount(totalWinCount)
			.totalProfitCents(totalWinCents - totalBetCents)
			.build();
	}

	private Optional<AccountingSummaryTransactionTypeClient> getAccountingSummaryTransactionTypeClient() {
		return getClient(AccountingSummaryTransactionTypeClient.class, "service-accounting");
	}

	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;

		try {
			clientInstance = serviceClientFactory.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}

		return Optional.ofNullable(clientInstance);
	}
}
