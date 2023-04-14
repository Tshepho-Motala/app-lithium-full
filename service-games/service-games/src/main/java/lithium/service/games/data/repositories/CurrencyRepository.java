package lithium.service.games.data.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.games.data.entities.progressivejackpotfeeds.Currency;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CurrencyRepository extends FindOrCreateByCodeRepository<Currency, Long>, JpaSpecificationExecutor<Currency> {
}
