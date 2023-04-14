package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.roxor.storage.entities.Currency;
import org.springframework.cache.annotation.Cacheable;

public interface CurrencyRepository extends FindOrCreateByCodeRepository<Currency, Long> {

    @Cacheable(value = "lithium.service.casino.provider.roxor.storage.entities.Currency.byCode", unless = "#result == null")
    Currency findByCode(String currencyCode);

}
