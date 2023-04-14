package lithium.service.pushmsg.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.pushmsg.data.entities.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {
	User findByGuid(String guid);
	List<User> findByDomainName(String domainName);
	List<User> findByDomainNameAndGuidContains(String domainName, String guidSearch);
}