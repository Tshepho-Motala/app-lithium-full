package lithium.service.accounting.domain.v2.storage.repositories;

import lithium.service.accounting.domain.v2.storage.entities.Domain;
import lithium.service.accounting.domain.v2.storage.entities.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface PeriodRepository extends JpaRepository<Period, Long> {
	Period findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(Domain domain, Integer year, Integer month,
			Integer day, Integer week, int granularity);

	@Query("select o from #{#entityName} o where o.id in (:ids)")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Period> findForUpdate(@Param("ids") List<Long> ids);
}
