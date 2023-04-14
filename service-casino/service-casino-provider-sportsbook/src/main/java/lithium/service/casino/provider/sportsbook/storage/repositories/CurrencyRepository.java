package lithium.service.casino.provider.sportsbook.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import org.springframework.cache.annotation.Cacheable;

public interface CurrencyRepository extends FindOrCreateByCodeRepository<Currency, Long> {

	Currency findByCode(String currencyCode);

}