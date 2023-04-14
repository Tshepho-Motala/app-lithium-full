package lithium.service.promo.pr.casino.roxor;

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
import lithium.service.games.client.service.EnableGamesInternalClientService;
import lithium.service.promo.client.dto.FieldDataType;
import lithium.service.promo.client.dto.ActivityExtraField;
import lithium.service.promo.client.dto.FieldType;
import lithium.service.promo.client.dto.PromoActivity;
import lithium.service.promo.client.dto.PromoProviderRegistration;
import lithium.service.promo.client.enums.PromoCategory;
import lithium.service.promo.client.stream.EnableMissionStatsStream;
import lithium.service.promo.client.stream.provider.EnablePromoProvider;
import lithium.service.promo.client.stream.provider.PromoProviderService;
import lithium.service.promo.pr.casino.roxor.dto.Activity;
import lithium.service.promo.pr.casino.roxor.dto.Category;
import lithium.service.promo.pr.casino.roxor.dto.ExtraFieldType;
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
@EnableMissionStatsStream
@EnableLithiumServiceClients
@EnableCustomHttpErrorCodeExceptions
@EnableAccountingTransactionCompletedEvent( transactionTypeCodes = {"CASINO_WIN", "CASINO_BET"}, enhanceGameData = true )
@EnableGamesInternalClientService
public class ServicePromoProviderCasinoRoxorApplication extends LithiumServiceApplication {

  @Autowired
  private PromoProviderService promoProviderService;

  @Value( "${test:noop}" )
  private String test;

  @Autowired
  LeaderCandidate leaderCandidate;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServicePromoProviderCasinoRoxorApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);
    registerPromoProvider();
  }

  private void registerPromoProvider() {

    PromoProviderRegistration registration = PromoProviderRegistration.builder()
        .url(applicationName)
        .name("Roxor")
        .category(PromoCategory.CASINO)
        .activities(getPromoActivities())
        .build();
    log.info("PromoProviderRegistration: " + registration);
    promoProviderService.registerPromoProvider(registration);
  }

  private List<PromoActivity> getPromoActivities() {

    return Stream.of(Activity.WAGER, Activity.WIN).map(activity -> PromoActivity.builder()
                    .activity(activity)
                    .extraFields(Arrays.asList(
                            ActivityExtraField.builder()
                                    .name(ExtraFieldType.GAME.getType())
                                    .type(FieldDataType.TYPE_STRING)
                                    .fieldType(FieldType.TYPE_MULTISELECT)
                                    .fetchExternalData(true)
                                    .build(),
                            ActivityExtraField.builder()
                                    .name(ExtraFieldType.GAME_TYPE.getType())
                                    .type(FieldDataType.TYPE_STRING)
                                    .fieldType(FieldType.TYPE_MULTISELECT)
                                    .fetchExternalData(true)
                                    .build()
                    ))
                    .build())
            .toList();
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
