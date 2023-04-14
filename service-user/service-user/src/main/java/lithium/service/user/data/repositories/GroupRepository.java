package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.Group;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "groups", path = "groups")
public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {
	Group findByName(String name);
	Iterable<Group> findByDomainId(Long domainId);
	Iterable<Group> findByDomainNameAndDeletedFalse(String domainName);
	Group findByNameAndDomainName(String name, String domainName);
  default Group findOne(Long id) {
    return findById(id).orElse(null);
  }
}
