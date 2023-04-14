package lithium.service.user.data.repositories;

import java.util.ArrayList;
import lithium.jpa.repository.FindOrCreateByCodeRepository;
import lithium.service.user.data.entities.UserLinkType;

public interface UserLinkTypeRepository extends FindOrCreateByCodeRepository<UserLinkType, Long> {

  ArrayList<UserLinkType> findByEnabledTrueAndDeletedFalse();
}
