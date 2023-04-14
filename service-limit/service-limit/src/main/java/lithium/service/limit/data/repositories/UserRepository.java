package lithium.service.limit.data.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.limit.data.entities.User;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
}
