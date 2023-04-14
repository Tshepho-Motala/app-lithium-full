package lithium.service.reward.service;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.reward.client.RewardProviderClient;
import lithium.service.reward.client.dto.PlayerRewardComponentStatus;
import lithium.service.reward.client.dto.PlayerRewardHistoryBO;
import lithium.service.reward.client.dto.PlayerRewardHistoryFE;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.client.dto.PlayerRewardTypeHistoryBO;
import lithium.service.reward.client.dto.PlayerRewardComponentHistoryFE;
import lithium.service.reward.client.dto.RewardGameFE;
import lithium.service.reward.client.dto.RewardSource;
import lithium.service.reward.client.exception.Status505UnavailableException;
import lithium.service.reward.config.ServiceRewardConfigurationProperties;
import lithium.service.reward.data.entities.PlayerRewardHistory;
import lithium.service.reward.data.entities.PlayerRewardHistory_;
import lithium.service.reward.data.entities.PlayerRewardTypeHistory;
import lithium.service.reward.data.entities.Reward;
import lithium.service.reward.data.entities.RewardRevision;
import lithium.service.reward.data.entities.RewardRevisionType;
import lithium.service.reward.data.entities.User;
import lithium.service.reward.data.repositories.PlayerRewardHistoryRepository;
import lithium.service.reward.data.repositories.PlayerRewardTypeHistoryRepository;
import lithium.service.reward.data.specifications.PlayerRewardHistorySpecification;
import lithium.service.reward.data.specifications.PlayerRewardTypeHistorySpecification;
import lithium.service.reward.dto.requests.RewardRequestFE;
import lithium.service.reward.object.PlayerRewardHistoryQuery;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlayerRewardHistoryService {

  @Autowired
  private ModelMapper modelMapper;
  @Autowired
  private PlayerRewardHistoryRepository playerRewardHistoryRepository;
  @Autowired
  private PlayerRewardTypeHistoryRepository playerRewardTypeHistoryRepository;
  @Autowired
  private RewardService rewardService;

  @Autowired
  private LithiumServiceClientFactory clientFactory;

  @Autowired
  private ChangeLogService changeLogService;

  @Autowired
  private LithiumTokenUtilService lithiumTokenUtilService;

  @Autowired
  private ServiceRewardConfigurationProperties configurationProperties;

  public PlayerRewardHistory createHistory(User player, RewardRevision rewardRevision, RewardSource rewardSource) {
    return playerRewardHistoryRepository.save(PlayerRewardHistory.builder()
        .player(player)
        .rewardRevision(rewardRevision)
        .rewardSource(rewardSource)
        .status(PlayerRewardHistoryStatus.PROCESSING)
        .build());
  }

  public PlayerRewardHistory markAwarded(PlayerRewardHistory playerRewardHistory, RewardRevision rewardRevision) {
    DateTime now = DateTime.now();
    DateTime expiry = now;
    switch (rewardRevision.getValidForGranularity()) {
      case GRANULARITY_HOUR -> expiry = expiry.plusHours(rewardRevision.getValidFor());
      case GRANULARITY_DAY -> expiry = expiry.plusDays(rewardRevision.getValidFor());
      case GRANULARITY_WEEK -> expiry = expiry.plusWeeks(rewardRevision.getValidFor());
      case GRANULARITY_MONTH -> expiry = expiry.plusMonths(rewardRevision.getValidFor());
      case GRANULARITY_YEAR -> expiry = expiry.plusYears(rewardRevision.getValidFor());
      case GRANULARITY_TOTAL -> expiry = expiry.plusYears(199);
    }

    playerRewardHistory.setStatus(PlayerRewardHistoryStatus.AWARDED);
    playerRewardHistory.setAwardedDate(playerRewardHistory.getAwardedDate() == null ? now.toDate() : playerRewardHistory.getAwardedDate());
    playerRewardHistory.setExpiryDate(playerRewardHistory.getExpiryDate() == null ? expiry.toDate() : playerRewardHistory.getExpiryDate());
    return saveHistory(playerRewardHistory);
  }

  public PlayerRewardHistory markAsRedeemed(PlayerRewardHistory playerRewardHistory) {
    if (playerRewardHistory.getAwardedDate() == null) {
      markAwarded(playerRewardHistory, playerRewardHistory.getRewardRevision());
    }

    playerRewardHistory.setStatus(PlayerRewardHistoryStatus.REDEEMED);
    playerRewardHistory.setRedeemedDate(new DateTime(DateTimeZone.UTC).toDate());
    saveHistory(playerRewardHistory);

    return playerRewardHistory;
  }

  public PlayerRewardHistory changeStatus(PlayerRewardHistory playerRewardHistory, PlayerRewardHistoryStatus status) {
    playerRewardHistory.setStatus(status);

    if(status == PlayerRewardHistoryStatus.AWARDED && playerRewardHistory.getAwardedDate() == null) {
      playerRewardHistory.setAwardedDate(new Date());
    }

    if(status == PlayerRewardHistoryStatus.REDEEMED && playerRewardHistory.getRedeemedDate() == null) {
      playerRewardHistory.setRedeemedDate(new Date());
    }

    return saveHistory(playerRewardHistory);
  }

  public PlayerRewardTypeHistory changeStatus(PlayerRewardTypeHistory playerRewardTypeHistory, PlayerRewardComponentStatus status) {
    playerRewardTypeHistory.setStatus(status);

    if (status == PlayerRewardComponentStatus.AWARDED && playerRewardTypeHistory.getAwardedDate() == null) {
      playerRewardTypeHistory.setAwardedDate(new Date());
    }
    return savePlayerRewardTypeHistory(playerRewardTypeHistory);
  }

  private PlayerRewardHistory saveHistory(PlayerRewardHistory playerRewardHistory) {
    return playerRewardHistoryRepository.save(playerRewardHistory);
  }

  public PlayerRewardTypeHistory createTypeHistory(PlayerRewardHistory playerRewardHistory, RewardRevisionType rewardRevisionType) {
    return playerRewardTypeHistoryRepository.save(PlayerRewardTypeHistory.builder()
        .createdDate(new Date())
        .playerRewardHistory(playerRewardHistory)
        .rewardRevisionType(rewardRevisionType)
        .status((rewardRevisionType.isInstant()) ? PlayerRewardComponentStatus.PROCESSING : PlayerRewardComponentStatus.PENDING_PLAYER_APPROVAL)
        .build());
  }

  public PlayerRewardTypeHistory findPlayerRewardTypeHistory(RewardRevisionType rewardRevisionType, PlayerRewardHistory playerRewardHistory) {
    return playerRewardTypeHistoryRepository.findByRewardRevisionTypeAndPlayerRewardHistory(rewardRevisionType, playerRewardHistory);
  }

  public PlayerRewardTypeHistory savePlayerRewardTypeHistory(PlayerRewardTypeHistory playerRewardTypeHistory) {
    return playerRewardTypeHistoryRepository.save(playerRewardTypeHistory);
  }

  public PlayerRewardTypeHistory findByRewardTypeReference(String rewardTypeReference) {
    return playerRewardTypeHistoryRepository.findByReferenceId(rewardTypeReference);
  }

  public lithium.service.reward.client.dto.PlayerRewardTypeHistory findByRewardTypeReferenceAndConvert(String rewardTypeReference) {
    PlayerRewardTypeHistory findByRewardTypeReference = playerRewardTypeHistoryRepository.findByReferenceId(rewardTypeReference);
    if (findByRewardTypeReference == null) {
      return null;
    }
    return map(findByRewardTypeReference, lithium.service.reward.client.dto.PlayerRewardTypeHistory.class);
  }

  public lithium.service.reward.client.dto.PlayerRewardTypeHistory findTypeHistoryById(Long playerRewardTypeHistoryId) {
    PlayerRewardTypeHistory findByRewardTypeReference = findPlayerRewardTypeHistoryId(playerRewardTypeHistoryId);
    if (findByRewardTypeReference == null) {
      return null;
    }
    return map(findByRewardTypeReference, lithium.service.reward.client.dto.PlayerRewardTypeHistory.class);
  }

  public PlayerRewardHistory findPlayerRewardHistoryById(Long playerRewardHistoryId) {
    return playerRewardHistoryRepository.findOne(playerRewardHistoryId);
  }
  public List<PlayerRewardTypeHistory> findPlayerRewardTypeHistoryByPlayerRewardHistory(PlayerRewardHistory playerRewardHistory) {
    return playerRewardTypeHistoryRepository.findByPlayerRewardHistory(playerRewardHistory);
  }

  public PlayerRewardTypeHistory findPlayerRewardTypeHistoryId(Long playerRewardTypeHistoryId) {
    return playerRewardTypeHistoryRepository.findOne(playerRewardTypeHistoryId);
  }

  public <D> D map(Object source, Class<D> destinationType) {
    return modelMapper.map(source, destinationType);
  }

  public lithium.service.reward.client.dto.PlayerRewardTypeHistory updateCounter(Long playerRewardTypeHistoryId) {
    PlayerRewardTypeHistory prth = playerRewardTypeHistoryRepository.findOne(playerRewardTypeHistoryId);
    prth.setValueUsed((prth.getValueUsed() == null) ? BigDecimal.ONE : prth.getValueUsed().add(BigDecimal.ONE));

    BigDecimal valueGiven = Optional.ofNullable(prth.getValueGiven()).orElse(BigDecimal.ZERO);

    if (prth.getValueUsed().compareTo(valueGiven) >=  0) {
        prth.setValueUsed(valueGiven);
        prth.setStatus(PlayerRewardComponentStatus.REDEEMED);
        prth.setRedeemedDate(new Date());
    }
    prth = playerRewardTypeHistoryRepository.save(prth);
    syncPlayerHistoryStatus(prth.getPlayerRewardHistory());
    return map(prth, lithium.service.reward.client.dto.PlayerRewardTypeHistory.class);
  }

  public Page<PlayerRewardHistory> findExpiredRewards(PageRequest pageRequest) {
    return playerRewardHistoryRepository.findByExpiryDateIsBeforeAndStatusIsIn(DateTime.now().toDate(), Arrays.asList(PlayerRewardHistoryStatus.AWARDED, PlayerRewardHistoryStatus.PARTIALLY_AWARDED, PlayerRewardHistoryStatus.PENDING), pageRequest);
  }

  @TimeThisMethod
  public void processRewardCancellation(RewardRevision rewardRevision) {
    int page = 0;
    boolean hasMore = true;
    while (hasMore) {
      PageRequest pageRequest = PageRequest.of(page, configurationProperties.getPlayerRewardCancelPageSize());
      Page<PlayerRewardHistory> findByRewardRevision = playerRewardHistoryRepository.findByRewardRevisionAndStatusIsNotIn(rewardRevision, Arrays.asList(PlayerRewardHistoryStatus.CANCELLED, PlayerRewardHistoryStatus.FAILED, PlayerRewardHistoryStatus.REDEEMED), pageRequest);
      log.debug("Processing Reward Cancellation Found {} entries. Page {} of {}", findByRewardRevision.getContent().size(), findByRewardRevision.getNumber(), findByRewardRevision.getTotalPages());
      SW.start("processRewardCancellation_pagerequest_" + page);

      for (PlayerRewardHistory playerRewardHistory: findByRewardRevision.getContent()) {
        AtomicBoolean allCancelsSucceeded = new AtomicBoolean(true);
        log.debug("PlayerRewardHistory: {}", playerRewardHistory);

        if (!cancelPlayerReward(playerRewardHistory)) {
          allCancelsSucceeded.set(false);
        }

        if (allCancelsSucceeded.get()) {
          changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.CANCELLED);
        }
      }
      SW.stop();
      page++;
      if (!findByRewardRevision.hasNext()) {
        hasMore = false;
      }
    }
    log.info("All player rewards have been cancelled for {}", rewardRevision.toShortString());
  }

  private boolean processRewardTypeCancellation(PlayerRewardTypeHistory playerRewardTypeHistory)
  {

    if (playerRewardTypeHistory == null){
      return false;
    }

    log.debug("RevisionType: {}", playerRewardTypeHistory.getRewardRevisionType());

    RewardProviderClient rewardProviderClient = rewardProviderClient(playerRewardTypeHistory.getRewardRevisionType().rewardTypeUrl());
    PlayerRewardHistory playerRewardHistory = playerRewardTypeHistory.getPlayerRewardHistory();

    if(playerRewardTypeHistory.getStatus() == PlayerRewardComponentStatus.CANCELLED) {
      log.debug("Cannot cancel reward component with id {} because the reward component is already cancelled", playerRewardTypeHistory.getId());
      return false;
    }

    if (playerRewardTypeHistory.getStatus() == PlayerRewardComponentStatus.PENDING_PLAYER_APPROVAL) {
      //A reward in this status does not need to be cancelled on external provider since it is not yet granted
      playerRewardTypeHistory.setStatus(PlayerRewardComponentStatus.CANCELLED);
      playerRewardTypeHistoryRepository.save(playerRewardTypeHistory);
      return true;
    }

    if (playerRewardTypeHistory.getStatus() == PlayerRewardComponentStatus.REDEEMED) {
      log.debug("Cannot cancel reward component with id {} because the reward component is already redeemed", playerRewardTypeHistory.getId());
      return false;
    }

    if (playerRewardTypeHistory.getReferenceId() == null) {
      return false;
    }


    CancelRewardRequest cancelRewardRequest = CancelRewardRequest.builder()
        .domainName(playerRewardHistory.getPlayer().domainName())
        .playerGuid(playerRewardHistory.getPlayer().guid())
        .referenceId(playerRewardTypeHistory.getReferenceId())
        .playerRewardTypeHistoryId(playerRewardTypeHistory.getId())
        .build();
    log.debug("Going to reward provider. request: " + cancelRewardRequest);
    CancelRewardResponse providerResponse = null;
    try {
      providerResponse = rewardProviderClient.processCancelReward(cancelRewardRequest);
      if (providerResponse.isSuccess()) {
        String fromStatus = playerRewardTypeHistory.getStatus().status();
        playerRewardTypeHistory = changeStatus(playerRewardTypeHistory, PlayerRewardComponentStatus.CANCELLED);

        String authorGuid = lithium.service.user.client.objects.User.SYSTEM_GUID;
        String authorFullName = lithium.service.user.client.objects.User.SYSTEM_FULL_NAME;

        LithiumTokenUtil tokenUtil = lithiumTokenUtilService.getUtilForCurrentPrincipal();

        if (tokenUtil != null) {
          authorFullName = tokenUtil.userLegalName();
          authorGuid = tokenUtil.guid();
        }

        List<ChangeLogFieldChange> changes = Lists.newArrayList(
                ChangeLogFieldChange.builder().field("status").fromValue(fromStatus).toValue(PlayerRewardComponentStatus.CANCELLED.status()).build());
        String comment = MessageFormat.format("Player reward component {0} with Id {1} from reward {2} was cancelled",
                playerRewardTypeHistory.getRewardRevisionType().getRewardType().getName(), playerRewardTypeHistory.getId(), playerRewardHistory.getRewardRevision().getName());

        changeLogService.registerChangesWithDomainAndFullName("user.reward.history", "edit", Long.parseLong(playerRewardHistory.getPlayer().getOriginalId()), authorGuid, comment, null, changes, Category.REWARDS, SubCategory.REWARD_CANCELLED, 100, playerRewardHistory.getPlayer().domainName(), authorFullName);
        return (Objects.equals(playerRewardTypeHistory.getStatus(), PlayerRewardComponentStatus.CANCELLED));
      }
    } catch (Exception e) {
      throw new RuntimeException(e); //TODO: Impl proper exception
    }
    log.debug("Response from provider: " + providerResponse);
    return false;
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

  public Page<PlayerRewardHistoryFE> findPlayerRewardHistoryWithStatus(LithiumTokenUtil tokenUtil, int pageSize, int page,
      PlayerRewardHistoryStatus status)
  {
    Sort sort = Sort.by(Sort.Direction.DESC, PlayerRewardHistory_.awardedDate.getName());
    PageRequest pageRequest = PageRequest.of(page, pageSize, sort);
    Specification<PlayerRewardHistory> spec = Specification.where(PlayerRewardHistorySpecification.statusIn(Lists.newArrayList(status)))
        .and(Specification.where(PlayerRewardHistorySpecification.player(tokenUtil.guid())));
    Page<PlayerRewardHistory> playerRewardHistories = playerRewardHistoryRepository.findAll(spec, pageRequest);
    List<PlayerRewardHistoryFE> converted = playerRewardHistories.getContent().stream().map(this::convertPlayerRewardHistoryFE).toList();
    return new SimplePageImpl<>(converted, page, pageSize, playerRewardHistories.getTotalElements());
  }

  public List<PlayerRewardHistoryFE> listPlayerRewardHistoryWithStatus(LithiumTokenUtil tokenUtil, PlayerRewardHistoryStatus status)
  {
    Specification<PlayerRewardHistory> spec = Specification.where(PlayerRewardHistorySpecification.statusIn(Lists.newArrayList(status)))
        .and(Specification.where(PlayerRewardHistorySpecification.player(tokenUtil.guid())));

    Pageable pageable = PageRequest.of(0, configurationProperties.getPlayerRewardHistoryPageSize());
    List<PlayerRewardHistory> playerRewardHistories = playerRewardHistoryRepository.findAll(spec, pageable).getContent();
    return playerRewardHistories.stream().map(this::convertPlayerRewardHistoryFE).toList();
  }

  private String convertDateToString(Date date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    return (date != null) ? ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")).format(formatter) : "";
  }

  private PlayerRewardHistoryFE convertPlayerRewardHistoryFE(PlayerRewardHistory playerRewardHistory) {
    List<PlayerRewardTypeHistory> types = playerRewardTypeHistoryRepository.findByPlayerRewardHistory(playerRewardHistory);

    return PlayerRewardHistoryFE.builder()
        .id(playerRewardHistory.getId())
        .awardedDate(convertDateToString(playerRewardHistory.getAwardedDate()))
        .redeemedDate(convertDateToString(playerRewardHistory.getRedeemedDate()))
        .expiryDate(convertDateToString(playerRewardHistory.getExpiryDate()))
        .playerGuid(playerRewardHistory.getPlayer().guid())
        .rewardName(playerRewardHistory.getRewardRevision().getName())
        .rewardCode(playerRewardHistory.getRewardRevision().getCode())
        .status(playerRewardHistory.getStatus().status())
        .rewardComponents(types.stream().map(this::convertPlayerRewardTypeHistoryFE).toList())
        .build();
  }

  protected PlayerRewardComponentHistoryFE convertPlayerRewardTypeHistoryFE(PlayerRewardTypeHistory playerRewardTypeHistory) {
    List<RewardGameFE> games = rewardService.findGamesByRewardRevisionType(playerRewardTypeHistory.getRewardRevisionType().getId())
            .stream().map(game -> RewardGameFE.builder()
                    .guid(game.getGuid())
                    .name(game.getGameName())
                    .build())
            .toList();


    PlayerRewardComponentHistoryFE playerRewardComponentHistoryFE = PlayerRewardComponentHistoryFE.builder()
            .id(playerRewardTypeHistory.getId())
            .status(playerRewardTypeHistory.getStatus().status())
            .rewardComponentName(playerRewardTypeHistory.getRewardRevisionType().getRewardType().getName())
            .playerRewardHistoryId(playerRewardTypeHistory.getPlayerRewardHistory().getId())
            .playerGuid(playerRewardTypeHistory.getPlayerRewardHistory().getPlayer().guid())
            .awardedOn(convertDateToString(playerRewardTypeHistory.getAwardedDate()))
            .created(convertDateToString(playerRewardTypeHistory.getCreatedDate()))
            .updated(convertDateToString(playerRewardTypeHistory.getUpdatedDate()))
            .amountGiven(Optional.ofNullable(playerRewardTypeHistory.getValueGiven()).orElse(BigDecimal.ZERO).doubleValue())
            .amountInCents(Optional.ofNullable(playerRewardTypeHistory.getValueInCents()).orElse(BigDecimal.ZERO).doubleValue())
            .amountUsed(Optional.ofNullable(playerRewardTypeHistory.getValueUsed()).orElse(BigDecimal.ZERO).doubleValue())
            .gameList(games)
            .description(playerRewardTypeHistory.getRewardRevisionType().getRewardRevision().getDescription())
            .build();

    return playerRewardComponentHistoryFE;
  }

  private PlayerRewardHistoryBO convertPlayerRewardHistoryBO(PlayerRewardHistory playerRewardHistory) {
    List<PlayerRewardTypeHistory> types = playerRewardTypeHistoryRepository.findByPlayerRewardHistory(playerRewardHistory);
    boolean cancel = List.of(PlayerRewardHistoryStatus.AWARDED,PlayerRewardHistoryStatus.PENDING).contains(playerRewardHistory.getStatus());

    return PlayerRewardHistoryBO.builder()
        .id(playerRewardHistory.getId())
        .awardedDate(convertDateToString(playerRewardHistory.getAwardedDate()))
        .redeemedDate(convertDateToString(playerRewardHistory.getRedeemedDate()))
        .expiryDate(convertDateToString(playerRewardHistory.getExpiryDate()))
        .playerGuid(playerRewardHistory.getPlayer().guid())
        .rewardName(playerRewardHistory.getRewardRevision().getName())
        .rewardCode(playerRewardHistory.getRewardRevision().getCode())
        .status(playerRewardHistory.getStatus().status().toUpperCase())
        .rewardTypes(types.stream().map(this::convertPlayerRewardTypeHistoryBO).toList())
        .cancellable(cancel)
        .build();
  }

  protected PlayerRewardTypeHistoryBO convertPlayerRewardTypeHistoryBO(PlayerRewardTypeHistory playerRewardTypeHistory) {
    boolean cancel = List.of(PlayerRewardComponentStatus.PENDING_PLAYER_APPROVAL, PlayerRewardComponentStatus.AWARDED).contains(playerRewardTypeHistory.getStatus());
    return PlayerRewardTypeHistoryBO.builder()
        .id(playerRewardTypeHistory.getId())
        .status(playerRewardTypeHistory.getStatus().status().toUpperCase())
        .rewardTypeName(playerRewardTypeHistory.getRewardRevisionType().getRewardType().getName())
        .playerRewardHistoryId(playerRewardTypeHistory.getPlayerRewardHistory().getId())
        .rewardName(playerRewardTypeHistory.getRewardRevisionType().getRewardRevision().getName())
        .rewardCode(playerRewardTypeHistory.getRewardRevisionType().getRewardRevision().getCode())
        .playerGuid(playerRewardTypeHistory.getPlayerRewardHistory().getPlayer().guid())
        .awardedOn(convertDateToString(playerRewardTypeHistory.getAwardedDate()))
        .created(convertDateToString(playerRewardTypeHistory.getCreatedDate()))
        .typeCounter((playerRewardTypeHistory.getValueUsed() != null) ? playerRewardTypeHistory.getValueUsed() : BigDecimal.ZERO)
        .cancellable(cancel)
        .build();
  }

  public List<PlayerRewardTypeHistory> findPlayerRewardTypeHistoryWithStatuses(List<PlayerRewardComponentStatus> statuses) {
    return playerRewardTypeHistoryRepository.findAll(PlayerRewardTypeHistorySpecification.withStatuses(statuses));
  }

  public List<PlayerRewardTypeHistory> findPlayerRewardTypeHistoryWithStatuses(PlayerRewardHistory playerRewardHistory, List<PlayerRewardComponentStatus> statuses) {
    return playerRewardTypeHistoryRepository.findByPlayerRewardHistoryAndStatusIn(playerRewardHistory, statuses);
  }


  public Page<PlayerRewardHistoryBO> findAllPlayerRewardHistoryRecordsPaged(PlayerRewardHistoryQuery query, DataTableRequest request) {
    Specification<PlayerRewardHistory> spec = Specification.where(PlayerRewardHistorySpecification.domain(query.getDomainName()));
    if (StringUtils.isNotEmpty(query.getPlayerGuid())) {
      spec = spec.and(PlayerRewardHistorySpecification.player(query.getPlayerGuid()));
    }
    if (Optional.ofNullable(query.getHistoryStatuses()).isPresent()) {
      spec = spec.and(PlayerRewardHistorySpecification.historyStatuses(query.getHistoryStatuses()));
    }
    if (StringUtils.isNotEmpty(query.getRewardCode())) {
      spec = spec.and(PlayerRewardHistorySpecification.rewardCode(query.getRewardCode()));
    }

    if (Objects.nonNull(query.getRewardId())) {
      spec = spec.and(PlayerRewardHistorySpecification.rewardId(query.getRewardId()));
    }

    if (Objects.nonNull(query.getAwardedDateFrom())) {
      spec = spec.and(PlayerRewardHistorySpecification.awardedDateFrom(query.getAwardedDateFrom()));
    }
    if (Objects.nonNull(query.getAwardedDateTo())) {
      spec = spec.and(PlayerRewardHistorySpecification.awardedDateTo(query.getAwardedDateTo()));
    }
    if (Objects.nonNull(query.getRedeemedDateFrom())) {
      spec = spec.and(PlayerRewardHistorySpecification.redeemedDateFrom(query.getRedeemedDateFrom()));
    }
    if (Objects.nonNull(query.getRedeemedDateTo())) {
      spec = spec.and(PlayerRewardHistorySpecification.redeemedDateTo(query.getRedeemedDateTo()));
    }
    if (Objects.nonNull(query.getExpiryDateFrom())) {
      spec = spec.and(PlayerRewardHistorySpecification.expiryDateFrom(query.getExpiryDateFrom()));
    }
    if (Objects.nonNull(query.getExpiryDateTo())) {
      spec = spec.and(PlayerRewardHistorySpecification.expiryDateTo(query.getExpiryDateTo()));
    }
    if(request.getPageRequest().getSort().isUnsorted()){
      request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber(), request.getPageRequest().getPageSize(), Sort.by(Sort.Direction.DESC, "awardedDate")));
    }
    Page<PlayerRewardHistory> findAllPlayerRewardHistoryRecordsPaged = playerRewardHistoryRepository.findAll(spec, request.getPageRequest());
    List<PlayerRewardHistoryBO> converted = findAllPlayerRewardHistoryRecordsPaged.getContent()
        .stream()
        .map(this::convertPlayerRewardHistoryBO)
        .toList();
    return new SimplePageImpl<>(converted, request.getPageRequest().getPageNumber(), request.getPageRequest().getPageSize(),
        findAllPlayerRewardHistoryRecordsPaged.getTotalElements());
  }

  @TimeThisMethod
  public boolean cancelPlayerReward(String playerGuid, Long playerRewardHistoryId) {
    PlayerRewardHistory history = playerRewardHistoryRepository.findOne(playerRewardHistoryId);
    log.debug("PlayerRewardHistory: {}", history);

    if (history == null || !history.getPlayer().guid().equalsIgnoreCase(playerGuid)) {
      log.warn("Cannot cancel player reward {}, it is either it does not exist or it does not belong to player {}", playerRewardHistoryId, playerGuid);
      return false;
    }
    return cancelPlayerReward(history);
  }

  @TimeThisMethod
  public boolean cancelPlayerReward(PlayerRewardHistory history) {

    if (history == null) {
      return false;
    }

    AtomicBoolean allCancelsSucceeded = new AtomicBoolean(true);
    List<PlayerRewardTypeHistory> playerRewardComponents = playerRewardTypeHistoryRepository.findByPlayerRewardHistory(history);
    log.debug("Player Reward Type Cancellation Found {} entries.", playerRewardComponents.size());

    for (PlayerRewardTypeHistory component: playerRewardComponents) {
      try {
        if (!processRewardTypeCancellation(component)) {
          allCancelsSucceeded.set(false);
        }
      }
      catch (Throwable e) {
        allCancelsSucceeded.set(false);
        log.error("Failed to cancel player reward component {}, for player {}, reason: {}", component, history.getPlayer().guid(),  e.getMessage(), e);
      }
    }
    Reward reward = history.getRewardRevision().getReward();
    SW.start("cancelPlayerReward_"+reward.getId()+"_"+history.getPlayer().guid());

    if (allCancelsSucceeded.get()) {
      Optional<PlayerRewardHistory> fresh = playerRewardHistoryRepository.findById(history.getId());

      if (fresh.isPresent()) {
        changeStatus(fresh.get(), PlayerRewardHistoryStatus.CANCELLED);
      }

    }

    SW.stop();
    return allCancelsSucceeded.get();
  }

  public boolean cancelPlayerRewardType(String playerGuid, Long playerRewardTypeHistoryId, boolean updateParent) {
    PlayerRewardTypeHistory playerRewardTypeHistory = findPlayerRewardTypeHistoryId(playerRewardTypeHistoryId);
    if (!playerRewardTypeHistory.getPlayerRewardHistory().getPlayer().guid().equalsIgnoreCase(playerGuid)) {
      log.warn("Cannot cancel reward component {} because it does not belong to player {}", playerRewardTypeHistory.getId(), playerGuid);
      return false;
    }
    boolean success = processRewardTypeCancellation(playerRewardTypeHistory);

    if(success && updateParent) {
      syncPlayerHistoryStatus(playerRewardTypeHistory.getPlayerRewardHistory());
    }
    return success;
  }

  public Page<PlayerRewardComponentHistoryFE> findRewardComponents(RewardRequestFE rewardRequestFE, LithiumTokenUtil util) {
    PlayerRewardComponentStatus status = PlayerRewardComponentStatus.fromStatus(rewardRequestFE.getStatus());
    Integer pageSize = Optional.ofNullable(rewardRequestFE.getPageSize()).orElse(1000);
    Integer page = Optional.ofNullable(rewardRequestFE.getPage()).orElse(0);

    Specification<PlayerRewardTypeHistory> spec = PlayerRewardTypeHistorySpecification.player(util.guid());

    if (rewardRequestFE.active) {
      spec = PlayerRewardTypeHistorySpecification.withStatuses(List.of(PlayerRewardComponentStatus.AWARDED));
    } else if(status != null) {
      spec = PlayerRewardTypeHistorySpecification.withStatuses(List.of(status));
    }

    Pageable pageable = PageRequest.of(page, pageSize);

    return  playerRewardTypeHistoryRepository.findAll(spec, pageable)
            .map(this::convertPlayerRewardTypeHistoryFE);
  }

  public List<PlayerRewardTypeHistory> filterByStatuses(List<PlayerRewardTypeHistory> playerRewardTypeHistories, List<PlayerRewardComponentStatus> statuses) {
    return playerRewardTypeHistories.stream().filter(history -> statuses.contains(history.getStatus()))
            .toList();
  }

  public void syncPlayerHistoryStatus(PlayerRewardHistory playerRewardHistory) {
    List<PlayerRewardTypeHistory> playerRewardTypeHistories = findPlayerRewardTypeHistoryByPlayerRewardHistory(playerRewardHistory);
    List<PlayerRewardTypeHistory> playerRewardTypeHistoryPending = filterByStatuses(playerRewardTypeHistories, List.of(PlayerRewardComponentStatus.PENDING_PLAYER_APPROVAL));
    List<PlayerRewardTypeHistory> playerRewardTypeHistoryCancelled = filterByStatuses(playerRewardTypeHistories, List.of(PlayerRewardComponentStatus.CANCELLED));
    List<PlayerRewardTypeHistory> playerRewardTypeHistoryFailed = filterByStatuses(playerRewardTypeHistories, List.of(PlayerRewardComponentStatus.FAILED_EXTERNALLY, PlayerRewardComponentStatus.FAILED_INTERNALLY));

    if (playerRewardTypeHistoryFailed.size() == playerRewardTypeHistories.size()) {
      changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.FAILED);
      return;
    }

    if (playerRewardTypeHistoryCancelled.size() == playerRewardTypeHistories.size()) {
      changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.CANCELLED);
      return;
    }

    if (playerRewardTypeHistoryPending.isEmpty()) {
      //No reward types are in pending state anymore, lets check if all have been awarded.
      List<PlayerRewardTypeHistory> playerRewardTypeHistoryAwarded = filterByStatuses(playerRewardTypeHistories, List.of(PlayerRewardComponentStatus.AWARDED));

      if (!playerRewardTypeHistoryAwarded.isEmpty()) {
        if (playerRewardTypeHistoryAwarded.size() == playerRewardTypeHistories.size() && playerRewardHistory.getStatus() != PlayerRewardHistoryStatus.AWARDED) {
          //all reward types are in awarded state, lets update the playerRewardHistory
          markAwarded(playerRewardHistory, playerRewardHistory.getRewardRevision());
        } else {
          changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.AWARDED);
        }
      } else {
        changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.PARTIALLY_AWARDED);
      }

    } else {
      changeStatus(playerRewardHistory, PlayerRewardHistoryStatus.PARTIALLY_AWARDED);
    }

    playerRewardHistory = findPlayerRewardHistoryById(playerRewardHistory.getId());

    List<PlayerRewardTypeHistory> playerRewardTypeHistoryRedeemed = filterByStatuses(playerRewardTypeHistories, List.of(PlayerRewardComponentStatus.REDEEMED))
            .stream()
            .sorted(Comparator.comparing(PlayerRewardTypeHistory::getUpdatedDate, Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
            .toList();

    if (!playerRewardTypeHistoryRedeemed.isEmpty() && playerRewardTypeHistoryRedeemed.size() == playerRewardTypeHistories.size()) {
      playerRewardHistory.setRedeemedDate(new DateTime().toDate());

      if (playerRewardHistory.getStatus() != PlayerRewardHistoryStatus.AWARDED) {
        //A reward needs to be full awarded before it cannot be redeemed
        markAwarded(playerRewardHistory, playerRewardHistory.getRewardRevision());
      }
      playerRewardHistory.setStatus(PlayerRewardHistoryStatus.REDEEMED);
      playerRewardHistory.setRedeemedDate(playerRewardTypeHistoryRedeemed.get(0).getRedeemedDate()); //get the redeemed date of the last component
      saveHistory(playerRewardHistory);
    }
  }
}
