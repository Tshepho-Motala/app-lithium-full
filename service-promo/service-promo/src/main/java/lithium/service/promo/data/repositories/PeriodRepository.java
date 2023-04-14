package lithium.service.promo.data.repositories;

import java.util.Date;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.entities.Period;

import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository extends PagingAndSortingRepository<Period, Long> {


	@CacheEvict(value = "lithium.service.missions.provider.internal.data.entities.Period.byId")
	@Override
	<S extends Period> S save(S entity);

	@Cacheable(value = "lithium.service.missions.provider.internal.data.entities.Period.byId", unless = "#result == null")
	default Period findOne(Long id) {
		return findById(id).orElse(null);
	}
	Period findByDomainAndYearAndMonthAndDayAndHourAndWeekAndGranularity(Domain domain, Integer year, Integer month, Integer day, Integer hour, Integer week, Integer granularity);
}
