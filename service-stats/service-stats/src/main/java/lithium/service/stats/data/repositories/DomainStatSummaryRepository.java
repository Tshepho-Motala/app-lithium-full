package lithium.service.stats.data.repositories;

import lithium.service.stats.data.entities.DomainStat;
import lithium.service.stats.data.entities.DomainStatSummary;
import lithium.service.stats.data.entities.Period;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainStatSummaryRepository extends PagingAndSortingRepository<DomainStatSummary, Long> {
	DomainStatSummary findByDomainStatAndPeriod(DomainStat domainStat, Period period);
}
