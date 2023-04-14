package lithium.service.user.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.Granularity;
import lithium.service.user.data.entities.Period;
import lithium.service.user.data.repositories.PeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeriodService {

  private final PeriodRepository periodRepository;

  public Period findOrCreatePeriod(LocalDateTime date, Domain domain, Granularity granularity) {

    lithium.service.client.objects.Granularity gr = lithium.service.client.objects.Granularity.fromType(granularity.getType());

    if (ObjectUtils.isEmpty(gr)) {
      throw new Status426InvalidParameterProvidedException("Invalid granularity :".concat(granularity.getType()));
    }

    return switch (gr) {
      case GRANULARITY_DAY -> findOrCreatePeriodPerDay(domain, date, granularity);
      case GRANULARITY_WEEK -> findOrCreatePeriodPerWeek(domain, date, granularity);
      case GRANULARITY_MONTH -> findOrCreatePeriodPerMonth(domain, date, granularity);
      default -> throw new Status426InvalidParameterProvidedException("Invalid granularity :".concat(granularity.getType()));
    };
  }

  public Period findOrCreatePeriodPerMonth(Domain domain, LocalDateTime initial, Granularity granularity) {
    LocalDateTime dateStart = initial.withDayOfMonth(1).toLocalDate().atStartOfDay();
    LocalDateTime dateEnd = LocalDateTime.of(initial.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).toLocalDate(), LocalTime.MIDNIGHT);
    return findOrCreatePeriod(dateStart.getYear(), dateStart.getMonthValue(), null,
        null, dateStart, dateEnd, domain, granularity);
  }

  public Period findOrCreatePeriodPerWeek(Domain domain, LocalDateTime initial, Granularity granularity) {
    LocalDateTime dateStart = initial.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
    LocalDateTime dateEnd = LocalDateTime.of(dateStart.plusWeeks(1).toLocalDate(), LocalTime.MIDNIGHT);
    return findOrCreatePeriod(dateStart.getYear(), null, null,
        initial.get(WeekFields.ISO.weekOfWeekBasedYear()), dateStart, dateEnd, domain, granularity);
  }

  public Period findOrCreatePeriodPerDay(Domain domain, LocalDateTime initial, Granularity granularity) {
    LocalDateTime dateStart = LocalDate.of(initial.getYear(), initial.getMonthValue(), initial.getDayOfMonth()).atStartOfDay();
    LocalDateTime dateEnd = LocalDateTime.of(dateStart.plusDays(1).toLocalDate(), LocalTime.MIDNIGHT);
    return findOrCreatePeriod(dateStart.getYear(), dateStart.getMonthValue(), dateStart.getDayOfMonth(),
        null, dateStart, dateEnd, domain, granularity);
  }

  @Cacheable(cacheNames = "lithium.service.user.services.PeriodService", key = "#year + \"_\" + #month + \"_\"  + #day + \"_\"  + #week + \"_\"+ #granularity.type + \"_\"  + #domain.name", unless = "#result == null")
  @Retryable(maxAttempts = 10)
  public Period findOrCreatePeriod(Integer year, Integer month, Integer day, Integer week, LocalDateTime dateStart, LocalDateTime dateEnd,
      Domain domain, Granularity granularity) {
    if (day == null) {
      day = -1;
    }
    if (month == null) {
      month = -1;
    }
    if (year == null) {
      year = -1;
    }
    if (week == null) {
      week = -1;
    }

    Period period = periodRepository.findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(domain, year, month, day, week, granularity);

    if (ObjectUtils.isEmpty(period)) {
      period = createPeriod(year, month, day, week, dateStart, dateEnd, domain, granularity);
    }

    return period;
  }

  private Period createPeriod(Integer year, Integer month, Integer day, Integer week, LocalDateTime dateStart, LocalDateTime dateEnd,
      Domain domain, Granularity granularity) {
    Period period = periodRepository.findByDomainAndYearAndMonthAndDayAndWeekAndGranularity(domain, year, month, day, week, granularity);

    if (!ObjectUtils.isEmpty(period)) {
      return period;
    }

    period = Period.builder()
        .granularity(granularity)
        .dateStart(dateStart)
        .dateEnd(dateEnd)
        .day(day)
        .month(month)
        .year(year)
        .week(week)
        .domain(domain)
        .build();

    log.debug("createPeriod " + period + " " + Thread.currentThread());
    periodRepository.save(period);
    log.debug("createPeriod created " + period);

    return period;
  }
}
