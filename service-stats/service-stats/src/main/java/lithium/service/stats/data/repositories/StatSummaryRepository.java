package lithium.service.stats.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.stats.data.entities.Period;
import lithium.service.stats.data.entities.Stat;
import lithium.service.stats.data.entities.StatSummary;

public interface StatSummaryRepository extends PagingAndSortingRepository<StatSummary, Long> {
	StatSummary findByPeriodAndStat(Period period, Stat stat);
	
	List<StatSummary> findTop7ByStatAndPeriodGranularity(Stat stat, int granularity);
}
