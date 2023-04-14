package lithium.service.casino.provider.incentive.storage.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.provider.incentive.storage.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
}
