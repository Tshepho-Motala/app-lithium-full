package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.incentive.storage.entities.Market;
import lithium.service.casino.provider.incentive.storage.entities.PickAnyGame;

public interface PickAnyGameRepository extends FindOrCreateByCodeRepository<PickAnyGame, Long> {
}