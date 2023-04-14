package lithium.service.access.provider.sphonic.kyc.storage.repositories;

import lithium.service.access.provider.sphonic.data.entities.Authentication;
import lithium.service.access.provider.sphonic.data.repositories.SphonicAuthenticationRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface AuthenticationRepository extends SphonicAuthenticationRepository<Authentication, Long> {
	@CacheEvict(cacheNames = "lithium.service.access.provider.sphonic.kyc.storage.entities.Authentication",
			key = "#root.args[0].getDomain().getName()")
	@Override
	<S extends Authentication> S save(S entity);

	@Cacheable(cacheNames="lithium.service.access.provider.sphonic.kyc.storage.entities.Authentication",
			key="#root.args[0]", unless="#result == null")
	public Authentication findByDomainName(String domainName);

	@Transactional
	@Modifying
	@CacheEvict(cacheNames = "lithium.service.access.provider.sphonic.kyc.storage.entities.Authentication",
			key = "#root.args[0]")
	public void deleteByDomainName(String domainName);
}
