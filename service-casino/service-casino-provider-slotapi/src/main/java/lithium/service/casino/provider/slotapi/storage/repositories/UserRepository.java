package lithium.service.casino.provider.slotapi.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.slotapi.storage.entities.User;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
}
