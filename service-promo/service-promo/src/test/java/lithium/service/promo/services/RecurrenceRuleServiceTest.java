package lithium.service.promo.services;

import lithium.service.promo.client.exception.Status464NoScheduledEventForPeriodException;
import lithium.service.promo.client.objects.Granularity;
import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@SpringBootTest
class RecurrenceRuleServiceTest {

  @Autowired
  RecurrenceRuleService recurrenceRuleService;

  @Autowired
  DomainService domainService;

  Promotion promotion;

  @BeforeEach
  void setUp() {
//    String domainName = "livescore_uk";
//    when(domainService.findOrCreate(domainName)).thenReturn(Domain.builder().name(domainName).build());
    Domain domain = domainService.findOrCreate("livescore_uk");

    promotion = Promotion.builder()
        .current(PromotionRevision.builder()
            .domain(domain)
            .recurrencePattern("DTSTART:20220801T000000Z\nRRULE:FREQ=DAILY;INTERVAL=3;COUNT=15") //every 3 days for 5 times
            .startDate(LocalDateTime.of(LocalDate.of(2022, 8, 1), LocalTime.MIN))
            .eventDuration(2)
            .eventDurationGranularity(Granularity.GRANULARITY_DAY.granularity())
            .redeemableInEvent(2)
            .redeemableInTotal(5)
            .build())
        .build();
  }

//  @Test
  @DisplayName( "Testing of period creation." )
  public void addPeriod()
  throws InvalidRecurrenceRuleException, Status464NoScheduledEventForPeriodException
  {
    recurrenceRuleService.addPeriod(promotion);
  }

//  @Test
  @DisplayName( "Testing of all scheduled events view." )
  public void allEventInstances() {
    recurrenceRuleService.firstEvents(promotion, 20);
  }


  @DisplayName("Testing existing events in a period")
  @Test
  public void promotionMustHaveEventGivenStartAndEndDates() {
    promotion.getCurrent().setEndDate(LocalDateTime.now().plusMonths(3));

    Assert.assertFalse(recurrenceRuleService.promotionHasEventsBetween(promotion,
            LocalDate.of(2022, 11, 1),
            LocalDate.of(2022, 11, 15)));
  }
}
