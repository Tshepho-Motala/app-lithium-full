package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.roxor.storage.entities.User;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
    User findByApiToken(String token);
}
