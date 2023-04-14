package lithium.service.promo.pr.casino.iforium;

import java.util.Arrays;
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
import lithium.service.promo.client.stream.EnableMissionStatsStream;
import lithium.service.promo.client.stream.provider.EnablePromoProvider;
import lithium.service.promo.client.stream.provider.PromoProviderService;
import lithium.service.promo.pr.casino.iforium.dto.Activity;
import lithium.service.promo.pr.casino.iforium.dto.Category;
import lithium.service.promo.pr.casino.iforium.dto.ExtraFieldType;
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
@EnableGamesInternalClientService
@EnableAccountingTransactionCompletedEvent( transactionTypeCodes = {"CASINO_WIN", "CASINO_BET"}, enhanceGameData = true )
public class ServicePromoProviderCasinoIforiumApplication extends LithiumServiceApplication {

  @Autowired
  private PromoProviderService promoProviderService;

  @Value( "${test:noop}" )
  private String test;

  @Autowired
  LeaderCandidate leaderCandidate;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServicePromoProviderCasinoIforiumApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);
    registerPromoProvider();
  }

  private void registerPromoProvider() {
    PromoProviderRegistration registration = PromoProviderRegistration.builder()
        .name("iForium")
        .url(applicationName)
        .category(Category.CASINO)
        .activities(Stream.of(Activity.WAGER, Activity.WIN).map(activity -> PromoActivity.builder()
                .activity(activity)
                .extraFields(Arrays.asList(
                        ActivityExtraField.builder()
                                .name(ExtraFieldType.GAME_PROVIDER.getType())
                                .type(FieldDataType.TYPE_STRING)
                                .fieldType(FieldType.TYPE_SINGLESELECT)
                                .fetchExternalData(true)
                                .build(),
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
                .build()).toList())

        .build();
    log.info("PromoProviderRegistration: " + registration);
    promoProviderService.registerPromoProvider(registration);
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
