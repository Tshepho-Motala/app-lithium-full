package lithium.service.promo.services;

import lithium.service.promo.client.objects.PromoActivityBasic;
import lithium.service.promo.context.PromoContext;
import lithium.service.promo.stubs.ActivityStub;
import lithium.service.promo.stubs.CategoryStub;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest
class PromotionsStatsServiceTest {

  @Autowired
  PromotionStatsService promotionStatsService;
  @Autowired
  DomainService domainService;

  PromoActivityBasic promoActivityBasic;

  @BeforeEach
  void setUp() {
    //    String gameGuid = "service-casino-provider-roxor_play-10p-roulette";
    String gameGuid = "service-casino-provider-roxor_play-banghai";
    String gameType = "Slots";

    promoActivityBasic = PromoActivityBasic.builder()
        .category(CategoryStub.CASINO)
        .activity(ActivityStub.WAGER)
        .ownerGuid("livescore_uk/586")
        .domainName("livescore_uk")
        .labelValues(
            Stream.of(new String[][] {{"game", gameGuid}, {"gameType", gameType},}).collect(Collectors.toMap(data -> data[0], data -> data[1])))
        .value(5000L)
        .build();
  }

  //  @Test
  @DisplayName( "Test user promotions." )
  public void testPromotions()
  throws Exception
  {
    log.info("Sending: " + promoActivityBasic);
    PromoContext promoContext = PromoContext.builder().promoActivityBasic(promoActivityBasic).build();
    promotionStatsService.register(promoContext);
  }
}