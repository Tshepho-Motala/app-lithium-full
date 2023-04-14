package lithium.service.user.provider.sphonic.idin.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.user.provider.sphonic.idin.storage.entities.User;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
}
