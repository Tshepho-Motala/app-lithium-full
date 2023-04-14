package lithium.service.reward.service;

import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.objects.AdjustmentRequest;
import lithium.service.accounting.objects.AdjustmentRequestComponent;
import lithium.service.accounting.objects.AdjustmentResponse;
import lithium.service.client.util.LabelManager;
import lithium.service.exception.Status511UpstreamServiceUnavailableException;
import lithium.service.reward.client.dto.GiveRewardContext;
import lithium.service.reward.client.dto.RewardType;
import lithium.service.reward.data.entities.RewardRevisionType;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountingService {

  @Autowired
  private AccountingClientService accountingClientService;

  private static final String SYSTEM_USER = "system";
  private static final String ADJUST_COMPONENT_ACTIVATION = "activation";
  private static final String ADJUST_COMPONENT_CASH = "cash";


  public AdjustmentResponse processActivation(RewardRevisionType rewardRevisionType, GiveRewardContext context, long amountAffected)
  throws Status511UpstreamServiceUnavailableException
  {
    return processActivation(context.map(rewardRevisionType.getRewardType(), RewardType.class), rewardRevisionType.getRewardRevision().getId(),
        context.domainName(), context.getDomain().getCurrency(), context.playerGuid(), context.getPlayerRewardTypeHistoryId(), amountAffected);
  }

  private AdjustmentResponse processActivation(RewardType rewardType, Long rewardRevisionId, String domainName, String domainCurrencyCode,
      String playerGuid, Long playerRewardTypeHistoryId, long amountAffected)
  throws Status511UpstreamServiceUnavailableException
  {
    AdjustmentRequest adjustmentRequest = AdjustmentRequest.builder().domainName(domainName).build();
    adjustmentRequest.add(
        constructAdjustmentRequestComponent(ADJUST_COMPONENT_ACTIVATION, rewardType, rewardRevisionId, domainName, domainCurrencyCode, playerGuid,
            playerRewardTypeHistoryId, amountAffected));

    return adjust(rewardType, rewardRevisionId, domainName, domainCurrencyCode, playerGuid, playerRewardTypeHistoryId, amountAffected,
        adjustmentRequest);
  }

  public AdjustmentResponse processCash(RewardType rewardType, Long rewardRevisionId, String domainName, String domainCurrencyCode, String playerGuid,
      Long playerRewardTypeHistoryId, long amountAffected)
  throws Status511UpstreamServiceUnavailableException
  {
    AdjustmentRequest adjustmentRequest = AdjustmentRequest.builder().domainName(domainName).build();
    adjustmentRequest.add(
        constructAdjustmentRequestComponent(ADJUST_COMPONENT_ACTIVATION, rewardType, rewardRevisionId, domainName, domainCurrencyCode, playerGuid,
            playerRewardTypeHistoryId, amountAffected));
    adjustmentRequest.add(
        constructAdjustmentRequestComponent(ADJUST_COMPONENT_CASH, rewardType, rewardRevisionId, domainName, domainCurrencyCode, playerGuid,
            playerRewardTypeHistoryId, amountAffected));
    return adjust(rewardType, rewardRevisionId, domainName, domainCurrencyCode, playerGuid, playerRewardTypeHistoryId, amountAffected,
        adjustmentRequest);
  }

  private AdjustmentResponse adjust(RewardType rewardType, Long rewardRevisionId, String domainName, String domainCurrencyCode, String playerGuid,
      Long playerRewardTypeHistoryId, long amountAffected, AdjustmentRequest adjustmentRequest)
  {
    AdjustmentResponse adjustmentResponse;
    try {
      adjustmentResponse = accountingClientService.adjust(adjustmentRequest);
      log.debug("Accounting response: " + adjustmentResponse);
    } catch (Exception e) {
      log.error(
          "Could not register bonus activation on svc-accounting. rewardType: {}, rewardRevisionId: {}, domainName: {}, domainCurrencyCode: {}, playerGuid: {}, playerRewardTypeHistoryId: {}, amountAffected: {} :: {}",
          rewardType, rewardRevisionId, domainName, domainCurrencyCode, playerGuid, playerRewardTypeHistoryId, amountAffected, e);
      throw new Status511UpstreamServiceUnavailableException("Could not communicate with svc-accounting: " + e.getMessage());
    }
    return adjustmentResponse;
  }

  private AdjustmentRequestComponent constructAdjustmentRequestComponent(String adjustComponent, RewardType rewardType, Long rewardRevisionId,
      String domainName, String domainCurrencyCode, String playerGuid, Long playerRewardTypeHistoryId, long amountAffected)
  {
    String rewardFullSuffix = rewardType.getName().toUpperCase();

    if (rewardType.getCode()
        != null) { //getCode is null for service-reward (adding a null guard check so that it does not fail for unlock-games and cash rewards
      rewardFullSuffix = rewardType.getCode().toUpperCase() + "_" + rewardType.getName().toUpperCase();
    }

    LabelManager labelManager = new LabelManager();
    labelManager.addLabel(LabelManager.PLAYER_REWARD_TYPE_HISTORY_ID, playerRewardTypeHistoryId + "");
    labelManager.addLabel(LabelManager.REWARD_REVISION_ID, rewardRevisionId + "");
    labelManager.addLabel(LabelManager.PROVIDER_GUID, rewardType.getUrl());

    AdjustmentRequestComponent adjustmentRequestComponent = AdjustmentRequestComponent.builder()
        .allowNegativeAdjust(false)
        .amountCents(amountAffected)
        .authorGuid(SYSTEM_USER)
        .currencyCode(domainCurrencyCode)
        .date(DateTime.now())
        .domainName(domainName)
        .labels(labelManager.getLabelArray())
        .ownerGuid(playerGuid)
        .build();

    adjustmentRequestComponent = switch (adjustComponent) {
      case ADJUST_COMPONENT_ACTIVATION -> buildActivationComponent(adjustmentRequestComponent, rewardFullSuffix);
      case ADJUST_COMPONENT_CASH -> buildCashComponent(adjustmentRequestComponent, rewardFullSuffix);
      default -> throw new IllegalArgumentException();
    };
    return adjustmentRequestComponent;
  }

  private AdjustmentRequestComponent buildActivationComponent(AdjustmentRequestComponent adjustmentRequestComponent, String rewardFullSuffix) {
    adjustmentRequestComponent.setAccountCode("PLAYER_BALANCE_REWARD_" + rewardFullSuffix);
    adjustmentRequestComponent.setAccountTypeCode("PLAYER_BALANCE_REWARD");
    adjustmentRequestComponent.setContraAccountCode("REWARD_BALANCE_" + rewardFullSuffix);
    adjustmentRequestComponent.setContraAccountTypeCode("REWARD_BALANCE");
    adjustmentRequestComponent.setTransactionTypeCode("REWARD_ACTIVATE");
    return adjustmentRequestComponent;
  }

  private AdjustmentRequestComponent buildCashComponent(AdjustmentRequestComponent adjustmentRequestComponent, String rewardFullSuffix) {
    adjustmentRequestComponent.setAccountCode("PLAYER_BALANCE");
    adjustmentRequestComponent.setAccountTypeCode("PLAYER_BALANCE");
    adjustmentRequestComponent.setContraAccountCode("PLAYER_BALANCE_REWARD_" + rewardFullSuffix);
    adjustmentRequestComponent.setContraAccountTypeCode("PLAYER_BALANCE_REWARD");
    adjustmentRequestComponent.setTransactionTypeCode("TRANSFER_FROM_REWARD");
    return adjustmentRequestComponent;
  }
}
