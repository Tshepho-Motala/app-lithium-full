package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.roxor.storage.entities.Platform;

public interface PlatformRepository extends FindOrCreateByCodeRepository<Platform, Long> {
}
