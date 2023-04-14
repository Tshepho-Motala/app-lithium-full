package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.Granularity;
import lithium.service.user.data.entities.Period;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PeriodRepository extends PagingAndSortingRepository<Period, Long>, JpaSpecificationExecutor<Period> {

  Period findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(Domain domain, Integer year, Integer month,
      Integer day, Integer week, Granularity granularity);
}
