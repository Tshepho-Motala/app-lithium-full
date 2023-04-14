package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.incentive.storage.entities.IncentiveUser;

public interface IncentiveUserRepository extends FindOrCreateByGuidRepository<IncentiveUser, Long> {
}
