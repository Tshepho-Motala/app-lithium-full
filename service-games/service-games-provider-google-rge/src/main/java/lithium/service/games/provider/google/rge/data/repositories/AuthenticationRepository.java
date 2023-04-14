package lithium.service.games.provider.google.rge.data.repositories;

import lithium.service.games.provider.google.rge.data.entities.Authentication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

public interface AuthenticationRepository extends PagingAndSortingRepository<Authentication, Long> {
    @CacheEvict(cacheNames = "lithium.service.games.provider.google.rge.data.entities.Authentication",
            key = "#root.args[0].getDomain().getName()")
    @Override
    <S extends Authentication> S save(S entity);

    @Cacheable(cacheNames="lithium.service.games.provider.google.rge.data.entities.Authentication",
            key="#root.args[0]", unless="#result == null")
    public Authentication findByDomainName(String domainName);

    @Transactional
    @Modifying
    @CacheEvict(cacheNames = "lithium.service.games.provider.google.rge.data.entities.Authentication",
            key = "#root.args[0]")
    public void deleteByDomainName(String domainName);
}
