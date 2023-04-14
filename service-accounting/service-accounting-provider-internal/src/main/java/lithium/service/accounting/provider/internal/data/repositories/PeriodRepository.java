package lithium.service.accounting.provider.internal.data.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Period;

public interface PeriodRepository extends PagingAndSortingRepository<Period, Long> {

	Period findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(Domain domain, Integer year, Integer month, Integer day, Integer week, int granularity);
	
	Period findFirstByDomainAndDateStartBeforeAndGranularityOrderByDateStartDesc(Domain domain, Date date, int granularity);
	
	List<Period> findByOpenAndGranularityOrderByDateStart(boolean open, int granularity);

	Period findTop1ByDomainNameAndGranularity(String domainName, int granularity);


	default Period findOne(Long id) {
		return findById(id).orElse(null);
	}
}
