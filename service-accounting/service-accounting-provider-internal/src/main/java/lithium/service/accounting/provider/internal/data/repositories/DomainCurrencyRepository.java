package lithium.service.accounting.provider.internal.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.accounting.provider.internal.data.entities.DomainCurrency;

public interface DomainCurrencyRepository extends PagingAndSortingRepository<DomainCurrency, Long>, JpaSpecificationExecutor<DomainCurrency> {
	DomainCurrency findByDomainNameAndCurrencyCode(String domainName, String code);
	List<DomainCurrency> findByDomainName(String domainName);
	
	DomainCurrency findByDomainNameAndIsDefaultTrue(String domainName);
}
