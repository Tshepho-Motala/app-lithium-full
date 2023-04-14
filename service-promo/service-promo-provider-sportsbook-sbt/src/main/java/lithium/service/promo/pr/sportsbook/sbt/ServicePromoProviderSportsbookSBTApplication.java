package lithium.service.promo.pr.sportsbook.sbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.leader.LeaderCandidate;
import lithium.service.accounting.client.stream.event.EnableAccountingTransactionCompletedEvent;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.promo.client.dto.FieldDataType;
import lithium.service.promo.client.dto.ActivityExtraField;
import lithium.service.promo.client.dto.FieldType;
import lithium.service.promo.client.dto.PromoActivity;
import lithium.service.promo.client.dto.PromoProviderRegistration;
import lithium.service.promo.client.stream.EnableMissionStatsStream;
import lithium.service.promo.client.stream.provider.EnablePromoProvider;
import lithium.service.promo.client.stream.provider.PromoProviderService;
import lithium.service.promo.pr.sportsbook.sbt.dto.Activity;
import lithium.service.promo.pr.sportsbook.sbt.dto.Category;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@LithiumService
@EnableScheduling
@EnablePromoProvider
@EnableLeaderCandidate
@EnableChangeLogService
@EnableTranslationsStream
@EnableLithiumServiceClients
@EnableCustomHttpErrorCodeExceptions
@EnableMissionStatsStream
@EnableAccountingTransactionCompletedEvent( transactionTypeCodes = {"SPORTS_WIN", "SPORTS_RESERVE"} )
public class ServicePromoProviderSportsbookSBTApplication extends LithiumServiceApplication {

  @Autowired
  private PromoProviderService promoProviderService;

  @Value( "${test:noop}" )
  private String test;

  @Autowired
  LeaderCandidate leaderCandidate;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServicePromoProviderSportsbookSBTApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);
    registerPromoProvider();
  }

  private void registerPromoProvider() {
    promoProviderService.registerPromoProvider(
        PromoProviderRegistration.builder()
              .name("sport")
            .url(applicationName)
            .category(Category.SPORT)
            .activities(Stream.of(Activity.BET, Activity.WIN).map(activity -> PromoActivity.builder()
                    .activity(activity)
                    .extraFields(new ArrayList<>())
                    .build())
                    .toList())

            .build());
  }

  public List<ActivityExtraField> extraFields() {
    return Arrays.asList(
            ActivityExtraField.builder()
                    .name("league")
                    .type(FieldDataType.TYPE_STRING)
                    .fieldType(FieldType.TYPE_MULTISELECT)
                    .build(),
            ActivityExtraField.builder()
                    .name("sport")
                    .type(FieldDataType.TYPE_STRING)
                    .fieldType(FieldType.TYPE_MULTISELECT)
                    .build(),
            ActivityExtraField.builder()
                    .name("event")
                    .type(FieldDataType.TYPE_STRING)
                    .fieldType(FieldType.TYPE_MULTISELECT)
                    .build(),
            ActivityExtraField.builder()
                    .name("market")
                    .type(FieldDataType.TYPE_STRING)
                    .fieldType(FieldType.TYPE_MULTISELECT)
                    .build(),
            ActivityExtraField.builder()
                    .name("odds")
                    .type(FieldDataType.TYPE_MONEY)
                    .fieldType(FieldType.TYPE_INPUT)
                    .build()
    );
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
