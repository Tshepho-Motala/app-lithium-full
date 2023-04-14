package lithium.service.affiliate.provider.data.repositories;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.Domain;

public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
	
	public Optional<Domain> findByName(String domainName);
	
	@CacheEvict(allEntries=true, cacheNames={
		"lithium.service.affliate.data.entities.Domain.byId",
		"lithium.service.affiliate.data.entities.Domain.byCode",
	})
	@Override
	<S extends Domain> S save(S entity);

	@Cacheable(value = "lithium.service.affliate.data.entities.Domain.byId", unless = "#result == null")
	default Domain findOne(Long id) {
		return findById(id).orElse(null);
	}

	@Cacheable(value = "lithium.service.affiliate.data.entities.Domain.byCode", unless = "#result == null")
	Domain findByMachineName(String name);
}