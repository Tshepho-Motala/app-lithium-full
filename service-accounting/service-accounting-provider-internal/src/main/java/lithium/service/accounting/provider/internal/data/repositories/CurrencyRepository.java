package lithium.service.accounting.provider.internal.data.repositories;

import lithium.service.accounting.provider.internal.data.entities.Domain;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.accounting.provider.internal.data.entities.Currency;

public interface CurrencyRepository extends PagingAndSortingRepository<Currency, Long> {

	@CacheEvict(allEntries = true, cacheNames = {
			"lithium.service.accounting.provider.internal.data.entities.Currency.byId",	
			"lithium.service.accounting.provider.internal.data.entities.Currency.byCode",	
	})
	@Override
	<S extends Currency> S save(S entity);

	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.Currency.byId", unless = "#result == null")
	default Currency findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Cacheable(value = "lithium.service.accounting.provider.internal.data.entities.Currency.byCode", unless = "#result == null")
	Currency findByCode(String code);
	
	Iterable<Currency> findByCodeStartingWithOrderByCode(String code);
}
