package lithium.service.reward.data.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.reward.data.entities.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {

}