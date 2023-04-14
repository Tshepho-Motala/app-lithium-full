package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.incentive.storage.entities.Competition;
import lithium.service.casino.provider.incentive.storage.entities.Sport;

public interface CompetitionRepository extends FindOrCreateByCodeRepository<Competition, Long> {
}