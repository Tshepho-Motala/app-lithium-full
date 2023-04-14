package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.roxor.storage.entities.Source;

public interface SourceRepository extends FindOrCreateByGuidRepository<Source, Long> {
}
