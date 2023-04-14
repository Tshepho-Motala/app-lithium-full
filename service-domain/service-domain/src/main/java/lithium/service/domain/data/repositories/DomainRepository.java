package lithium.service.domain.data.repositories;

import java.util.List;
import lithium.service.domain.data.entities.Domain;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "domains", path = "domains")
public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	Domain findByName(String name);
	List<Domain> findByParent(Domain parent);
	List<Domain> findByParentId(Long id);
  @Cacheable(value = "lithium.service.domain.data.findallplayerdomains", unless="#result == null or #result.isEmpty()")
	List<Domain> findByPlayersIsTrue();

	@Override
  @Caching(evict = {
      @CacheEvict(value = "lithium.service.domain.data.findbyname", key="#result.name"), //Gets erased here, but the object type stored is the client version of the domain object
      @CacheEvict(value = "lithium.service.domain.data.findallplayerdomains", allEntries = true),
      @CacheEvict(value = "lithium.service.domain.data.is-domain-name", allEntries = true),
      @CacheEvict(value = "lithium.service.domain.data.find-all-domains", allEntries = true)
  })
  <S extends Domain> S save(S arg0);

//	@CacheEvict(value="lithium.service.domain.data.findbyname", key="#result.name")  //Gets erased here, but the object type stored is the client version of the domain object
//	<S extends Domain> S save(S arg0);

  default Domain findOne(Long id) {
    return findById(id).orElse(null);
  }
}
