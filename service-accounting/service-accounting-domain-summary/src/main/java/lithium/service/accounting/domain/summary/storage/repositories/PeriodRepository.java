package lithium.service.accounting.domain.summary.storage.repositories;

import lithium.service.accounting.domain.summary.storage.entities.Domain;
import lithium.service.accounting.domain.summary.storage.entities.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface PeriodRepository extends JpaRepository<Period, Long> {
	Period findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(Domain domain, Integer year, Integer month,
			Integer day, Integer week, int granularity);

	@Query("select o from #{#entityName} o where o.id = :id")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Period findForUpdate(@Param("id") Long id);
}
