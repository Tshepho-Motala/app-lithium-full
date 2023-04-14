package lithium.service.stats.services;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.stats.data.entities.Domain;
import lithium.service.stats.data.entities.Period;
import lithium.service.stats.data.repositories.PeriodRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PeriodService {

	@Autowired PeriodRepository periodRepository;

	public Period findOrCreatePeriodPerLifetime(Domain domain) {
		return findOrCreatePeriod(null, null, null, null, null, null, null, domain, Period.GRANULARITY_TOTAL);
	}

	public Period findOrCreatePeriodPerWeek(Domain domain, int year, int week) {
		DateTime dateStart = new DateTime(year, 1, 1, 0, 0).withWeekyear(year).withWeekOfWeekyear(week).withDayOfWeek(1);
		DateTime dateEnd = dateStart.plusWeeks(1);
		return findOrCreatePeriod(year, null, null, null, week, dateStart, dateEnd, domain, Period.GRANULARITY_WEEK);
	}

	public Period findOrCreatePeriodPerDay(Domain domain, int year, int month, int day) {
		DateTime dateStart = new DateTime(year, month, day, 0, 0);
		DateTime dateEnd = dateStart.plusDays(1);
		return findOrCreatePeriod(year, month, day, null, null, dateStart, dateEnd, domain, Period.GRANULARITY_DAY);
	}
	
	public Period findOrCreatePeriodPerHour(Domain domain, int year, int month, int day, int hour) {
		DateTime dateStart = new DateTime(year, month, day, hour, 0);
		DateTime dateEnd = dateStart.plusHours(1);
		return findOrCreatePeriod(year, month, day, hour, null, dateStart, dateEnd, domain, Period.GRANULARITY_HOUR);
	}

	public Period findOrCreatePeriodPerMonth(Domain domain, int year, int month) {
		DateTime dateStart = new DateTime(year, month, 1, 0, 0);
		DateTime dateEnd = dateStart.plusMonths(1);
		return findOrCreatePeriod(year, month, null, null, null, dateStart, dateEnd, domain, Period.GRANULARITY_MONTH);
	}

	public Period findOrCreatePeriodPerYear(Domain domain, int year) {
		DateTime dateStart = new DateTime(year, 1, 1, 0, 0);
		DateTime dateEnd = dateStart.plusYears(1);
		return findOrCreatePeriod(year, null, null, null, null, dateStart, dateEnd, domain, Period.GRANULARITY_YEAR);
	}

	public Period findOrCreatePeriod(DateTime date, Domain domain, int granularity) {
		switch (granularity) {
			case Period.GRANULARITY_HOUR:
				return findOrCreatePeriodPerHour(domain, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), date.getHourOfDay());
			case Period.GRANULARITY_DAY:
				return findOrCreatePeriodPerDay(domain, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
			case Period.GRANULARITY_MONTH:
				return findOrCreatePeriodPerMonth(domain, date.getYear(), date.getMonthOfYear());
			case Period.GRANULARITY_WEEK:
				return findOrCreatePeriodPerWeek(domain, date.getWeekyear(), date.getWeekOfWeekyear());
			case Period.GRANULARITY_YEAR:
				return findOrCreatePeriodPerYear(domain, date.getYear());
			case Period.GRANULARITY_TOTAL:
				return findOrCreatePeriodPerLifetime(domain);
			default: throw new RuntimeException("Invalid granularity");
		}
	}
	
	public Period findOrCreatePeriodByOffset(int offset, Domain domain, int granularity) {
		DateTime date = new DateTime();
		
		switch (granularity) {
			case Period.GRANULARITY_YEAR:
				date = date.minusYears(offset); break;
			case Period.GRANULARITY_MONTH:
				date = date.minusMonths(offset); break;
			case Period.GRANULARITY_DAY:
				date = date.minusDays(offset); break;
			case Period.GRANULARITY_HOUR:
				date = date.minusHours(offset); break;
			case Period.GRANULARITY_WEEK:
				date = date.minusWeeks(offset); break;
			case Period.GRANULARITY_TOTAL: 
				return findOrCreatePeriodPerLifetime(domain);
			default: throw new RuntimeException("Invalid granularity");
		}
		
		log.debug("findOrCreatePeriodByOffset " + date + " domain " + domain + " granularity " + granularity + " offset " + offset);
		
		return findOrCreatePeriod(date, domain, granularity);
	}
	
	private Period createPeriod(Integer year, Integer month, Integer day, Integer hour, Integer week, DateTime dateStart, DateTime dateEnd, Domain domain, int granularity) {
		Period period = periodRepository.findByDomainAndYearAndMonthAndDayAndHourAndWeekAndGranularity(domain, year, month, day, hour, week, granularity);
		if (period != null) return period;
		period = Period.builder()
				.granularity(granularity)
				.dateStart(dateStart.toDate())
				.dateEnd(dateEnd.toDate())
				.day(day)
				.hour(hour)
				.month(month)
				.year(year)
				.week(week)
				.domain(domain)
				.build();

		log.debug("createPeriod " + period + " " + Thread.currentThread().toString());
		periodRepository.save(period);
		log.debug("createPeriod created " + period);
		return period;
	}
		
	@Cacheable(cacheNames="lithium.service.stats.services.PeriodService", key="#year #month #day #hour #week #domain.name", unless = "#result == null")
	@Retryable(maxAttempts=10)
	private Period findOrCreatePeriod(Integer year, Integer month, Integer day, Integer hour, Integer week, DateTime dateStart, DateTime dateEnd, Domain domain, int granularity) {
		
		if (day == null) day = -1;
		if (month == null) month = -1;
		if (year == null) year = -1;
		if (week == null) week = -1;
		if (hour == null) hour = -1;
		
		if (granularity == 5) {
			dateStart = new DateTime(1900, 1, 1, 1, 0, 0, 0);
			dateEnd = new DateTime(5000, 1, 1, 1, 0, 0, 0);
		}
		
		log.debug("findOrCreatePeriod year " + year + " month " + month + " day "+day+" hour "+hour+" week " + week + " dateStart " + dateStart.toString() + " dateEnd " + dateEnd.toString() + " domain " + domain.getName() + " granularity " + granularity);
		
		Period period = periodRepository.findByDomainAndYearAndMonthAndDayAndHourAndWeekAndGranularity(domain, year, month, day, hour, week, granularity);
		if (period == null) {
			period = createPeriod(year, month, day, hour, week, dateStart, dateEnd, domain, granularity);
		}
		return period;
	}
}
