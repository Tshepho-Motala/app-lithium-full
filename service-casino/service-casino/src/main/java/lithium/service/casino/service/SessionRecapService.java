package lithium.service.casino.service;

import lithium.casino.CasinoTransactionLabels;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.client.AccountingPeriodClient;
import lithium.service.accounting.client.AccountingSummaryAccountLabelValueClient;
import lithium.service.accounting.enums.Granularity;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccountLabelValue;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.api.frontend.schema.SessionRecap;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SessionRecapService {
	@Autowired private LithiumServiceClientFactory services;
	@Autowired private CachingDomainClientService cachingDomainClientService;

	@TimeThisMethod
	public SessionRecap sessionRecap(LithiumTokenUtil tokenUtil) throws Status550ServiceDomainClientException,
			Status500InternalServerErrorException {
		Long loginEventId = tokenUtil.sessionId();
		String domainName = tokenUtil.domainName();
		SW.start("getDefaultCurrency");
		String currencyCode = cachingDomainClientService.getDefaultDomainCurrency(domainName);
		SW.stop();

		try {
			Long totalBetCents = 0L;
			Long totalWinCents = 0L;

			List<SummaryAccountLabelValue> summaries = getSummaries(domainName, loginEventId, currencyCode);
			for (SummaryAccountLabelValue summary : summaries) {
				if (isBet(summary.getTransactionType().getCode())) {
					totalBetCents = addBetCents(totalBetCents, summary);
				} else {
					totalWinCents = addWinCents(totalWinCents, summary);
				}
			}

			log.info(SW.getFromThreadLocal().prettyPrint());

			// FIXME: Because of this calculation, until bets are settled, the loss value will be inflated.
			Long totalLossCents = totalBetCents - totalWinCents;

			return SessionRecap.builder()
					.totalStakeCents(totalBetCents)
					.totalWinCents(totalWinCents)
					.totalLossCents(totalLossCents)
					.build();
		} catch (Exception e) {
			String msg = "Failed to retrieve data from accounting";
			log.error(msg + "[user.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
	}

	private List<SummaryAccountLabelValue> getSummaries(String domainName, Long loginEventId, String currencyCode)
			throws Exception {
		SW.start("period");
		Period period = getAccountingPeriodClient().get().findByOffset(domainName,
				Granularity.GRANULARITY_TOTAL.id(), 0).getData();
		log.trace("period " + period);
		SW.stop();

		SW.start("summaries");
		List<SummaryAccountLabelValue> summaries = getAccountingSummaryAccountLabelValueClient().get().find(
				domainName, period.getId(), CasinoTranType.PLAYERBALANCE.value(), transactionTypes(),
				String.valueOf(loginEventId), CasinoTransactionLabels.LOGIN_EVENT_ID, currencyCode).getData();
		SW.stop();
		log.trace("summaries " + summaries);

		return summaries;
	}

	private List<String> transactionTypes() {
		List<String> transactionTypes = new ArrayList<>();
		transactionTypes.add(CasinoTranType.CASINO_BET.value());
		transactionTypes.add(CasinoTranType.CASINO_WIN.value());
		transactionTypes.add(CasinoTranType.SPORTS_BET.value());
		transactionTypes.add(CasinoTranType.SPORTS_WIN.value());
		transactionTypes.add(CasinoTranType.VIRTUAL_BET.value());
		transactionTypes.add(CasinoTranType.VIRTUAL_WIN.value());
		return transactionTypes;
	}

	private boolean isBet(String transactionType) {
		return transactionType.contentEquals(CasinoTranType.CASINO_BET.value()) ||
				transactionType.contentEquals(CasinoTranType.SPORTS_BET.value()) ||
				transactionType.contentEquals(CasinoTranType.VIRTUAL_BET.value());
	}

	private Long addBetCents(Long totalBetCents, SummaryAccountLabelValue summary) {
		totalBetCents += (summary == null) ? 0L : summary.getDebitCents() - summary.getCreditCents();
		return totalBetCents;
	}

	private Long addWinCents(Long totalWinCents, SummaryAccountLabelValue summary) {
		totalWinCents += (summary == null) ? 0L :  summary.getCreditCents() - summary.getDebitCents();
		return totalWinCents;
	}

	private Optional<AccountingPeriodClient> getAccountingPeriodClient() {
		return getClient(AccountingPeriodClient.class, "service-accounting");
	}

	private Optional<AccountingSummaryAccountLabelValueClient> getAccountingSummaryAccountLabelValueClient() {
		return getClient(AccountingSummaryAccountLabelValueClient.class, "service-accounting");
	}

	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;

		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}

		return Optional.ofNullable(clientInstance);
	}
}
