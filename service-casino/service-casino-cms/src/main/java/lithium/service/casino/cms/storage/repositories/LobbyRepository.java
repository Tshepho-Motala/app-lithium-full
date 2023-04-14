package lithium.service.casino.cms.storage.repositories;

import lithium.service.casino.cms.storage.entities.Lobby;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LobbyRepository extends PagingAndSortingRepository<Lobby, Long>, JpaSpecificationExecutor<Lobby> {
	Page<Lobby> findByDomainName(String domainName, Pageable pageable);

	@Cacheable(cacheNames="lithium.service.casino.cms.storage.entities.lobby", key="#root.args[0]", unless="#result == null")
	Lobby findTop1ByDomainName(String domainName);

	@Override
	@CacheEvict(cacheNames="lithium.service.casino.cms.storage.entities.lobby", key="#result.domain.name")
	<S extends Lobby> S save(S arg0);
}
