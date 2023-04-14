package lithium.service.promo.pr.user;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.leader.LeaderCandidate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.promo.client.dto.FieldDataType;
import lithium.service.promo.client.dto.ActivityExtraField;
import lithium.service.promo.client.dto.FieldType;
import lithium.service.promo.client.dto.PromoActivity;
import lithium.service.promo.client.dto.PromoProviderRegistration;
import lithium.service.promo.client.stream.EnableMissionStatsStream;
import lithium.service.promo.client.stream.provider.EnablePromoProvider;
import lithium.service.promo.client.stream.provider.PromoProviderService;
import lithium.service.promo.pr.user.dto.Activity;
import lithium.service.promo.pr.user.dto.Category;
import lithium.service.promo.pr.user.dto.ExtraFieldType;
import lithium.service.stats.client.stream.event.EnableStatsCompletedEvent;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

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
@EnableStatsCompletedEvent(events = {"login-success", "registration-success"})
public class ServicePromoProviderUserApplication extends LithiumServiceApplication {

  @Autowired
  private PromoProviderService promoProviderService;

  @Autowired
  LeaderCandidate leaderCandidate;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServicePromoProviderUserApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);
    registerPromoProvider();
  }

  private void registerPromoProvider() {
    //TODO: leaderCandidate needs to be tested, this was not working correctly during TA, please test/figure out why.
    //    if (!leaderCandidate.iAmTheLeader()) {
    //      log.debug("I am not the leader.");
    //      return;
    //    }
    PromoProviderRegistration promoProviderRegistration = PromoProviderRegistration.builder()
        .url(applicationName)
        .name("User")
        .category(Category.USER)
        .activities(Arrays.asList(loginActivity(), registrationActivity()))
        .build();
    log.info("PromoProviderRegistration: " + promoProviderRegistration);
    promoProviderService.registerPromoProvider(promoProviderRegistration);
  }

  private PromoActivity loginActivity() {
    return PromoActivity.builder()
            .activity(Activity.LOGIN)
            .extraFields(Arrays.asList(
            ActivityExtraField.builder()
                    .name(ExtraFieldType.DAYS_OF_WEEK.getType())
                    .type(FieldDataType.TYPE_NUMBER)
                    .fieldType(FieldType.TYPE_MULTISELECT)
                    .fetchExternalData(true)
                    .build(),
            ActivityExtraField.builder()
                    .name(ExtraFieldType.GRANULARITY.getType())
                    .type(FieldDataType.TYPE_NUMBER)
                    .fieldType(FieldType.TYPE_SINGLESELECT)
                    .required(true)
                    .fetchExternalData(true)
                    .build(),
            ActivityExtraField.builder()
                    .name(ExtraFieldType.CONSECUTIVE_LOGINS.getType())
                    .type(FieldDataType.TYPE_NUMBER)
                    .fieldType(FieldType.TYPE_INPUT)
                    .build()
    ))
            .build();
  }

  private PromoActivity registrationActivity() {
    return  PromoActivity.builder()
            .activity(Activity.REGISTRATION)
            .requiresValue(false)
            .extraFields(Arrays.asList(
                    ActivityExtraField.builder()
                            .name(ExtraFieldType.DAYS_OF_WEEK.getType())
                            .type(FieldDataType.TYPE_NUMBER)
                            .fieldType(FieldType.TYPE_MULTISELECT)
                            .fetchExternalData(true)
                            .build(),
                    ActivityExtraField.builder()
                            .name(ExtraFieldType.REFERRER_GUID.getType())
                            .type(FieldDataType.TYPE_STRING)
                            .fieldType(FieldType.TYPE_INPUT)
                            .build(),
                    ActivityExtraField.builder().name(ExtraFieldType.PROMO_CODE.getType())
                            .type(FieldDataType.TYPE_STRING)
                            .fieldType(FieldType.TYPE_INPUT)
                            .build()
            ))
            .build();
  }

//  @Bean
//  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
//    return new Jackson2JsonMessageConverter();
//  }
}
