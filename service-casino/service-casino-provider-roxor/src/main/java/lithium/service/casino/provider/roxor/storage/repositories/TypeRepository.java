package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.casino.provider.roxor.storage.entities.Type;

public interface TypeRepository extends FindOrCreateByCodeRepository<Type, Long> {
}
