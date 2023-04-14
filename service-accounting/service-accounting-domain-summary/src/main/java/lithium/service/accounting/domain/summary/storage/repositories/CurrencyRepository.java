package lithium.service.accounting.domain.summary.storage.repositories;

import lithium.service.accounting.domain.summary.storage.entities.Currency;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
	@CacheEvict(allEntries = true, cacheNames = {
		"lithium.service.accounting.domain.summary.storage.entities.Currency.byId",
		"lithium.service.accounting.domain.summary.storage.entities.Currency.byCode",
	})
	@Override
	<S extends Currency> S save(S entity);

	@Cacheable(value = "lithium.service.accounting.domain.summary.storage.entities.Currency.byId",
			unless = "#result == null")
	default Currency findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Cacheable(value = "lithium.service.accounting.domain.summary.storage.entities.Currency.byCode",
			unless = "#result == null")
	Currency findByCode(String code);
}
