package lithium.service.accounting.service;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.AccountingClientWithExceptions;
import lithium.service.accounting.client.AccountingFrontendClient;
import lithium.service.accounting.client.AccountingPeriodClient;
import lithium.service.accounting.client.AccountingSummaryAccountClient;
import lithium.service.accounting.client.AccountingSummaryAccountLabelValueClient;
import lithium.service.accounting.client.AccountingSummaryDomainLabelValueClient;
import lithium.service.accounting.client.AccountingSummaryDomainTransactionTypeClient;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.client.AdminTransactionsClient;
import lithium.service.accounting.client.SystemSummaryReconciliationClient;
import lithium.service.accounting.client.SystemTransactionClient;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AccountingService {
	@Autowired Environment environment;
	@Autowired LithiumServiceClientFactory lithiumServiceClientFactory;
	
	public AccountingClient accountingClient(String url) throws Status510AccountingProviderUnavailableException {
		try {
			return lithiumServiceClientFactory.target(AccountingClient.class, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public AccountingClientWithExceptions accountingClientWithExceptions(String url) throws Status510AccountingProviderUnavailableException {
		try {
			return lithiumServiceClientFactory.target(AccountingClientWithExceptions.class, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public AccountingClient accountingClient(boolean readOnly) throws Status510AccountingProviderUnavailableException {
		try {
			String providerUrl = provider(null, readOnly, false).getUrl();
			log.debug("Accounting call using : "+providerUrl);
			return lithiumServiceClientFactory.target(AccountingClient.class, providerUrl, true);
		} catch (Exception e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public AccountingFrontendClient accountingFrontendClient() throws Exception {
		String providerUrl = provider(null, true, false).getUrl();
		log.debug("Accounting Frontend call using : "+providerUrl);
		return lithiumServiceClientFactory.target(AccountingFrontendClient.class, providerUrl,true);
	}

	public AccountingSummaryDomainTransactionTypeClient summaryDomainTransactionTypeClient() throws Exception {
		String providerUrl = provider(null, true, false).getUrl();
		log.debug("Accounting Summary Domain TransactionType call using : "+providerUrl);
		return lithiumServiceClientFactory.target(AccountingSummaryDomainTransactionTypeClient.class, providerUrl,true);
	}

	public AccountingSummaryTransactionTypeClient summaryTransactionTypeClient() throws Exception {
		String providerUrl = provider(null, true, false).getUrl();
		log.debug("Accounting Summary TransactionType call using : "+providerUrl);
		return lithiumServiceClientFactory.target(AccountingSummaryTransactionTypeClient.class, providerUrl,true);
	}

	public AccountingSummaryAccountLabelValueClient summaryAccountLabelValueClient() throws Status510AccountingProviderUnavailableException {
		try {
			String providerUrl = provider(null, true, false).getUrl();
			log.debug("Accounting Summary LabelValue call using : " + providerUrl);
			return lithiumServiceClientFactory.target(AccountingSummaryAccountLabelValueClient.class, providerUrl, true);
		} catch (Exception e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public AccountingSummaryAccountClient summaryAccountClient() throws Exception {
		String providerUrl = provider(null, true, false).getUrl();
		log.debug("Accounting Summary Account call using : "+providerUrl);
		return lithiumServiceClientFactory.target(AccountingSummaryAccountClient.class, providerUrl,true);
	}

	public AccountingSummaryDomainLabelValueClient summaryDomainLabelValueClient() throws Exception {
		String providerUrl = provider(null, true, false).getUrl();
		log.debug("Accounting Summary Domain LabelValue call using : "+providerUrl);
		return lithiumServiceClientFactory.target(AccountingSummaryDomainLabelValueClient.class, providerUrl,true);
	}

	public AdminTransactionsClient adminTransactionsClient() throws Status510AccountingProviderUnavailableException {
		try {
			String providerUrl = provider(null, true, false).getUrl();
			log.debug("Admin transactions call using : "+providerUrl);
			return lithiumServiceClientFactory.target(AdminTransactionsClient.class, providerUrl,true);
		} catch (Exception e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public SystemTransactionClient systemTransactionClient() throws Status510AccountingProviderUnavailableException {
		try {
			String providerUrl = provider(null, true, false).getUrl();
			log.debug("System transaction call using : "+providerUrl);
			return lithiumServiceClientFactory.target(SystemTransactionClient.class, providerUrl,true);
		} catch (Exception e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public AccountingPeriodClient periodClient() throws Status510AccountingProviderUnavailableException {
		try {
			String providerUrl = provider(null, true, false).getUrl();
			log.debug("Accounting period call using : "+providerUrl);
			return lithiumServiceClientFactory.target(AccountingPeriodClient.class, providerUrl,true);
		} catch (Exception e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}

	public SystemSummaryReconciliationClient systemSummaryReconciliationClient()
			throws Status510AccountingProviderUnavailableException {
		try {
			String providerUrl = provider(null, true, true).getUrl();
			log.debug("System summary reconciliation call using : "+providerUrl);
			return lithiumServiceClientFactory.target(SystemSummaryReconciliationClient.class, providerUrl,
					true);
		} catch (Exception e) {
			throw new Status510AccountingProviderUnavailableException(e.getMessage());
		}
	}
	
	@Cacheable(value = "lithium.service.accounting.service.Provider")
	public Provider provider(String domainName) throws Exception {
		return provider(domainName, false, false);
	}

	@Cacheable(value = "lithium.service.accounting.service.Provider")
	public Provider provider(String domainName, boolean checkReadOnly, boolean checkSecondaryReadOnly) throws Exception {
		log.warn("####################### NOT FROM CACHE #######################");
		Provider provider = null;
		if (domainName != null) {
			log.info("provider(" + domainName + ")");
			ProviderClient providerClient = lithiumServiceClientFactory.target(ProviderClient.class, true);
			log.debug("ProviderClient :" + providerClient);
			Response<Iterable<Provider>> response = providerClient.listByDomainAndType(domainName, ProviderType.ACCOUNTING.type());
			log.debug("Response :" + response);
			Iterable<Provider> list = response.getData();
			log.debug("List :" + list);
			Map<Integer, Provider> map = StreamSupport.stream(list.spliterator(), true)
				.filter(p -> {
					if (p.getProviderType().getName().equals(ProviderType.ACCOUNTING.name())) {
						if (p.getEnabled()) return true;
					}
					return false;
				})
				.sorted((p1, p2) -> p1.getPriority().compareTo(p2.getPriority()))
				.collect(
					Collectors.toMap(
						Provider::getPriority,
						Function.identity()
					)
				);
			log.debug("Map :" + map);
			provider = map.get(1);
			log.debug("Return :" + provider);
		}
		if (provider == null) {
			Boolean secondaryReadOnlyEnabled = environment.getProperty("lithium.enable-secondary-read-only",
					Boolean.class, false);
			Boolean readOnlyEnabled = environment.getProperty("lithium.enable-read-only", Boolean.class, false);
			if ((checkSecondaryReadOnly) && (secondaryReadOnlyEnabled)) {
				provider = Provider.builder()
					.url("service-accounting-provider-readonly-secondary")
					.build();
			} else if ((checkReadOnly) && (readOnlyEnabled)) {
				provider = Provider.builder()
					.url("service-accounting-provider-readonly")
					.build();
			} else {
				provider = Provider.builder()
					.url("service-accounting-provider-internal")
					.build();
			}
		}
		return provider;
	}
}
