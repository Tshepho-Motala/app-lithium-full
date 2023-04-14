package lithium.service.kyc.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.kyc.entities.User;

public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {

}
