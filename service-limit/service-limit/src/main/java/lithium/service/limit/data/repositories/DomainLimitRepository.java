package lithium.service.limit.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import lithium.service.limit.data.entities.DomainLimit;

public interface DomainLimitRepository extends PagingAndSortingRepository<DomainLimit, Long> {
	
//	@Cacheable(cacheNames="lithium.service.limit.domaingran", key="#root.args[0] + #root.args[1] + #root.args[2]", unless="#result == null")
	DomainLimit findByDomainNameAndGranularityAndType(String domainName, int granularity, int type);
	
	@Modifying
	@Transactional
//	@CacheEvict(cacheNames="lithium.service.limit.domaingran", key="#root.args[0] + #root.args[1] + #root.args[2]")
	void deleteByDomainNameAndGranularityAndType(String domainName, int granularity, int type);
	
	List<DomainLimit> findByDomainName(String domainName);
	
	@Override
//	@CacheEvict(cacheNames="lithium.service.limit.domaingran", key="#result.getDomainName() + #result.getGranularity() + #result.getType()")
	<S extends DomainLimit> S save(S arg0);
	
	@Override
//	@CacheEvict(cacheNames="lithium.service.limit.domaingran", key="#root.args[0].getDomainName() + #root.args[0].getGranularity() + #root.args[0].getType()")
	void delete(DomainLimit arg0);
}
