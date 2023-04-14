package lithium.service.pushmsg.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.pushmsg.data.entities.ExternalUser;
import lithium.service.pushmsg.data.entities.User;

public interface ExternalUserRepository extends PagingAndSortingRepository<ExternalUser, Long>, JpaSpecificationExecutor<ExternalUser> {
	List<ExternalUser> findByUserGuid(String guid);
	List<ExternalUser> findByUser(User user);
	ExternalUser findByUuid(String uuid);
	ExternalUser findByUserGuidAndUuid(String guid, String uuid);
	ExternalUser findByUserAndUuid(User user, String uuid);
}