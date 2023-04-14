package lithium.service.stats.data.repositories;

import java.util.Date;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.stats.data.entities.Domain;
import lithium.service.stats.data.entities.Period;

public interface PeriodRepository extends PagingAndSortingRepository<Period, Long> {

	@CacheEvict("lithium.service.stats.provider.internal.data.entities.Period.byId")
	@Override
	<S extends Period> S save(S entity);

	@Cacheable(value = "lithium.service.stats.provider.internal.data.entities.Period.byId", unless = "#result == null")
	default Period findOne(Long id) {
		return findById(id).orElse(null);
	}

	Period findByDomainAndYearAndMonthAndDayAndHourAndWeekAndGranularity(Domain domain, Integer year, Integer month, Integer day, Integer hour, Integer week, int granularity);
	
	Period findFirstByDomainAndDateStartBeforeAndGranularityOrderByDateStartDesc(Domain domain, Date date, int granularity);
}
