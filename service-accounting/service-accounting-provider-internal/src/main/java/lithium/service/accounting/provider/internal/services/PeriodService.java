package lithium.service.accounting.provider.internal.services;

import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.accounting.provider.internal.data.entities.Domain;
import lithium.service.accounting.provider.internal.data.entities.Period;
import lithium.service.accounting.provider.internal.data.repositories.PeriodRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PeriodService {
	
	// Maintain a near-cache of periods in order to prevent TCP hops to hazelcast and database.
	// There are ways to do this with spring cache abstraction but I need way more time to learn that right
	// now...
	//TODO implement via spring cache near cache abstraction.  
	ConcurrentHashMap<String, Period> cache = new ConcurrentHashMap<>(100000);

	@Autowired PeriodRepository periodRepository;

	private Period findOrCreatePeriodPerLifetime(Domain domain) {
		return findOrCreatePeriod(null, null, null, null, null, null, domain, Period.GRANULARITY_TOTAL);
	}

	private Period findOrCreatePeriodPerWeek(Domain domain, int year, int week) {
		DateTime dateStart = new DateTime(year, 1, 1, 0, 0).withWeekyear(year).withWeekOfWeekyear(week).withDayOfWeek(1);
		DateTime dateEnd = dateStart.plusWeeks(1);
		return findOrCreatePeriod(year, null, null, week, dateStart, dateEnd, domain, Period.GRANULARITY_WEEK);
	}

	private Period findOrCreatePeriodPerDay(Domain domain, int year, int month, int day) {
		DateTime dateStart = new DateTime(year, month, day, 0, 0);
		DateTime dateEnd = dateStart.plusDays(1);
		return findOrCreatePeriod(year, month, day, null, dateStart, dateEnd, domain, Period.GRANULARITY_DAY);
	}

	private Period findOrCreatePeriodPerMonth(Domain domain, int year, int month) {
		DateTime dateStart = new DateTime(year, month, 1, 0, 0);
		DateTime dateEnd = dateStart.plusMonths(1);
		return findOrCreatePeriod(year, month, null, null, dateStart, dateEnd, domain, Period.GRANULARITY_MONTH);
	}

	private Period findOrCreatePeriodPerYear(Domain domain, int year) {
		DateTime dateStart = new DateTime(year, 1, 1, 0, 0);
		DateTime dateEnd = dateStart.plusYears(1);
		return findOrCreatePeriod(year, null, null, null, dateStart, dateEnd, domain, Period.GRANULARITY_YEAR);
	}

	@Retryable(maxAttempts=100, backoff = @Backoff(value = 10, delay = 10))
	@Transactional(value = TxType.NOT_SUPPORTED)
	public Period findOrCreatePeriod(DateTime date, Domain domain, int granularity) {
		switch (granularity) {
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

	@Retryable(maxAttempts=100, backoff = @Backoff(value = 10, delay = 10))
	@Transactional(value = TxType.NOT_SUPPORTED)
	public Period findOrCreatePeriodByOffset(int offset, Domain domain, int granularity) {
		DateTime date = new DateTime();
		
		switch (granularity) {
		case Period.GRANULARITY_YEAR:
			date = date.minusYears(offset); break;
		case Period.GRANULARITY_MONTH:
			date = date.minusMonths(offset); break;
		case Period.GRANULARITY_DAY:
			date = date.minusDays(offset); break;
		case Period.GRANULARITY_WEEK:
			date = date.minusWeeks(offset); break;
		case Period.GRANULARITY_TOTAL: 
			return findOrCreatePeriodPerLifetime(domain);
		default: throw new RuntimeException("Invalid granularity");
		}
		
		log.debug("findOrCreatePeriodByOffset " + date + " domain " + domain + " granularity " + granularity + " offset " + offset);
		
		return findOrCreatePeriod(date, domain, granularity);
	}
	
	@Retryable(maxAttempts=100, backoff = @Backoff(value = 10, delay = 10))
	@Transactional(value = TxType.NOT_SUPPORTED)
	public Period createPeriod(Integer year, Integer month, Integer day, Integer week, DateTime dateStart, DateTime dateEnd, Domain domain, int granularity) {
		Period period = periodRepository.findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(domain, year, month, day, week, granularity);
		if (period != null) return period;
		period = Period.builder()
				.granularity(granularity)
				.dateStart(dateStart.toDate())
				.dateEnd(dateEnd.toDate())
				.day(day)
				.month(month)
				.year(year)
				.week(week)
				.domain(domain)
				.open(true)
				.build();

		log.info("createPeriod " + period + " " + Thread.currentThread().toString());
		period = periodRepository.save(period);
		log.info("createPeriod created " + period);
		return period;
	}

	@Retryable(maxAttempts=100, backoff = @Backoff(value = 10, delay = 10))
	@Transactional(value = TxType.NOT_SUPPORTED)
	public Period findOrCreatePeriod(Integer year, Integer month, Integer day, Integer week, DateTime dateStart, DateTime dateEnd, Domain domain, int granularity) {
		
		if (day == null) day = -1;
		if (month == null) month = -1;
		if (year == null) year = -1;
		if (week == null) week = -1;
		
		if (granularity == 5) {
			dateStart = new DateTime(1900, 1, 1, 1, 0, 0, 0);
			dateEnd = new DateTime(5000, 1, 1, 1, 0, 0, 0);
		}
		
		log.debug("findOrCreatePeriod year " + year + " month " + month + " day " + day + " week " + week + " dateStart " + dateStart.toString() + " dateEnd " + dateEnd.toString() + " domain " + domain.getName() + " granularity " + granularity);
		
		boolean cacheMiss = false;
		Period period = cacheGet(year, month, day, week, dateStart, dateEnd, domain, granularity);
		
		if (period == null) {
			cacheMiss = true;
			period = periodRepository.findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(domain, year, month, day, week, granularity);
		}
		
		if (period == null) {
			log.info("findOrCreatePeriod creating period year " + year + " month " + month + " day " + day + " week " + week + " dateStart " + dateStart.toString() + " dateEnd " + dateEnd.toString() + " domain " + domain.getName() + " granularity " + granularity);
			period = createPeriod(year, month, day, week, dateStart, dateEnd, domain, granularity);
			log.info("findOrCreatePeriod created period year " + year + " month " + month + " day " + day + " week " + week + " dateStart " + dateStart.toString() + " dateEnd " + dateEnd.toString() + " domain " + domain.getName() + " granularity " + granularity);
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
			period.getWeek() + "_" + 
			period.getDomain().getName() + "_" +
			period.getGranularity();
		cache.put(cacheKey, period);
		return period;
	}
	
	private Period cacheGet(Integer year, Integer month, Integer day, Integer week, DateTime dateStart, DateTime dateEnd, Domain domain, int granularity) {
		String cacheKey = 
				year + "_" + 
				month + "_" + 
				day + "_" + 
				week + "_" + 
				domain.getName() + "_" +
				granularity;
		return cache.get(cacheKey);
	}

//	public List<Period> findPeriodsOldestFirst(boolean open, int granularity) {
//		return periodRepository.findByOpenAndGranularityOrderByDateStart(open, granularity);
//	}
//
//	public void closePeriod(Period period) {
//		
//		if (!period.isOpen()) throw new RuntimeException("This period is already closed. " + period.toString());
//		
//		Period previousPeriod = periodRepository.findFirstByDomainAndDateBeforeOrderByDateDesc(period.getDomain(), period.getDate());
//		if (previousPeriod != null) {
//			if (previousPeriod.isOpen()) throw new RuntimeException("The previous period is still open. " + previousPeriod.toString());
//		}
//		
//		List<PeriodAccount> periodsPerDayPerAccount = periodPerDayPerAccountRepository.findByPeriod(period);
//		for (PeriodAccount periodPerDayPerAccount: periodsPerDayPerAccount) {
//			if (periodPerDayPerAccount.isOpen()) {
//				PeriodAccount previousPeriodPerDayPerAccount = periodPerDayPerAccountRepository
//						.findByPeriodAndAccount(previousPeriod, periodPerDayPerAccount.getAccount());
//				if (previousPeriodPerDayPerAccount != null) {
//					if (previousPeriodPerDayPerAccount.isOpen()) throw new RuntimeException("The previous PeriodPerDayPerAccount is still open. " + previousPeriodPerDayPerAccount.toString());
//					periodPerDayPerAccount.setOpeningBalanceCents(previousPeriodPerDayPerAccount.getClosingBalanceCents());
//				} else {
//					periodPerDayPerAccount.setOpeningBalanceCents(0L);
//				}
//				
//				periodPerDayPerAccount.setClosingBalanceCents(
//						periodPerDayPerAccount.getOpeningBalanceCents() + 
//						periodPerDayPerAccount.getDebitCents() -
//						periodPerDayPerAccount.getCreditCents());
//				
//				periodPerDayPerAccount.setOpen(false);
//				periodPerDayPerAccountRepository.save(periodPerDayPerAccount);
//			}
//		}
//		
//		
//	}
}
