package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.incentive.storage.entities.Market;

public interface MarketRepository extends FindOrCreateByCodeRepository<Market, Long> {
}