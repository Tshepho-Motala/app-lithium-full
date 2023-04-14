package lithium.service.casino.cms.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.cms.storage.entities.User;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
}
