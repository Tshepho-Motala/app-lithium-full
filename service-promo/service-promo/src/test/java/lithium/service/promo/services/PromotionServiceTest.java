package lithium.service.promo.services;


import com.google.common.collect.Lists;
import lithium.service.promo.client.objects.PromotionBO;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.User;
import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.entities.Reward;
import lithium.service.promo.data.repositories.PromotionRepository;
import lithium.service.promo.objects.PromoQuery;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PromotionServiceTest {

    private PromotionRepository promotionRepository;

    private final RecurrenceRuleService recurrenceRuleService = new RecurrenceRuleService();

    private PromotionService promotionService = new PromotionService();

    @DisplayName("Testing promotion without events for a given period")
    @Test
    public void promotionWithoutEventsGivenStartAndEndDates() {
        Promotion promotion = Promotion.builder()
                .current(PromotionRevision.builder()
                        .recurrencePattern("DTSTART:20220801T000000Z\nRRULE:FREQ=DAILY;INTERVAL=3;COUNT=15")
                        .startDate(LocalDateTime.of(LocalDate.of(2022,8, 1), LocalTime.MIN))
                        .build())
                .build();
        promotion.getCurrent().setEndDate(LocalDateTime.now().plusMonths(3));

        Assert.assertFalse(recurrenceRuleService.promotionHasEventsBetween(promotion,
                LocalDate.of(2022, 11, 1),
                LocalDate.of(2022, 11, 15)));
    }

    @DisplayName("Testing existing events in a period")
    @Test
    public void promotionWithEventsGivenStartAndEndDates() {
        Promotion promotion = Promotion.builder()
                .current(PromotionRevision.builder()
                        .recurrencePattern("DTSTART:20220801T000000Z\nRRULE:FREQ=DAILY;INTERVAL=3;COUNT=15")
                        .startDate(LocalDateTime.of(LocalDate.of(2022,8, 1), LocalTime.MIN))
                        .build())
                .build();
        promotion.getCurrent().setEndDate(LocalDateTime.now().plusMonths(3));

        Assert.assertTrue(recurrenceRuleService.promotionHasEventsBetween(promotion,
                LocalDate.of(2022, 8, 1),
                LocalDate.of(2022, 8, 4)));
    }

    @Test
    public void ShouldReturnAllPromotionsWithEventsGivenStartAndEndDate() throws IllegalAccessException {

        promotionRepository = Mockito.mock(PromotionRepository.class);
        promotionService.promotionRepository = promotionRepository;

        FieldUtils.writeField(promotionService, "recurrenceRuleService", recurrenceRuleService, true);

        Promotion promotion1 = getPromotion("Ping Pong Promo", "DTSTART:20220801T000000Z\nRRULE:FREQ=DAILY;INTERVAL=3;COUNT=30",
                LocalDate.of(2022,8, 1));
        Promotion promotion2 = getPromotion("Casino Promotion", "DTSTART:20220413T150000Z\nRRULE:FREQ=MONTHLY;COUNT=30;INTERVAL=1;WKST=MO;BYMONTH=1,3,5,7,8,10,11",
                LocalDate.of(2022,4, 1));
        Promotion promotion3 = getPromotion("Squads Promotion", "DTSTART:20220801T000000Z\nRRULE:FREQ=DAILY;INTERVAL=3;COUNT=30",
                LocalDate.of(2022,8, 1));

        Mockito.when(promotionRepository.findAll(Mockito.any(Specification.class)))
                .thenReturn(Lists.newArrayList(promotion1, promotion2, promotion3));

        LocalDate startDate = LocalDate.of(2022, 9, 1);
        LocalDate endDate =  LocalDate.of(2022, 9, 25);

        List<PromotionBO> results =  promotionService.getPromotionsWithEventsWithPeriod(PromoQuery.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build());
        Assert.assertEquals(2, results.size());
    }

    private static Promotion getPromotion(String name, String recurrencePattern, LocalDate startDate){
        User editor = User.builder().guid("anyGuid").build();
        Domain domain = Domain.builder().name("anyDomain").build();
        Reward reward = Reward.builder().id(1L).rewardId(1L).build();
        return Promotion.builder()
                .editor(editor)
                .current(PromotionRevision.builder()
                        .name(name)
                        .recurrencePattern(recurrencePattern)
                        .startDate(LocalDateTime.of(startDate, LocalTime.MIN))
                        .domain(domain)
                        .reward(reward)
                        .build())
                .build();
    }

    @BeforeEach
    public void setup() {
        promotionRepository = Mockito.mock(PromotionRepository.class);
        promotionService = new PromotionService();
    }
}
