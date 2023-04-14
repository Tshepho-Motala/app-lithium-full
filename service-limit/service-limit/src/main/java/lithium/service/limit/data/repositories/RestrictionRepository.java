package lithium.service.limit.data.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.limit.data.entities.Restriction;

public interface RestrictionRepository extends FindOrCreateByCodeRepository<Restriction, Long> {
}
