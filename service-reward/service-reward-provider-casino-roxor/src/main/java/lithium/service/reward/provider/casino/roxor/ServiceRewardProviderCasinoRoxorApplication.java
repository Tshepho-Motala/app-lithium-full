package lithium.service.reward.provider.casino.roxor;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.rest.EnableRestTemplate;
import lithium.service.accounting.client.transactiontyperegister.EnableTransactionTypeRegisterService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.games.client.service.EnableGamesInternalClientService;
import lithium.service.reward.client.dto.FieldDataType;
import lithium.service.reward.client.dto.RewardType;
import lithium.service.reward.client.dto.RewardTypeField;
import lithium.service.reward.client.stream.EnableRewardTypeStream;
import lithium.service.reward.client.stream.RewardTypeStream;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;

import static java.util.Arrays.asList;

@LithiumService
@EnableScheduling
@EnableRestTemplate
@EnableLeaderCandidate
@EnableChangeLogService
@EnableRewardTypeStream
@EnableTranslationsStream
@EnableLithiumServiceClients
@EnableCustomHttpErrorCodeExceptions
@EnableTransactionTypeRegisterService
@EnableProviderClient
@EnableGamesInternalClientService
public class ServiceRewardProviderCasinoRoxorApplication extends LithiumServiceApplication {

  @Autowired
  RewardTypeStream rewardTypeStream;

  @Value( "${spring.application.name}" )
  private String applicationName;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServiceRewardProviderCasinoRoxorApplication.class, args);
  }

  @EventListener
  public void startup(ApplicationStartedEvent e)
  throws Exception
  {
    super.startup(e);
    registerRewardType("RX");
  }

  private void registerRewardType(String code) {
    rewardTypeStream.registerRewardType(RewardType.builder()
        .name(RewardTypeName.FREESPIN.rewardTypeName())
        .url(applicationName)
        .code(code)
        .setupFields(new ArrayList<>(asList(RewardTypeField.builder()
            .name(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName())
            .dataType(FieldDataType.TYPE_NUMBER)
            .description("How many freespins to award when reward is given to player.")
            .build(), RewardTypeField.builder()
            .name(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName())
            .dataType(FieldDataType.TYPE_MONEY)
            .description("Monetary value of each freespin awarded.")
            .build())))
        .displayGames(true)
        .build());
    rewardTypeStream.registerRewardType(RewardType.builder()
        .name(RewardTypeName.INSTANT_REWARD.rewardTypeName())
        .url(applicationName)
        .code(code)
        .setupFields(new ArrayList<>(asList(RewardTypeField.builder()
            .name(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName())
            .dataType(FieldDataType.TYPE_NUMBER)
            .description("How many instant rewards to award when reward is given to player.")
            .build(), RewardTypeField.builder()
            .name(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName())
            .dataType(FieldDataType.TYPE_MONEY)
            .description("Monetary value of each instant reward awarded.")
            .build())))
        .displayGames(true)
        .build());

    rewardTypeStream.registerRewardType(RewardType.builder()
            .name(RewardTypeName.INSTANT_REWARD_FREESPIN.rewardTypeName())
            .url(applicationName)
            .code(code)
            .setupFields(new ArrayList<>(asList(RewardTypeField.builder()
                    .name(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName())
                    .dataType(FieldDataType.TYPE_NUMBER)
                    .description("How many instant rewards to award when reward is given to player.")
                    .build(), RewardTypeField.builder()
                    .name(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName())
                    .dataType(FieldDataType.TYPE_MONEY)
                    .description("Monetary value of each instant reward awarded.")
                    .build())))
            .displayGames(true)
            .build());
    rewardTypeStream.registerRewardType(RewardType.builder()
        .name(RewardTypeName.CASINO_CHIP.rewardTypeName())
        .url(applicationName)
        .code(code)
        .setupFields(new ArrayList<>(asList(RewardTypeField.builder()
            .name(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName())
            .dataType(FieldDataType.TYPE_MONEY)
            .description("Monetary value of each casino chip awarded.")
            .build())))
        .displayGames(true)
        .build());
  }
}
