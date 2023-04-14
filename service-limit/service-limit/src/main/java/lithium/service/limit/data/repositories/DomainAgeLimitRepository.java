package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.DomainAgeLimit;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DomainAgeLimitRepository extends PagingAndSortingRepository<DomainAgeLimit, Long> {

//    @Cacheable(cacheNames="lithium.service.limit.domainagegran", key="#root.args[0] + #root.args[1] + #root.args[2]", unless="#result == null")
    List<DomainAgeLimit> findByDomainNameAndGranularityAndType(String domainName, int granularity, int type);

    DomainAgeLimit findByDomainNameAndAgeMaxAndAgeMinAndGranularityAndType(String domainName, int ageMax, int ageMin, int granularity, int type);

    List<DomainAgeLimit> findByIdIn(List<Long> id);

    default DomainAgeLimit findOne(Long id) {
        return findById(id).orElse(null);
    }

    List<DomainAgeLimit> findByDomainName(String domainName);

    @Override
//    @CacheEvict(cacheNames = "lithium.service.limit.domainagegran", key = "#result.getDomainName() + #result.getGranularity() + #result.getType()")
    <S extends DomainAgeLimit> S save(S arg0);

    @Override
//    @CacheEvict(cacheNames = "lithium.service.limit.domainagegran", key = "#root.args[0].getDomainName() + #root.args[0].getGranularity() + #root.args[0].getType()")
    void delete(DomainAgeLimit arg0);
}
