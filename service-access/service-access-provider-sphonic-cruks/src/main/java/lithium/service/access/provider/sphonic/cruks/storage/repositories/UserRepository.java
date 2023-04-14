package lithium.service.access.provider.sphonic.cruks.storage.repositories;

import lithium.jpa.entity.EntityWithUniqueGuid;
import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.access.provider.sphonic.cruks.storage.entities.User;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
}
