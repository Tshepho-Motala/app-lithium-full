package lithium.service.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.accounting.client.AccountingDomainCurrencyClient;
import lithium.service.accounting.objects.DomainCurrency;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.data.entities.Domain;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DomainCurrencyService {
	@Autowired LithiumServiceClientFactory services;
	
	public AccountingDomainCurrencyClient getAccountingDomainCurrencyClient() {
		AccountingDomainCurrencyClient client = null;
		try {
			client = services.target(AccountingDomainCurrencyClient.class, "service-accounting-provider-internal", true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return client;
	}
	
	public DomainCurrency syncDefaultCurrency(Domain domain) throws Exception {
		log.info("Syncing default currency with service-accounting-pr-internal [domain="+domain+"]");
		DomainCurrency domainCurrency = getAccountingDomainCurrencyClient().syncDefaultCurrency(
			domain.getName(),
			domain.getCurrency(),
			domain.getCurrency(),
			domain.getCurrencySymbol()
		).getData();
		log.info("Sync completed");
		return domainCurrency;
	}
}
