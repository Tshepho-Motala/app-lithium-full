package lithium.service.promo.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.promo.client.objects.Granularity;
import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.entities.Period;
import lithium.service.promo.data.repositories.PeriodRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PeriodService {
	// Maintain a near-cache of periods in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.
	private Map<String, Period> cache = new ConcurrentHashMap<>(100000);

	@Autowired PeriodRepository periodRepository;

	private Period findOrCreatePeriodPerLifetime(Domain domain) {
		return findOrCreatePeriod(null, null, null, null, null, null, null, domain, Granularity.GRANULARITY_TOTAL);
	}

	private Period findOrCreatePeriodPerWeek(Domain domain, int year, int week, int duration) {
		DateTime dateStart = new DateTime(year, 1, 1, 0, 0).withWeekyear(year).withWeekOfWeekyear(week).withDayOfWeek(1);
		DateTime dateEnd = dateStart.plusWeeks(duration);
		return findOrCreatePeriod(year, null, null, null, week, dateStart, dateEnd, domain, Granularity.GRANULARITY_WEEK);
	}

	private Period findOrCreatePeriodPerDay(Domain domain, int year, int month, int day, int duration) {
		DateTime dateStart = new DateTime(year, month, day, 0, 0);
		DateTime dateEnd = dateStart.plusDays(duration);
		return findOrCreatePeriod(year, month, day, null, null, dateStart, dateEnd, domain, Granularity.GRANULARITY_DAY);
	}

	private Period findOrCreatePeriodPerHour(Domain domain, int year, int month, int day, int hour, int duration) {
		DateTime dateStart = new DateTime(year, month, day, hour, 0);
		DateTime dateEnd = dateStart.plusHours(duration);
		return findOrCreatePeriod(year, month, day, hour, null, dateStart, dateEnd, domain, Granularity.GRANULARITY_HOUR);
	}

	private Period findOrCreatePeriodPerMonth(Domain domain, int year, int month, int duration) {
		DateTime dateStart = new DateTime(year, month, 1, 0, 0);
		DateTime dateEnd = dateStart.plusMonths(duration);
		return findOrCreatePeriod(year, month, null, null, null, dateStart, dateEnd, domain, Granularity.GRANULARITY_MONTH);
	}

	private Period findOrCreatePeriodPerYear(Domain domain, int year, int duration) {
		DateTime dateStart = new DateTime(year, 1, 1, 0, 0);
		DateTime dateEnd = dateStart.plusYears(duration);
		return findOrCreatePeriod(year, null, null, null, null, dateStart, dateEnd, domain, Granularity.GRANULARITY_YEAR);
	}

	@Retryable(maxAttempts=100, backoff = @Backoff(value = 10, delay = 10))
	@Transactional(value = TxType.NOT_SUPPORTED)
	public Period findOrCreatePeriod(DateTime date, Domain domain, Granularity granularity, Integer duration) {
		if (duration == null) duration = 1;
		switch (granularity) {
			case GRANULARITY_HOUR:
				return findOrCreatePeriodPerHour(domain, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), date.getHourOfDay(), duration);
			case GRANULARITY_DAY:
				return findOrCreatePeriodPerDay(domain, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), duration);
			case GRANULARITY_MONTH:
				return findOrCreatePeriodPerMonth(domain, date.getYear(), date.getMonthOfYear(), duration);
			case GRANULARITY_WEEK:
				return findOrCreatePeriodPerWeek(domain, date.getWeekyear(), date.getWeekOfWeekyear(), duration);
			case GRANULARITY_YEAR:
				return findOrCreatePeriodPerYear(domain, date.getYear(), duration);
			case GRANULARITY_TOTAL:
				return findOrCreatePeriodPerLifetime(domain);
			default: throw new RuntimeException("Invalid granularity");
		}
	}

	private Period createPeriod(Integer year, Integer month, Integer day, Integer hour, Integer week, DateTime dateStart, DateTime dateEnd, Domain domain, Granularity granularity) {
		Period period = periodRepository.findByDomainAndYearAndMonthAndDayAndHourAndWeekAndGranularity(domain, year, month, day, hour, week, granularity.granularity());
		if (period != null) return period;
		period = Period.builder()
		.granularity(granularity.granularity())
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

	private Period findOrCreatePeriod(Integer year, Integer month, Integer day, Integer hour, Integer week, DateTime dateStart, DateTime dateEnd, Domain domain, Granularity granularity) {
		if (day == null) day = -1;
		if (month == null) month = -1;
		if (year == null) year = -1;
		if (week == null) week = -1;
		if (hour == null) hour = -1;
		
		if (granularity == Granularity.GRANULARITY_TOTAL) {
			dateStart = new DateTime(1900, 1, 1, 1, 0, 0, 0);
			dateEnd = new DateTime(5000, 1, 1, 1, 0, 0, 0);
		}

		log.trace("findOrCreatePeriod year " + year + " month " + month + " day "+day+" hour "+hour+" week " + week + " dateStart " + dateStart.toString() + " dateEnd " + dateEnd.toString() + " domain " + domain.getName() + " granularity " + granularity);

		boolean cacheMiss = false;
		Period period = cacheGet(year, month, day, hour, week, domain, granularity.granularity());

		if (period == null) {
			cacheMiss = true;
			period = periodRepository.findByDomainAndYearAndMonthAndDayAndHourAndWeekAndGranularity(domain, year, month, day, hour, week, granularity.granularity());
		}

		if (period == null) {
			log.debug("findOrCreatePeriod creating period year " + year + " month " + month + " day " + day + " week "
					+ week + " dateStart " + dateStart + " dateEnd " + dateEnd + " domain "
					+ domain.getName() + " granularity " + granularity);
			period = createPeriod(year, month, day, hour, week, dateStart, dateEnd, domain, granularity);
			log.debug("findOrCreatePeriod created period year " + year + " month " + month + " day " + day + " week "
					+ week + " dateStart " + dateStart + " dateEnd " + dateEnd + " domain "
					+ domain.getName() + " granularity " + granularity);
		}

		if (cacheMiss) {
			cachePut(period);
		}

		return period;
	}

	private Period cachePut(Period period) {
		String cacheKey =
				period.getYear() + "_" +
						period.getMonth() + "_" +
						period.getDay() + "_" +
						period.getHour() + "_" +
						period.getWeek() + "_" +
						period.getDomain().getName() + "_" +
						period.getGranularity();
		cache.put(cacheKey, period);
		return period;
	}

	private Period cacheGet(Integer year, Integer month, Integer day, Integer hour, Integer week, Domain domain, int granularity) {
		String cacheKey =
				year + "_" +
						month + "_" +
						day + "_" +
						hour + "_" +
						week + "_" +
						domain.getName() + "_" +
						granularity;
		return cache.get(cacheKey);
	}
}
