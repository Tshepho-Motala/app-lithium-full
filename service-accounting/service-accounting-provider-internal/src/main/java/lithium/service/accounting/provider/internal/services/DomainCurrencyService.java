package lithium.service.accounting.provider.internal.services;

import java.util.Iterator;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lithium.service.accounting.objects.DomainCurrencyBasic;
import lithium.service.accounting.provider.internal.data.entities.Currency;
import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.DomainCurrency;
import lithium.service.accounting.provider.internal.data.repositories.CurrencyRepository;
import lithium.service.accounting.provider.internal.data.repositories.DomainCurrencyRepository;
import lithium.service.accounting.provider.internal.data.repositories.specifications.DomainCurrencySpecification;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DomainCurrencyService {
	@Autowired CurrencyRepository currencyRepository;
	@Autowired DomainService domainService;
	@Autowired DomainCurrencyRepository repository;
	@Autowired LithiumServiceClientFactory serviceFactory;
	
	public DomainClient domainClient() throws LithiumServiceClientFactoryException {
		return serviceFactory.target(DomainClient.class, "service-domain", true);
	}
	
	public void updateServiceDomainCurrency(String domainName, String code, String symbol) throws Exception {
		log.info("updateServiceDomainCurrency :: "+domainName+" "+code+" "+symbol);
		domainClient().updateCurrency(domainName, symbol, code);
	}
	
	/**
	 * Used on startup to sync the default currency as it is on svc-domain
	 */
	public void updateDomainDefaultCurrency() throws Exception {
		Iterable<lithium.service.domain.client.objects.Domain> domains = domainClient().findAllDomains().getData();
		Iterator<lithium.service.domain.client.objects.Domain> iterator = domains.iterator();
		while (iterator.hasNext()) {
			lithium.service.domain.client.objects.Domain domain = iterator.next();
			syncDefaultCurrency(domain.getName(),
				DomainCurrencyBasic.builder()
				.code(domain.getCurrency())
				.name(domain.getCurrency())
				.symbol(domain.getCurrencySymbol())
				.build()
			);
		}
	}
	
	public DomainCurrency syncDefaultCurrency(
		String domainName,
		DomainCurrencyBasic domainCurrencyBasic
	) throws Exception {
		log.info("Recived syncDefaultCurrency request [domainName="+domainName+", domainCurrencyBasic="+domainCurrencyBasic+"]");
		Domain domain = domainService.findOrCreate(domainName);
		DomainCurrency domainCurrency = repository.findByDomainNameAndCurrencyCode(domainName, domainCurrencyBasic.getCode());
		if (domainCurrency == null) {
			Currency currency = currencyRepository.findByCode(domainCurrencyBasic.getCode());
			if (currency == null) {
				currency = Currency.builder()
				.name(domainCurrencyBasic.getName())
				.code(domainCurrencyBasic.getCode())
				.real(false)
				.build();
				currency = currencyRepository.save(currency);
			}
			domainCurrency = DomainCurrency.builder()
			.domain(domain)
			.currency(currency)
			.name(domainCurrencyBasic.getName())
			.symbol(domainCurrencyBasic.getSymbol())
			.build();
			domainCurrency = repository.save(domainCurrency);
		} else {
			domainCurrency.setSymbol(domainCurrencyBasic.getSymbol());
			domainCurrency = repository.save(domainCurrency);
		}
		domainCurrency = setAsDefault(domainCurrency, false);
		log.info("Returning [domainCurrency="+domainCurrency+"]");
		return domainCurrency;
	}
	
	public Page<DomainCurrency> findByDomain(String domainName, String currency, String searchValue, Pageable pageable) {
		Specification<DomainCurrency> spec = Specification.where(DomainCurrencySpecification.domain(domainName));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<DomainCurrency> s = Specification.where(DomainCurrencySpecification.any(searchValue));
			spec = (spec == null) ? s : spec.and(s);
		}
		Page<DomainCurrency> result = repository.findAll(spec, pageable);
		return result;
	}
	
	public List<DomainCurrency> findByDomain(String domainName) {
		return repository.findByDomainName(domainName);
	}
	@Transactional(rollbackFor=Exception.class)
	public DomainCurrency save(String domainName, String code, String name, String description, String symbol, Integer divisor) throws Exception {
		Domain domain = domainService.findOrCreate(domainName);
		Currency currency = currencyRepository.findByCode(code);
		if (currency == null) {
			currency = Currency.builder().code(code).real(false).name(name).build();
			currency = currencyRepository.save(currency);
		}
		DomainCurrency domainCurrency = repository.findByDomainNameAndCurrencyCode(domainName, code);
		if (domainCurrency == null) {
			domainCurrency = DomainCurrency.builder()
			.domain(domain)
			.currency(currency)
			.name(name)
			.description(description)
			.isDefault(false)
			.symbol(symbol)
			.divisor(divisor)
			.build();
		} else {
			domainCurrency = domainCurrency.toBuilder()
			.name(name)
			.description(description)
			.symbol(symbol)
			.divisor(divisor)
			.build();
		}
		domainCurrency = repository.save(domainCurrency);
		if (domainCurrency.getIsDefault()) {
			updateServiceDomainCurrency(domainCurrency.getDomain().getName(), domainCurrency.getCurrency().getCode(), domainCurrency.getSymbol());
		}
		return domainCurrency;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public DomainCurrency setAsDefault(DomainCurrency dc, boolean updateServiceDomain) throws Exception {
		DomainCurrency defaultDomainCurrency = repository.findByDomainNameAndIsDefaultTrue(dc.getDomain().getName());
		if (defaultDomainCurrency != null &&
			!defaultDomainCurrency.getCurrency().getCode().equalsIgnoreCase(dc.getCurrency().getCode())) {
				defaultDomainCurrency.setIsDefault(false);
				defaultDomainCurrency = repository.save(defaultDomainCurrency);
		}
		
		dc.setIsDefault(true);
		dc = repository.save(dc);
		
		if (updateServiceDomain) {
			updateServiceDomainCurrency(dc.getDomain().getName(), dc.getCurrency().getCode(), dc.getSymbol());
		}
		
		return dc;
	}
	
	public void delete(DomainCurrency domainCurrency) {
		repository.delete(domainCurrency);
	}
}
