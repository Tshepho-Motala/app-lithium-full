package lithium.service.reward.service;

import com.google.common.collect.Lists;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.objects.AdjustmentResponse;
import lithium.service.accounting.objects.AdjustmentTransaction.AdjustmentResponseStatus;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.reward.client.RewardProviderClient;
import lithium.service.reward.client.dto.GiveRewardContext;
import lithium.service.reward.client.dto.GiveRewardRequest;
import lithium.service.reward.client.dto.GiveRewardResponse;
import lithium.service.reward.client.dto.PlayerRewardComponentHistoryFE;
import lithium.service.reward.client.dto.PlayerRewardComponentStatus;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.client.dto.RewardRevisionTypeValueOverride;
import lithium.service.reward.client.exception.Status466InvalidRewardStateException;
import lithium.service.reward.client.exception.Status505UnavailableException;
import lithium.service.reward.data.entities.Domain;
import lithium.service.reward.data.entities.PlayerRewardHistory;
import lithium.service.reward.data.entities.PlayerRewardTypeHistory;
import lithium.service.reward.data.entities.RewardRevision;
import lithium.service.reward.data.entities.RewardRevisionType;
import lithium.service.reward.data.entities.RewardRevisionTypeValue;
import lithium.service.reward.data.entities.User;
import lithium.service.reward.enums.RewardTypeName;
import lithium.service.reward.mappers.RewardBOMapper;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardStatus;
import lithium.service.reward.provider.client.dto.ProcessRewardTypeValue;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class GiveRewardService {

  @Autowired
  GiveRewardService self;
  @Autowired
  LithiumServiceClientFactory clientFactory;
  @Autowired
  UserService userService;
  @Autowired
  RewardService rewardService;
  @Autowired
  PlayerRewardHistoryService playerRewardHistoryService;
  @Autowired
  AccountingService accountingService;
  @Autowired
  LimitService limitService;
  @Autowired
  DomainService domainService;
  @Autowired
  RewardBOMapper rewardBOMapper;

  @Autowired
  private RewardNotificationService rewardNotificationService;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private PlayerRewardTypeHistoryService playerRewardHistoryValueService;

  @Autowired
  private LithiumTokenUtilService lithiumTokenUtilService;

  @Autowired
  private ChangeLogService changeLogService;


  @TimeThisMethod
  public GiveRewardResponse giveReward(GiveRewardContext context)
  throws Exception
  {

    User player = userService.findOrCreate(context.getGiveRewardRequest().getPlayerGuid());
    context.setPlayer(context.map(player, lithium.service.reward.client.dto.User.class));

    Domain domain = domainService.findOrCreate(player.domainName());
    lithium.service.domain.client.objects.Domain externalDomain = domainService.externalDomain(domain.getName());
    context.setDomain(lithium.service.reward.client.dto.Domain.builder().name(domain.getName()).currency(externalDomain.getCurrency()).build());

    limitService.performAllAccessChecks(context, context.getLocale());

    RewardRevision rewardRevision = rewardService.findCurrentRevision(domain, context.getGiveRewardRequest().getRewardId());
    if (rewardRevision == null) {
      log.warn("No reward found for domain: {}, rewardId: {} for player: {}", domain, context.getGiveRewardRequest().getRewardId(), player.guid());
      return GiveRewardResponse.builder().status(-1).build();
    }
    context.addLog("RewardRevision: " + rewardRevision);

    PlayerRewardHistory playerRewardHistory = playerRewardHistoryService.createHistory(player, rewardRevision,
        context.getGiveRewardRequest().getRewardSource());

    GiveRewardResponse internalProcessGiveReward = GiveRewardResponse.builder().build();
    try {
      internalProcessGiveReward = self.internalProcessGiveReward(context, playerRewardHistory, rewardRevision);
    } catch (Exception e) {
      playerRewardHistoryService.changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.FAILED);
    }
    return internalProcessGiveReward;
  }

  @TimeThisMethod
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
  public GiveRewardResponse internalProcessGiveReward(GiveRewardContext context, PlayerRewardHistory playerRewardHistory, RewardRevision rewardRevision)
  throws Exception
  {
    int status = 0;
    boolean hasNonInstant = false;
    boolean hasFailedRewardType = false;
    boolean isFullRedeemed = false;

    //PlayerRewardTypeHistoryValue.builder().rewardRevisionTypeId(1L).rewardTypeFieldId(3L).value("99").build(),
    for (RewardRevisionType rewardRevisionType: rewardRevision.getRevisionTypes()) {
      log.debug("RewardType: " + rewardRevisionType);
      context.addLog("RewardType: " + rewardRevisionType);
      PlayerRewardTypeHistory playerRewardTypeHistory = playerRewardHistoryService.createTypeHistory(playerRewardHistory, rewardRevisionType);
      context.setPlayerRewardTypeHistoryId(playerRewardTypeHistory.getId());

      /**
       * We will save the override values if we have any so that we can reuse when the player acknowledges the reward
       */

      List<RewardRevisionTypeValueOverride> overrides = context.findRewardRevisionTypeOverride(rewardRevisionType.getId());

      if (overrides != null && !overrides.isEmpty()) {
        log.debug("Saving reward type override values: {}", overrides);
        playerRewardHistoryValueService.saveHistoryValuesFromOverrides(playerRewardTypeHistory, overrides);
      }

      /*
      If the reward is not instant, it must first be accepted by the player before being awarded.
      Marked as PENDING_PLAYER_APPROVAL.
      Will have to follow another route.
       */
      if (!rewardRevisionType.isInstant()) {
        context.addLog("Skipping PENDING_PLAYER_APPROVAL");
        rewardNotificationService.sendPendingRewardTypeNotification(playerRewardTypeHistory);
        continue;
      }

     processRewardType(rewardRevisionType, context, playerRewardTypeHistory);
    }

    //Refresh player reward
    List<PlayerRewardTypeHistory> freshPlayerRewardTypeHistories = playerRewardHistoryService.findPlayerRewardTypeHistoryByPlayerRewardHistory(playerRewardHistory);

    hasFailedRewardType = freshPlayerRewardTypeHistories.stream().anyMatch(history -> Arrays.asList(PlayerRewardComponentStatus.FAILED_EXTERNALLY, PlayerRewardComponentStatus.FAILED_INTERNALLY).contains(history.getStatus()));
    hasNonInstant = freshPlayerRewardTypeHistories.stream().anyMatch(history -> history.getStatus() == PlayerRewardComponentStatus.PENDING_PLAYER_APPROVAL);
    boolean allComponentsFailed = playerRewardHistoryService.filterByStatuses(freshPlayerRewardTypeHistories, List.of(PlayerRewardComponentStatus.FAILED_EXTERNALLY, PlayerRewardComponentStatus.FAILED_INTERNALLY)).size() == freshPlayerRewardTypeHistories.size();
    isFullRedeemed = playerRewardHistoryService.filterByStatuses(freshPlayerRewardTypeHistories, List.of(PlayerRewardComponentStatus.REDEEMED)).size() == freshPlayerRewardTypeHistories.size();

    if (hasNonInstant) {
      playerRewardHistoryService.changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.PENDING);
    }

    //If all the reward components have failed then it reward should fail as well
    if (allComponentsFailed) {
      playerRewardHistoryService.changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.FAILED);
    } else if (hasFailedRewardType) {
      playerRewardHistoryService.changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.PARTIALLY_AWARDED);
    }

    if (!hasFailedRewardType && !hasNonInstant) {
      playerRewardHistoryService.markAwarded(playerRewardHistory, rewardRevision);
    }

    if (isFullRedeemed) {
      playerRewardHistoryService.markAsRedeemed(playerRewardHistory);
    }

    context.setGiveRewardResponse(GiveRewardResponse.builder().status(status).build());

    return context.getGiveRewardResponse();
  }

  private boolean processRewardType(RewardRevisionType rewardRevisionType, GiveRewardContext context, PlayerRewardTypeHistory playerRewardTypeHistory)
  throws Exception
  {
    boolean hasFailedRewardType = false;
    ProcessRewardResponse processRewardResponse = processRewardTypeOnRewardProvider(rewardRevisionType, context);
    RewardTypeName rewardType = RewardTypeName.fromType(rewardRevisionType.getRewardType().getName());

    if (rewardType == RewardTypeName.CASH) {
        /*
          This is a special case, which is handled differently.
          We cannot award cash before we register the activation.
          Activation and cash will be handled in the processing.
         */
      if (processRewardResponse.getStatus() == ProcessRewardStatus.SUCCESS) {
        playerRewardTypeHistory.setStatus(PlayerRewardComponentStatus.AWARDED);
        playerRewardTypeHistory.setReferenceId(processRewardResponse.getExternalReferenceId());
        playerRewardTypeHistory.setAwardedDate(new Date());
      } else {
        hasFailedRewardType = true;
        playerRewardTypeHistory.setStatus(PlayerRewardComponentStatus.FAILED_INTERNALLY);
      }
    } else {
      if (processRewardResponse.getStatus() == ProcessRewardStatus.SUCCESS) { //This feels hacky.
        playerRewardTypeHistory.setStatus(PlayerRewardComponentStatus.PROCESSED_EXTERNALLY);
        playerRewardTypeHistory.setReferenceId(processRewardResponse.getExternalReferenceId());

        // go to accounting.
        AdjustmentResponse adjustmentResponse = new AdjustmentResponse();

        try {
          adjustmentResponse = accountingService.processActivation(rewardRevisionType, context, processRewardResponse.getAmountAffected());
        } catch (Exception e) {
          log.error("Could not process activation on accounting. {}", context, e);
        }

        if ((!adjustmentResponse.getAdjustments().isEmpty()) && (adjustmentResponse.getAdjustments().get(0).getStatus() == AdjustmentResponseStatus.NEW)) {
          playerRewardTypeHistory.setStatus(PlayerRewardComponentStatus.AWARDED);
          playerRewardTypeHistory.setAwardedDate(new Date());
        } else {
          hasFailedRewardType = true;
          playerRewardTypeHistory.setStatus(PlayerRewardComponentStatus.FAILED_INTERNALLY);
        }

      } else {
        hasFailedRewardType = true;
        playerRewardTypeHistory.setStatus(PlayerRewardComponentStatus.FAILED_EXTERNALLY);
        playerRewardTypeHistory.setReferenceId((processRewardResponse.getErrorCode() != null) ? processRewardResponse.getErrorCode() + "" : "");
      }
      playerRewardTypeHistory = playerRewardHistoryService.savePlayerRewardTypeHistory(playerRewardTypeHistory);

    }
    playerRewardHistoryService.savePlayerRewardTypeHistory(playerRewardTypeHistory);

    if (processRewardResponse.getStatus() == ProcessRewardStatus.SUCCESS) {
      playerRewardTypeHistory.setValueGiven(BigDecimal.valueOf(processRewardResponse.getValueGiven()));
      playerRewardTypeHistory.setValueUsed(BigDecimal.valueOf(processRewardResponse.getValueUsed()));
      playerRewardTypeHistory.setValueInCents(BigDecimal.valueOf(processRewardResponse.getValueInCents()));


      if (processRewardResponse.getValueUsed().compareTo(processRewardResponse.getValueGiven()) == 0) {
        playerRewardTypeHistory.setStatus(PlayerRewardComponentStatus.REDEEMED);
        playerRewardTypeHistory.setRedeemedDate(new DateTime().toDate());
      }

      playerRewardHistoryService.savePlayerRewardTypeHistory(playerRewardTypeHistory);

      String comment = MessageFormat.format("Player {0} received reward component {1} from reward {2}",
              playerRewardTypeHistory.getPlayerRewardHistory().getPlayer().guid(), playerRewardTypeHistory.getRewardRevisionType().getRewardType().getName(), playerRewardTypeHistory.getRewardRevisionType().getRewardRevision().getName());

      registerPlayerRewardTypeHistoryStatusChangelogs(playerRewardTypeHistory, "comment", comment,
              null, playerRewardTypeHistory.getStatus().status(), SubCategory.REWARD_GRANT);

      log.debug(comment);
      log.debug("Registered progress stats for reward component {}, valueGiven: {}, valueInCents: {}", rewardRevisionType.getRewardType().getName(),processRewardResponse.getValueGiven(), processRewardResponse.getValueInCents());

    }
    return hasFailedRewardType;
  }

  private ProcessRewardResponse processRewardTypeOnRewardProvider(RewardRevisionType rewardRevisionType, GiveRewardContext context)
  throws Exception //TODO: remove Exception, and add specific status code exceptions
  {
    RewardProviderClient rewardProviderClient = rewardProviderClient(rewardRevisionType.rewardTypeUrl());

    //List of overrides sent
    List<RewardRevisionTypeValueOverride> typeValueOverrides = context.findRewardRevisionTypeOverride(rewardRevisionType.getId());
    //List of reward types associated with this revision.
    List<RewardRevisionTypeValue> rewardRevisionTypeValues = rewardService.findByRewardRevisionType(rewardRevisionType.getId());
    for (RewardRevisionTypeValueOverride override: typeValueOverrides) {
      for (RewardRevisionTypeValue rewardRevisionTypeValue: rewardRevisionTypeValues) {
        if ((override.getRewardRevisionTypeId().equals(rewardRevisionTypeValue.getRewardRevisionType().getId())) && (override.getRewardTypeFieldId()
            .equals(rewardRevisionTypeValue.getRewardTypeField().getId()))) {
          context.addLog("Override Specified: " + rewardRevisionTypeValue + " --> " + override.getValue());
          rewardRevisionTypeValue.setValue(override.getValue());
        }
      }
    }

    List<ProcessRewardTypeValue> processRewardTypeValues = rewardRevisionTypeValues.stream()
        .filter(r -> r.getRewardRevisionType().rewardTypeUrl().equalsIgnoreCase(rewardRevisionType.rewardTypeUrl()))
        .map(r -> ProcessRewardTypeValue.builder()
            .rewardRevisionTypeValueId(r.getId())
            .rewardRevisionTypeValue(r.getValue())
            .rewardTypeFieldId(r.getRewardTypeField().getId())
            .rewardTypeFieldName(r.getRewardTypeField().getName())
            .rewardTypeFieldDataType(r.getRewardTypeField().getDataType())
            .rewardRevisionTypeId(r.getRewardRevisionType().getId())
            .rewardRevisionTypeInstant(r.getRewardRevisionType().isInstant())
            .build())
        .toList();

    rewardRevisionTypeValues.stream().forEach(r -> log.debug("RewardRevisionTypeValue :: " + r));
    processRewardTypeValues.stream().forEach(r -> log.debug("ProcessRewardTypeValue :: " + r));

    ProcessRewardRequest providerRequest = ProcessRewardRequest.builder()
        .player(context.getPlayer())
        .reward(rewardBOMapper.convertToRewardBO(rewardRevisionType.getRewardRevision().getReward()))
        .rewardRevision(rewardBOMapper.convertToRewardRevisionBO(rewardRevisionType.getRewardRevision()))
        .rewardType(rewardBOMapper.convertToRewardTypeBO(rewardRevisionType.getRewardType()))
        .rewardRevisionTypeGames(rewardService.findGamesByRewardRevisionType(rewardRevisionType.getId())
            .stream()
            .map(rewardBOMapper::convertToRewardRevisionTypeGame)
            .toList())
        .processRewardTypeValues(processRewardTypeValues)
        .playerRewardTypeHistoryId(context.getPlayerRewardTypeHistoryId())
        .domain(context.getDomain())
        //        .rewardRevisionTypeValues(
        //            rewardRevisionTypeValues.stream().map(r -> context.map(r, lithium.service.reward.client.dto.RewardRevisionTypeValue.class))
        //            .peek(r -> r.getRewardRevisionType().setRewardType(null))
        //            .collect(Collectors.toList()))
        //        .customValueInCents(context.getGiveRewardRequest().getCustomValueInCents()) //TODO: need to lookup from type.
        .build();
    log.debug("Going to reward provider. request: " + providerRequest);
    ProcessRewardResponse providerResponse = ProcessRewardResponse.builder().build();
    try {
      providerResponse = rewardProviderClient.processGiveReward(providerRequest);
    } catch (Exception e) {
      log.error("Could not process RewardType({}): {} on RewardProvider: {} for {}", rewardRevisionType.getRewardType().getId(), rewardRevisionType.getRewardType().getName(), rewardRevisionType.rewardTypeUrl(), context.getPlayer().guid(), e);
    }
    log.debug("Response from provider: " + providerResponse);
    return providerResponse;
  }

  public PlayerRewardComponentHistoryFE acceptReward(Long playerRewardTypeHistoryId, boolean accept, Locale locale, LithiumTokenUtil tokenUtil) {
    PlayerRewardTypeHistory playerRewardTypeHistory = playerRewardHistoryService.findPlayerRewardTypeHistoryId(playerRewardTypeHistoryId);

    if (playerRewardTypeHistory == null || playerRewardTypeHistory.getStatus() != PlayerRewardComponentStatus.PENDING_PLAYER_APPROVAL) {
      String message =  MessageFormat.format("Only existing rewards with status {0} can be accepted", PlayerRewardComponentStatus.PENDING_PLAYER_APPROVAL.status());
      log.error(message);
      throw new Status466InvalidRewardStateException(message);

    }
    if (!tokenUtil.guid().equalsIgnoreCase(playerRewardTypeHistory.getPlayerRewardHistory().getPlayer().guid())) {
      throw new Status466InvalidRewardStateException(
          MessageFormat.format("Invalid reward for user {0}", tokenUtil.guid()));
    }

    String rewardName = playerRewardTypeHistory.getPlayerRewardHistory().getRewardRevision().getName();

    if (!accept) {
      playerRewardTypeHistory.setStatus(PlayerRewardComponentStatus.DECLINED_BY_PLAYER);
      playerRewardHistoryService.savePlayerRewardTypeHistory(playerRewardTypeHistory);

      String comment = MessageFormat.format("Reward {0} was not granted to the player ({1}) because the player decline.", rewardName,
          tokenUtil.guid());

      log.info(comment);

      //check if all components are declined
      PlayerRewardHistory playerRewardHistory = playerRewardTypeHistory.getPlayerRewardHistory();
      List<PlayerRewardTypeHistory> rewardTypeHistories = playerRewardHistoryService.findPlayerRewardTypeHistoryByPlayerRewardHistory(playerRewardHistory);
      if(rewardTypeHistories.stream().allMatch(history -> history.getStatus() == PlayerRewardComponentStatus.DECLINED_BY_PLAYER)) {
        playerRewardHistoryService.changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.DECLINED_BY_PLAYER);
      }

      registerPlayerRewardTypeHistoryStatusChangelogs(playerRewardTypeHistory, "comment", comment, PlayerRewardComponentStatus.PENDING_PLAYER_APPROVAL.status(),
          PlayerRewardComponentStatus.DECLINED_BY_PLAYER.status(), SubCategory.REWARD_DECLINED);

      return playerRewardHistoryService.convertPlayerRewardTypeHistoryFE(playerRewardTypeHistory);
    }

    List<RewardRevisionTypeValueOverride> overrides = playerRewardHistoryValueService.getPlayerRewardHistoryValues(playerRewardTypeHistory)
        .stream()
        .map(v -> RewardRevisionTypeValueOverride.builder()
            .rewardRevisionTypeId(v.getPlayerRewardTypeHistory().getRewardRevisionType().getId())
            .rewardTypeFieldId(v.getRewardTypeField().getId())
            .value(v.getValue())
            .build())
        .toList();

    GiveRewardRequest giveRewardRequest = GiveRewardRequest.builder()
        .rewardRevisionTypeValueOverrides(overrides)
        .rewardId(playerRewardTypeHistory.getPlayerRewardHistory().getRewardRevision().getId())
        .rewardSource(playerRewardTypeHistory.getPlayerRewardHistory().getRewardSource())
        .playerGuid(playerRewardTypeHistory.getPlayerRewardHistory().getPlayer().getGuid())
        .build();

    lithium.service.reward.client.dto.User player = playerRewardHistoryService.map(playerRewardTypeHistory.getPlayerRewardHistory().getPlayer(),
        lithium.service.reward.client.dto.User.class);
    lithium.service.domain.client.objects.Domain externalDomain = domainService.externalDomain(player.domainName());
    lithium.service.reward.client.dto.Domain domain = lithium.service.reward.client.dto.Domain.builder()
        .currency(externalDomain.getCurrency())
        .name(externalDomain.getName())
        .build();

    GiveRewardContext context = GiveRewardContext.builder()
        .modelMapper(modelMapper)
        .giveRewardRequest(giveRewardRequest)
        .player(player)
        .domain(domain)
        .playerRewardTypeHistoryId(playerRewardTypeHistory.getId())
        .build();

    try {
      boolean rewardTypeFailed = processRewardType(playerRewardTypeHistory.getRewardRevisionType(), context, playerRewardTypeHistory);

      if(!rewardTypeFailed) {
        String comment = MessageFormat.format("Reward {0} granted after the player accepted the reward.", rewardName);

        registerPlayerRewardTypeHistoryStatusChangelogs(
              playerRewardTypeHistory,
              "comment",
              comment,
              PlayerRewardComponentStatus.PENDING_PLAYER_APPROVAL.status(),
              PlayerRewardComponentStatus.AWARDED.status(),
              SubCategory.REWARD_GRANT
        );
      }

      PlayerRewardHistory playerRewardHistory = playerRewardHistoryService.findPlayerRewardHistoryById(playerRewardTypeHistory.getPlayerRewardHistory().getId());
      playerRewardHistoryService.syncPlayerHistoryStatus(playerRewardHistory);

    } catch (Exception e) {
      log.error("Failed while processing reward.", e);
    }

    return playerRewardHistoryService.convertPlayerRewardTypeHistoryFE(
        playerRewardHistoryService.findPlayerRewardTypeHistoryId(playerRewardTypeHistory.getId()));
  }

  private RewardProviderClient rewardProviderClient(String rewardTypeUrl)
  throws Status505UnavailableException
  {
    RewardProviderClient rewardProviderClient;
    try {
      rewardProviderClient = clientFactory.target(RewardProviderClient.class, rewardTypeUrl, true);
    } catch (Exception e) {
      log.error("Provider unavailable", e);
      throw new Status505UnavailableException(e.getMessage());
    }
    return rewardProviderClient;
  }

  public void registerPlayerRewardTypeHistoryStatusChangelogs(PlayerRewardTypeHistory playerRewardTypeHistory, String action, String comment, String fromValue,
      String toValue, SubCategory subCategory)
  {
    User user = playerRewardTypeHistory.getPlayerRewardHistory().getPlayer();
    String authorGuid = lithium.service.user.client.objects.User.SYSTEM_GUID;
    LithiumTokenUtil tokenUtil = lithiumTokenUtilService.getUtilForCurrentPrincipal();

    if (tokenUtil != null) {
      authorGuid = tokenUtil.guid();
    }

    ChangeLogFieldChange changeLogFieldChange =ChangeLogFieldChange.builder().field("status").toValue(toValue).build();

    if (!StringUtil.isEmpty(fromValue)) {
      changeLogFieldChange.setFromValue(fromValue);
    }

    List<ChangeLogFieldChange> changes = Lists.newArrayList();
    changes.add(changeLogFieldChange);

    changeLogService.registerChangesForNotesWithFullNameAndDomain("user.reward.history", action,
        Long.parseLong(user.getOriginalId()), authorGuid, tokenUtil, comment, null, changes, Category.REWARDS,
          subCategory, 100, user.domainName());
  }
}
