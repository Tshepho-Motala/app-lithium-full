package lithium.service.casino.data.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.data.entities.Currency;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends FindOrCreateByCodeRepository<Currency, Long> {
	@Cacheable(value = "lithium.service.casino.entities.Currency.byCode", unless = "#result == null")
	Currency findByCode(String currencyCode);
}