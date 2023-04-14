package lithium.service.reward.provider.casino.blueprint;

import static java.util.Arrays.asList;
import java.util.ArrayList;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.games.client.service.EnableGamesInternalClientService;
import lithium.service.reward.client.dto.FieldDataType;
import lithium.service.reward.client.dto.RewardType;
import lithium.service.reward.client.dto.RewardTypeField;
import lithium.service.reward.client.stream.EnableRewardTypeStream;
import lithium.service.reward.client.stream.RewardTypeStream;
import lithium.service.reward.provider.casino.blueprint.enums.RewardTypeFieldName;
import lithium.service.reward.provider.casino.blueprint.enums.RewardTypeName;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@LithiumService
@EnableScheduling
@EnableLeaderCandidate
@EnableChangeLogService
@EnableRewardTypeStream
@EnableTranslationsStream
@EnableLithiumServiceClients
@EnableCustomHttpErrorCodeExceptions
@EnableProviderClient
@EnableRestTemplate
@EnableGamesInternalClientService
public class ServiceRewardProviderCasinoBlueprintApplication extends LithiumServiceApplication {

  @Autowired
  RewardTypeStream rewardTypeStream;

  @Value( "${spring.application.name}" )
  private String applicationName;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServiceRewardProviderCasinoBlueprintApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e) throws Exception {
    super.startup(e);
    registerRewardType();
  }

  @Bean
  public RestTemplate restTemplate(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
    return builder.build();
  }

  private void registerRewardType() {
    // @formatter:off
    rewardTypeStream.registerRewardType(
      RewardType.builder()
      .name(RewardTypeName.FREESPIN.rewardTypeName())
      .code("BP")
      .url(applicationName)
      .setupFields(new ArrayList<>(asList(
        RewardTypeField.builder()
        .name(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName())
        .dataType(FieldDataType.TYPE_NUMBER)
        .description("How many freespins to award when reward is given to player.")
        .build(),
        RewardTypeField.builder()
        .name(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName())
        .dataType(FieldDataType.TYPE_MONEY)
        .description("Monetary value of each freespin awarded.")
        .build()
      )))
      .displayGames(true)
      .build()
    );
    // @formatter:on
  }
}
