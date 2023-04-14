package lithium.service.access.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.access.data.entities.List;

public interface ListRepository extends PagingAndSortingRepository<List, Long>, JpaSpecificationExecutor<List> {
	List findByDomainNameAndName(String domainName, String name);
	java.util.List<List> findByDomainName(String domainName);
	java.util.List<List> findByDomainNameAndListTypeNameAndEnabled(String domainName, String listTypeName, Boolean enabled);
  default List findOne(Long id) {
    return findById(id).orElse(null);
  }
}
