package lithium.service.promo.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.TimeZone;
import lithium.service.promo.client.exception.Status464NoScheduledEventForPeriodException;
import lithium.service.promo.client.objects.Granularity;
import lithium.service.promo.data.entities.Period;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision;
import lombok.extern.slf4j.Slf4j;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RecurrenceRuleService {

  @Autowired
  PeriodService periodService;

  private final ZoneId timezoneId = ZoneId.of("UTC");

  private RecurrenceRule rule(String pattern)
  throws InvalidRecurrenceRuleException
  {
    log.trace("Parsing RRULE: "+pattern);
    RecurrenceRule rule = null;
    try {
      //String recurrence = pattern.split("\n")[1];
      // String ruleStr = recurrence.split(":")[1];
      String ruleStr = pattern.split("RRULE:")[1];
      rule = new RecurrenceRule(ruleStr);
    } catch (Exception e) {
      log.error("Could not determine rule for pattern: "+pattern, e);
      throw new InvalidRecurrenceRuleException("Invalid pattern: "+pattern);
    }
    return rule;
  }

  private DateTime event(PromotionRevision promotionRevision, boolean nextEvent)
  throws InvalidRecurrenceRuleException
  {
    String rrule = promotionRevision.getRecurrencePattern();
    ZonedDateTime startTime = promotionRevision.getStartDate().atZone(timezoneId);

    DateTime instance = null;
    RecurrenceRule rule = rule(rrule);
    org.dmfs.rfc5545.DateTime start = new org.dmfs.rfc5545.DateTime(TimeZone.getDefault(), startTime.toInstant().toEpochMilli());
    log.trace("rfc5545.Start DateTime: "+start+ " ::: "+rule.toString());

    RecurrenceRuleIterator it = rule.iterator(start);
    ZonedDateTime now = ZonedDateTime.now(timezoneId);

    if (startTime.isBefore(now)) {
      if (nextEvent) {
        it.fastForward(now.toInstant().toEpochMilli());
      } else {
        it.fastForward(getPreviousEventStartDate(promotionRevision).getMillis());
      }
    }
    if (it.hasNext()) instance = new DateTime(it.nextMillis(), DateTimeZone.forTimeZone(TimeZone.getTimeZone(timezoneId)));
    log.debug("Instance ("+((nextEvent)?"Next":"Current")+") :: "+instance);
    return instance;
  }

  public Period addPeriod(Promotion promotion)
  throws InvalidRecurrenceRuleException, Status464NoScheduledEventForPeriodException
  {
    final String debugLog = "{} Event:: start: {} , end: {}";
    PromotionRevision promotionRevision = promotion.getCurrent();
    DateTime nextEventStartDate = event(promotionRevision, true);
    DateTime currentEventStartDate = event(promotionRevision, false);

    if (currentEventStartDate != null) {
      log.debug(debugLog, "Current", currentEventStartDate, getNextEventEndDate(promotionRevision, currentEventStartDate));
    }

    if (nextEventStartDate != null) {
      DateTime nextEventEndDate = getNextEventEndDate(promotionRevision, nextEventStartDate);
      log.debug(debugLog, "Next", nextEventStartDate, nextEventEndDate);
    }

    if (currentEventStartDate != null && currentEventStartDate.isBeforeNow()) {
      Period period = periodService.findOrCreatePeriod(currentEventStartDate, promotionRevision.getDomain(), Granularity.fromGranularity(promotionRevision.getEventDurationGranularity()), promotionRevision.getEventDuration());
      log.debug("Period: "+period);
      return period;
    }
    throw new Status464NoScheduledEventForPeriodException();
  }

  public void firstEvents(Promotion promotion, int numberOfEvents) {
    PromotionRevision promotionRevision = promotion.getCurrent();

    RecurrenceRule rule;
    try {
      rule = rule(promotionRevision.getRecurrencePattern());
    } catch (InvalidRecurrenceRuleException e) {
      log.error("Could not determine next events.", e);
      return;
    }

    long startTime = promotionRevision.getStartDate().atZone(timezoneId).toInstant().toEpochMilli();
    org.dmfs.rfc5545.DateTime start = new org.dmfs.rfc5545.DateTime(TimeZone.getDefault(),startTime);

    log.trace("rfc5545.DateTime: "+start);
    RecurrenceRuleIterator it = rule.iterator(start);
    Integer instances = rule.getCount();
    if (instances > numberOfEvents) instances = numberOfEvents;

    while (it.hasNext() && (--instances >= 0)) {
      DateTime nextEventStartDate = new DateTime(it.nextMillis(), DateTimeZone.forTimeZone(TimeZone.getTimeZone(timezoneId)));
      DateTime nextEventEndDate = getNextEventEndDate(promotionRevision, nextEventStartDate);
      log.debug("Promo Event ("+instances+") :: start: " + nextEventStartDate + " end: " + nextEventEndDate);
    }
  }

  public boolean promotionHasEventsBetween(Promotion promotion, LocalDate startDate, LocalDate endDate) {
    RecurrenceRule rule;
    try {
      rule = rule(promotion.getCurrent().getRecurrencePattern());

      org.dmfs.rfc5545.DateTime start = new org.dmfs.rfc5545.DateTime(TimeZone.getDefault(), promotion.getCurrent().getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

      log.trace("rfc5545.DateTime: "+start);
      RecurrenceRuleIterator it = rule.iterator(start);
      Integer instances = rule.getCount();

      while (it.hasNext() && (--instances >= 0)) {
        LocalDate nextEventStartDate = LocalDate.ofInstant(Instant.ofEpochMilli(it.nextMillis()), timezoneId);

        if (!nextEventStartDate.isBefore(startDate) && !nextEventStartDate.isAfter(endDate)) {
          return true; //Found first, now exit to avoid unnecessary looping
        }
      }
    } catch (Exception e) {
      log.error("Could not determine next events.", e);
    }
    return false;
  }

  public boolean promotionHasEventsForDate(Promotion promotion, LocalDateTime date)  {
    try {
      LocalDateTime eventDate = null;
      PromotionRevision promotionRevision = promotion.getCurrent();
      ZonedDateTime startTime = promotionRevision.getStartDate().atZone(timezoneId);
      RecurrenceRule rule = rule(promotion.getCurrent().getRecurrencePattern());
      org.dmfs.rfc5545.DateTime start = new org.dmfs.rfc5545.DateTime(TimeZone.getTimeZone(timezoneId), startTime.toInstant().toEpochMilli());

      RecurrenceRuleIterator it = rule.iterator(start);
      ZonedDateTime now = ZonedDateTime.now(timezoneId);
      ZonedDateTime firstEventEnd = getNextEventEndDate(promotionRevision, startTime.toInstant().toEpochMilli());
      int eventDuration = Optional.ofNullable(promotionRevision.getEventDuration()).orElse(1);

      //Check if the event spans across days
      if (eventDuration > 1 && !now.isBefore(startTime) && !now.isAfter(firstEventEnd)) {
        return true;
      }

      if (startTime.isBefore(now)) {
        ZonedDateTime startOfEventToday = now.with(LocalTime.of(startTime.getHour(), startTime.getMinute()));
        it.fastForward(startOfEventToday.toInstant().toEpochMilli());
      }

      if (it.hasNext()) {
        eventDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.nextMillis()), timezoneId);
      };

      return eventDate != null && !eventDate.isAfter(date);
    } catch (InvalidRecurrenceRuleException e) {
      log.error("Failed to determine promo events for {}", date, e);
    }

    return false;
  }

  private DateTime getPreviousEventStartDate(PromotionRevision promotionRevision) {
    DateTime previousEventStartDate = null;
    DateTime now = DateTime.now();
    switch (Granularity.fromGranularity(promotionRevision.getEventDurationGranularity())) {
      case GRANULARITY_DAY -> previousEventStartDate = now.minusDays(promotionRevision.getEventDuration());
      case GRANULARITY_WEEK -> previousEventStartDate = now.minusWeeks(promotionRevision.getEventDuration());
      case GRANULARITY_MONTH -> previousEventStartDate = now.minusMonths(promotionRevision.getEventDuration());
      case GRANULARITY_YEAR -> previousEventStartDate = now.minusYears(promotionRevision.getEventDuration());
      default -> log.error("Granularity not implemented yet.");
    }
    return previousEventStartDate;
  }

  private DateTime getNextEventEndDate(PromotionRevision promotionRevision, DateTime nextEventStartDate) {
    DateTime nextEventEndDate = null;
    switch (Granularity.fromGranularity(promotionRevision.getEventDurationGranularity())) {
      case GRANULARITY_DAY -> nextEventEndDate = nextEventStartDate.plusDays(promotionRevision.getEventDuration());
      case GRANULARITY_WEEK -> nextEventEndDate = nextEventStartDate.plusWeeks(promotionRevision.getEventDuration());
      case GRANULARITY_MONTH -> nextEventEndDate = nextEventStartDate.plusMonths(promotionRevision.getEventDuration());
      case GRANULARITY_YEAR -> nextEventEndDate = nextEventStartDate.plusYears(promotionRevision.getEventDuration());
      default -> log.error("Granularity not implemented yet.");
    }
    return nextEventEndDate;
  }

  private ZonedDateTime getNextEventEndDate(PromotionRevision promotionRevision, long milliseconds) {
    long nextMillis = getNextEventEndDate(promotionRevision, new DateTime(milliseconds, DateTimeZone.forTimeZone(TimeZone.getTimeZone(timezoneId)))).getMillis();
    return Instant.ofEpochMilli(nextMillis).atZone(timezoneId);
  }
}
