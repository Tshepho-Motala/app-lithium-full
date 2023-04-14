package lithium.service.reward;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.accounting.client.transactiontyperegister.EnableTransactionTypeRegisterService;
import lithium.service.accounting.client.transactiontyperegister.TransactionTypeRegisterService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.client.util.LabelManager;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.domain.client.EnableProviderClient;
import lithium.service.games.client.service.EnableGamesInternalClientService;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.promo.client.services.EnablePromotionsClientService;
import lithium.service.reward.client.dto.FieldDataType;
import lithium.service.reward.client.dto.RewardType;
import lithium.service.reward.client.dto.RewardTypeField;
import lithium.service.reward.client.stream.EnableRewardTypeStream;
import lithium.service.reward.client.stream.RewardTypeStream;
import lithium.service.reward.enums.RewardTransactionType;
import lithium.service.reward.enums.RewardTypeFieldName;
import lithium.service.reward.enums.RewardTypeName;
import lithium.service.reward.service.RewardNotificationService;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@LithiumService
@EnableScheduling
@EnableLeaderCandidate
@EnableChangeLogService
@EnableRewardTypeStream
@EnableTranslationsStream
@EnableLithiumServiceClients
@EnableAccountingClientService
@EnablePromotionsClientService
@EnableLimitInternalSystemClient
@EnableUserApiInternalClientService
@EnableCustomHttpErrorCodeExceptions
@EnableTransactionTypeRegisterService
@EnableGamesInternalClientService
@EnableNotificationStream
@EnableProviderClient
@EnableDomainClient
public class ServiceRewardApplication extends LithiumServiceApplication {

  @Autowired
  RewardTypeStream rewardTypeStream;
  @Autowired
  private TransactionTypeRegisterService transactionTypeService;

  @Autowired
  private RewardNotificationService rewardNotificationService;

  @Value( "${spring.application.name}" )
  private String applicationName;

  public static void main(String[] args) {
    LithiumShutdownSpringApplication.run(ServiceRewardApplication.class, args);
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @EventListener
  public void startup(ApplicationStartedEvent e)
  throws Exception
  {
    super.startup(e);
    registerRewardType();
    registerAccountingData();
    rewardNotificationService.registerNotificationTypes();
  }

  private void registerRewardType() {
    rewardTypeStream.registerRewardType(RewardType.builder()
        .name(RewardTypeName.CASH.rewardTypeName())
        .url(applicationName)
        .setupFields(new ArrayList<>(asList(
            //TODO: currently defaults to domain currency. this setup field needs to be changed so that a dropdown is shown of configured currencies on the domain.
//            RewardTypeField.builder()
//            .name(RewardTypeFieldName.CURRENCY_CODE.rewardTypeFieldName())
//            .dataType(FieldDataType.TYPE_CURRENCY)
//            .description("The currency of the free money to award the player.")
//            .build(),
            RewardTypeField.builder()
            .name(RewardTypeFieldName.VALUE_IN_CENTS.rewardTypeFieldName())
            .dataType(FieldDataType.TYPE_MONEY)
            .description("The amount of free money to award the player.")
            .build())))
        .displayGames(false)
        .build());
    rewardTypeStream.registerRewardType(
        RewardType.builder().name(RewardTypeName.UNLOCK_GAMES.rewardTypeName()).url(applicationName).displayGames(true).build());
  }

  private void registerAccountingData() {
    {
      Long ttid = transactionTypeService.create(RewardTransactionType.REWARD_ACTIVATE.value()).getData().getId();
      transactionTypeService.addAccount(ttid, RewardTransactionType.PLAYER_BALANCE_REWARD.value(), false, true);
      transactionTypeService.addAccount(ttid, RewardTransactionType.REWARD_BALANCE.value(), true, false);
      transactionTypeService.addLabel(ttid, LabelManager.PLAYER_REWARD_TYPE_HISTORY_ID, true);
      transactionTypeService.addLabel(ttid, LabelManager.REWARD_REVISION_ID, true);
      transactionTypeService.addLabel(ttid, LabelManager.PROVIDER_GUID, true);
    }

    {
      Long ttid = transactionTypeService.create(RewardTransactionType.REWARD_BET.value()).getData().getId();
      transactionTypeService.addAccount(ttid, RewardTransactionType.PLAYER_BALANCE_REWARD.value(), true, false);
      transactionTypeService.addAccount(ttid, RewardTransactionType.REWARD_BET.value(), false, true);
      labels(RewardTransactionType.REWARD_BET.value(), ttid);
    }

    {
      Long ttid = transactionTypeService.create(RewardTransactionType.REWARD_BET_ROLLBACK.value()).getData().getId();
      transactionTypeService.addAccount(ttid, RewardTransactionType.PLAYER_BALANCE_REWARD.value(), false, true);
      transactionTypeService.addAccount(ttid, RewardTransactionType.REWARD_BET.value(), true, true);
      transactionTypeService.addUniqueLabel(ttid, LabelManager.REVERSE_TRANSACTION_ID, false, RewardTransactionType.REWARD_BET.value());
      transactionTypeService.addLabel(ttid, LabelManager.ORIGINAL_TRANSACTION_ID, false);
      commonLabels(ttid);
    }

    {
      Long ttid = transactionTypeService.create(RewardTransactionType.REWARD_WIN.value()).getData().getId();
      transactionTypeService.addAccount(ttid, RewardTransactionType.PLAYER_BALANCE.value(), false, true);
      transactionTypeService.addAccount(ttid, RewardTransactionType.REWARD_WIN.value(), true, false);
      labels(RewardTransactionType.REWARD_WIN.value(), ttid);
    }

    {
      Long ttid = transactionTypeService.create(RewardTransactionType.REWARD_WIN_ROLLBACK.value()).getData().getId();
      transactionTypeService.addAccount(ttid, RewardTransactionType.PLAYER_BALANCE.value(), true, false);
      transactionTypeService.addAccount(ttid, RewardTransactionType.REWARD_WIN.value(), false, true);
      labels(RewardTransactionType.REWARD_WIN.value(), ttid);
    }

    {
      Long ttid = transactionTypeService.create(RewardTransactionType.REWARD_LOSS.value()).getData().getId();
      transactionTypeService.addAccount(ttid, RewardTransactionType.PLAYER_BALANCE.value(), false, true);
      transactionTypeService.addAccount(ttid, RewardTransactionType.REWARD_LOSS.value(), true, false);
      labels(RewardTransactionType.REWARD_LOSS.value(), ttid);
    }

    {
      Long ttid = transactionTypeService.create(RewardTransactionType.TRANSFER_FROM_REWARD.value()).getData().getId();
      transactionTypeService.addAccount(ttid, RewardTransactionType.PLAYER_BALANCE.value(), false, true);
      transactionTypeService.addAccount(ttid, RewardTransactionType.PLAYER_BALANCE_REWARD.value(), true, false);
      transactionTypeService.addLabel(ttid, LabelManager.PLAYER_REWARD_TYPE_HISTORY_ID, true);
      transactionTypeService.addLabel(ttid, LabelManager.REWARD_REVISION_ID, true);
      transactionTypeService.addLabel(ttid, LabelManager.PROVIDER_GUID, true);
    }

    transactionTypeService.register();
  }

  private void labels(String accountTypeCode, Long ttid) {
    transactionTypeService.addUniqueLabel(ttid, LabelManager.TRANSACTION_ID, false, accountTypeCode);
    commonLabels(ttid);
  }

  private void commonLabels(Long ttid) {
    transactionTypeService.addOptionalLabel(ttid, LabelManager.PLAYER_REWARD_TYPE_HISTORY_ID, true);
    //    transactionTypeService.addLabel(ttid, LabelManager.REWARD_REVISION_ID, true); // Can be traced from player_reward_type_history above
    transactionTypeService.addLabel(ttid, LabelManager.PROVIDER_GUID, true);
    transactionTypeService.addLabel(ttid, LabelManager.GAME_GUID, true);
    transactionTypeService.addLabel(ttid, LabelManager.LOGIN_EVENT_ID, true, true, false);
  }
}
