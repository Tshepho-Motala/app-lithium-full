package lithium.service.user.services;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.PlayTimeLimitPubSubDTO;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.service.user.data.dto.PlayerPlaytimeLimitConfigRequest;
import lithium.service.user.data.entities.Granularity;
import lithium.service.user.data.entities.Period;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV1DataMigrationProgress;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2Config;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2ConfigRevision;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2Entry;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserActiveSessionsMetadata;
import lithium.service.user.data.entities.playtimelimit.LimitType;
import lithium.service.user.data.entities.playtimelimit.PlayerPlayTimeLimit;
import lithium.service.user.data.repositories.GranularityRepository;
import lithium.service.user.data.repositories.PlayerPlaytimeLimitV1DataMigrationProgressRepository;
import lithium.service.user.data.repositories.PlayerPlaytimeLimitV2ConfigRepository;
import lithium.service.user.data.repositories.PlayerPlaytimeLimitV2ConfigRevisionRepository;
import lithium.service.user.data.repositories.PlayerPlaytimeLimitV2EntryRepository;
import lithium.service.user.data.repositories.PlayerTimeLimitRepository;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class PlaytimeLimitsV2Service {

  private final UserService userService;
  @Autowired
  private PlaytimeLimitsV2Service self;
  private final PlayerPlaytimeLimitV2ConfigRepository configRepository;
  private final DomainService domainService;
  private final ChangeLogService changeLogService;
  private final PlayerPlaytimeLimitV2ConfigRevisionRepository configRevisionRepository;
  private final GranularityRepository granularityRepository;
  private final PlayerPlaytimeLimitV1DataMigrationProgressRepository migrationProgressRepository;
  private final PlayerTimeLimitRepository playerTimeLimitRepository;
  private final PlayerPlaytimeLimitV2EntryRepository limitEntryRepository;

  private final UserApiInternalClientService userApiInternalClientService;
  private final PubSubUserService pubSubUserService;
  private final UserActiveSessionsMetadataService userActiveSessionsMetadataService;
  private final MessageSource messageSource;
  private final ModelMapper modelMapper;
  private final CachingDomainClientService cachingDomainClientService;

  private final PeriodService periodService;


  @Autowired
  public PlaytimeLimitsV2Service(UserService userService, PlayerPlaytimeLimitV2ConfigRepository configRepository,
      DomainService domainService, ChangeLogService changeLogService,
      PlayerPlaytimeLimitV2ConfigRevisionRepository configRevisionRepository, GranularityRepository granularityRepository,
      PlayerPlaytimeLimitV1DataMigrationProgressRepository migrationProgressRepository, PlayerTimeLimitRepository playerTimeLimitRepository,
      PlayerPlaytimeLimitV2EntryRepository limitEntryRepository, UserApiInternalClientService userApiInternalClientService,
      PubSubUserService pubSubUserService, UserActiveSessionsMetadataService userActiveSessionsMetadataService,
      MessageSource messageSource, ModelMapper modelMapper, CachingDomainClientService cachingDomainClientService,
      PeriodService periodService) {
    this.userService = userService;
    this.configRepository = configRepository;
    this.domainService = domainService;
    this.changeLogService = changeLogService;
    this.configRevisionRepository = configRevisionRepository;
    this.granularityRepository = granularityRepository;
    this.migrationProgressRepository = migrationProgressRepository;
    this.playerTimeLimitRepository = playerTimeLimitRepository;
    this.limitEntryRepository = limitEntryRepository;
    this.userApiInternalClientService = userApiInternalClientService;
    this.pubSubUserService = pubSubUserService;
    this.userActiveSessionsMetadataService = userActiveSessionsMetadataService;
    this.messageSource = messageSource;
    this.modelMapper = modelMapper;
    this.cachingDomainClientService = cachingDomainClientService;
    this.periodService = periodService;
  }

  public PlayerPlaytimeLimitV2Config getPlayerConfiguration(String playerGuid)
      throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException {
    User user = userService.findFromGuid(playerGuid);

    if (ObjectUtils.isEmpty(user)) {
      throw new Status414UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_GUID_NOT_FOUND",
          new Object[]{new lithium.service.translate.client.objects.Domain(playerGuid.split("/")[0]), playerGuid},
          "User with guid={0} not found.", LocaleContextHolder.getLocale()));

    }

    Optional<PlayerPlaytimeLimitV2Config> limitConfig = configRepository.findByUser(user);

    return limitConfig.orElseThrow(
        () -> new Status438PlayTimeLimitConfigurationNotFoundException(
            messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.PLAYTIME_CONFIGURATION_NOT_FOUND",
                new Object[]{new lithium.service.translate.client.objects.Domain(playerGuid.split("/")[0]), playerGuid},
                "Playtime Limits Configuration for player: ".concat(playerGuid).concat(" not found"), LocaleContextHolder.getLocale())));
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = {ObjectOptimisticLockingFailureException.class}, exclude = {
      Exception.class})
  public PlayerPlaytimeLimitV2Config setPlayerConfiguration(PlayerPlaytimeLimitConfigRequest request, LithiumTokenUtil tokenUtil)
      throws Status414UserNotFoundException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException {

    User user = userService.findById(request.getUserId());
    User author = user;

    if (ObjectUtils.isEmpty(user)) {
      throw new Status414UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_ID_NOT_FOUND",
          new Object[]{new lithium.service.translate.client.objects.Domain(tokenUtil.domainName()), request.getUserId()},
          "User with id={0} not found.", LocaleContextHolder.getLocale()));
    }

    if (!ObjectUtils.isEmpty(tokenUtil)) {
      author = userService.findById(tokenUtil.getJwtUser().getId());
    }

    Optional<Granularity> granularity = granularityRepository.findById(request.getGranularity());

    if (granularity.isEmpty() || isInActiveGranularity(granularity.get().getType())) {
      throw new Status426InvalidParameterProvidedException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.INVALID_GRANULARITY",
          new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "Invalid granularity.",
          LocaleContextHolder.getLocale()));
    }
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

    Optional<PlayerPlaytimeLimitV2Config> limitConfig = configRepository.findByUser(user);
    PlayerPlaytimeLimitV2Config config;
    String comment;
    String type = "edit";
    PlayerPlaytimeLimitV2ConfigRevision before = new PlayerPlaytimeLimitV2ConfigRevision();
    PlayerPlaytimeLimitV2ConfigRevision after;

    if (limitConfig.isPresent()) {
      PlayerPlaytimeLimitV2Config currentConfig = limitConfig.get();
      before = PlayerPlaytimeLimitV2ConfigRevision.builder()
          .id(currentConfig.getCurrentConfigRevision().getId())
          .createdDate(currentConfig.getCurrentConfigRevision().getCreatedDate())
          .effectiveFrom(currentConfig.getCurrentConfigRevision().getEffectiveFrom())
          .secondsAllocated(currentConfig.getCurrentConfigRevision().getSecondsAllocated())
          .build();
      if (currentConfig.getCurrentConfigRevision().getSecondsAllocated() < request.getSecondsAllocated()) {
        if (!ObjectUtils.isEmpty(currentConfig.getPendingConfigRevision()) && (request.getSecondsAllocated() == currentConfig.getPendingConfigRevision().getSecondsAllocated())) {
            after = currentConfig.getPendingConfigRevision();
        } else {
          PlayerPlaytimeLimitV2ConfigRevision pendingRevision = PlayerPlaytimeLimitV2ConfigRevision.builder().user(user).createdBy(author)
              .createdDate(now).effectiveFrom(now.plusSeconds(getPendingPlaytimeLimitUpdateDelay(user.domainName())))
              .secondsAllocated(request.getSecondsAllocated()).granularity(granularity.get()).build();
          pendingRevision = configRevisionRepository.save(pendingRevision);
          currentConfig.setPendingConfigRevision(pendingRevision);
          after = pendingRevision;
        }
      } else {
        currentConfig.getCurrentConfigRevision().setSecondsAllocated(request.getSecondsAllocated());
        currentConfig.getCurrentConfigRevision().setGranularity(granularity.get());

        currentConfig.getCurrentConfigRevision().setEffectiveFrom(now);
        currentConfig.setCurrentConfigRevision(configRevisionRepository.save(currentConfig.getCurrentConfigRevision()));
        after = currentConfig.getCurrentConfigRevision();
      }
      config = currentConfig;

      comment = " PlayTime Limit Config changed FROM: " + limitToString(granularity.get().getType(),
          currentConfig.getCurrentConfigRevision().getSecondsAllocated()) + " TO: " + limitToString(granularity.get().getType(),
          request.getSecondsAllocated());
    } else {
      PlayerPlaytimeLimitV2ConfigRevision newRevision = PlayerPlaytimeLimitV2ConfigRevision.builder().user(user).createdBy(author)
          .createdDate(ObjectUtils.isEmpty(request.getCreatedDate()) ? now : request.getCreatedDate()).secondsAllocated(request.getSecondsAllocated())
          .granularity(granularity.get()).effectiveFrom(now).build();
      type = "create";
      newRevision = configRevisionRepository.save(newRevision);
      config = PlayerPlaytimeLimitV2Config.builder().user(user).currentConfigRevision(newRevision).build();
      after = newRevision;
      comment = "New PlayTime limit Config Created: " + limitToString(granularity.get().getType(), request.getSecondsAllocated());
    }

    config = configRepository.save(config);
    log.debug(comment + " for player: " + config.getUser().getGuid());
    try {
      List<ChangeLogFieldChange> clfc = changeLogService.compare(after, before,
          new String[]{"id", "createdDate", "effectiveFrom", "secondsAllocated"});
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user.playtimelimit", type, user.getId(), author.guid(), tokenUtil, comment, null,
          clfc, Category.RESPONSIBLE_GAMING, SubCategory.PLAY_TIME_LIMIT, 1, user.domainName());
    } catch (Exception e) {
      log.warn("can't write Changelog for playtime limit update for user: {}", user.guid(), e);
    }

    try {
      pubSubUserService.publishAccountChange(user, tokenUtil.getAuthentication());
    } catch (Exception e) {
      log.warn("can't send pub-sub message for playtime limit update for user: {}", user.guid(), e);
    }

    return config;
  }

  public PlayTimeLimitPubSubDTO updateAndGetPlayerEntryDTO(String playerGuid)
      throws Status414UserNotFoundException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException {
    Optional<PlayerPlaytimeLimitV2Config> limitConfig = configRepository.findByUser_Guid(playerGuid);
    if (limitConfig.isEmpty()) {
      return null;
    }
    PlayTimeLimitPubSubDTO dto = new PlayTimeLimitPubSubDTO();
    PlayerPlaytimeLimitV2Entry entry = self.updateAndGetPlayerEntry(playerGuid);
    long remainingSeconds = limitConfig.get().getCurrentConfigRevision().getSecondsAllocated() - entry.getSecondsAccumulated();
    dto.setType(LimitType.TYPE_PLAY_TIME_LIMIT_ACTIVE.name());
    dto.setPlayTimeLimitSeconds(limitConfig.get().getCurrentConfigRevision().getSecondsAllocated());
    dto.setPlayTimeLimitRemainingSeconds(remainingSeconds);
    dto.setGranularity(
        lithium.service.client.objects.Granularity.fromType(limitConfig.get().getCurrentConfigRevision().getGranularity().getType()).granularity());
    dto.setPlayTimeLimit(remainingSeconds / 60);
    return dto;
  }


  public PlayerPlaytimeLimitV2Entry updateAndGetPlayerEntry(String playerGuid)
      throws Status414UserNotFoundException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException {
    Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(playerGuid.split("/")[0]);

    if (!domain.isPlaytimeLimit()) {
      return null;
    }

    Optional<PlayerPlaytimeLimitV2Config> limitConfig = getPlayerPlaytimeLimitV2Config(
        playerGuid);

    if (limitConfig.isEmpty()) {
      return null;
    }

    PlayerPlaytimeLimitV2Config config = limitConfig.get();

    Period period = periodService.findOrCreatePeriod(LocalDateTime.now(ZoneOffset.UTC), config.getUser().getDomain(),
        config.getCurrentConfigRevision().getGranularity());

    return self.updateAndGetPlayerEntry(config, period);
  }

  public Optional<PlayerPlaytimeLimitV2Config> getPlayerPlaytimeLimitV2Config(String playerGuid)
      throws Status414UserNotFoundException {
    User user = userService.findFromGuid(playerGuid);

    if (ObjectUtils.isEmpty(user)) {
      throw new Status414UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_GUID_NOT_FOUND",
          new Object[]{new lithium.service.translate.client.objects.Domain(playerGuid.split("/")[0]), playerGuid},
          "User with id={0} not found.", LocaleContextHolder.getLocale()));
    }

    Optional<PlayerPlaytimeLimitV2Config> limitConfig = configRepository.findByUser(user);

    if (limitConfig.isEmpty()) {
      return Optional.empty();
    }
    return limitConfig;
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = {ObjectOptimisticLockingFailureException.class}, exclude = {
      Exception.class})
  public PlayerPlaytimeLimitV2Entry updateAndGetPlayerEntry(PlayerPlaytimeLimitV2Config configuration, Period period) {
    // Get an exclusive lock on user. Concurrent access to this method will queue up.
    User user = userService.findForUpdate(configuration.getUser().getId());

    // Transaction is propagated
    UserActiveSessionsMetadata metadata = userActiveSessionsMetadataService.findOrCreate(user.getId());
    log.trace("updatePlayTimeLimitForUser | metadata (before): {}", metadata);

    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    if (metadata.getActiveSessionCount() > 0 && metadata.getPlaytimeLimitLastUpdated() == null) {
        metadata.setPlaytimeLimitLastUpdated(now);
        userActiveSessionsMetadataService.save(metadata);
    }

    if (ObjectUtils.isEmpty(metadata) || metadata.getActiveSessionCount() == 0) {
      return limitEntryRepository.findByPeriodAndUser(period, user);
    }

    PlayerPlaytimeLimitV2Entry entry = limitEntryRepository.findByPeriodAndUser(period, user);

    if (!ObjectUtils.isEmpty(entry)) {
      return self.currentPeriodPlayerEntry(metadata, entry, configuration.getCurrentConfigRevision(), now);
    }

    entry = PlayerPlaytimeLimitV2Entry.builder().period(period).secondsAccumulated(0).user(user).build();

    if (metadata.getPlaytimeLimitLastUpdated().isAfter(period.getDateEnd())) {
      long value = ChronoUnit.SECONDS.between(metadata.getPlaytimeLimitLastUpdated(), period.getDateEnd());

      entry.setSecondsAccumulated(
          Math.min((value + entry.getSecondsAccumulated()), configuration.getCurrentConfigRevision().getSecondsAllocated())
      );

      if (entry.getSecondsAccumulated() >= configuration.getCurrentConfigRevision().getSecondsAllocated() && ObjectUtils.isEmpty(entry.getLimitReachedAt())) {
        entry.setLimitReachedAt(now);
      }
    }
    return self.currentPeriodPlayerEntry(metadata, entry, configuration.getCurrentConfigRevision(), now);
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = {ObjectOptimisticLockingFailureException.class}, exclude = {
      Exception.class})
  public PlayerPlaytimeLimitV2Entry currentPeriodPlayerEntry(UserActiveSessionsMetadata metadata, PlayerPlaytimeLimitV2Entry entry,
      PlayerPlaytimeLimitV2ConfigRevision currentConfigRevision, LocalDateTime now) {

    long value = DateUtil.secondsBetween(metadata.getPlaytimeLimitLastUpdated(), now);

    entry.setSecondsAccumulated(Math.min(value + entry.getSecondsAccumulated(), currentConfigRevision.getSecondsAllocated()));

    if (entry.getSecondsAccumulated() >= currentConfigRevision.getSecondsAllocated()) {
      entry.setLimitReachedAt(now);
    }

    entry = self.saveLimitEntry(entry);
    metadata.setPlaytimeLimitLastUpdated(now);
    metadata = userActiveSessionsMetadataService.save(metadata);

    log.trace("currentPeriodPlayerEntry | currentTimeUsed: {}, newTimeUsed: {}, metadata (after): {}", entry.getSecondsAccumulated(), value,
        metadata);

    log.debug(
        "PlayTime used updated for userId:" + currentConfigRevision.getUser().getId() + "from " + entry.getSecondsAccumulated() + " to " + value);

    return entry;
  }

  public boolean isAllowedToPlay(String playerGuid) throws Status414UserNotFoundException, Status438PlayTimeLimitConfigurationNotFoundException,
      Status438PlayTimeLimitReachedException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException {
    PlayerPlaytimeLimitV2Entry entry = updateAndGetPlayerEntry(playerGuid);
    if (ObjectUtils.isEmpty(entry)) {
      return true;
    }

    PlayerPlaytimeLimitV2Config playerConfiguration = getPlayerConfiguration(playerGuid);

    boolean value = entry.getSecondsAccumulated() < playerConfiguration.getCurrentConfigRevision().getSecondsAllocated();

    if (!value) {
      String message = playerConfiguration.getCurrentConfigRevision().getGranularity().getType() + " limit " +
          buildFormattedTime(playerConfiguration.getCurrentConfigRevision().getSecondsAllocated()) + " has been exhausted";
      throw new Status438PlayTimeLimitReachedException(message);
    }

    return true;
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = {ObjectOptimisticLockingFailureException.class}, exclude = {
      Exception.class})
  public PlayerPlaytimeLimitV2Entry saveLimitEntry(PlayerPlaytimeLimitV2Entry entry) {
    return limitEntryRepository.save(entry);
  }

  @TimeThisMethod
  public void migrateV1Data(LithiumTokenUtil tokenUtil, Integer pageSize) throws Status550ServiceDomainClientException {
    Optional<PlayerPlaytimeLimitV1DataMigrationProgress> initial = migrationProgressRepository.findFirstByIdGreaterThan(0L);
    PlayerPlaytimeLimitV1DataMigrationProgress current = initial.orElseGet(
        () -> migrationProgressRepository.save(PlayerPlaytimeLimitV1DataMigrationProgress.builder().id(1L).running(false).build()));

    if (current.isRunning()) {
      log.warn("Player Playtime Migration currently running");
      return;
    }

    current.setRunning(true);

    processPlayerMigration(tokenUtil, current, pageSize);
  }

  public void processPlayerMigration(LithiumTokenUtil tokenUtil, PlayerPlaytimeLimitV1DataMigrationProgress current, Integer pageSize)
      throws Status550ServiceDomainClientException {
    int page = 0;
    int size =  ObjectUtils.isEmpty(pageSize) ? 10 : pageSize;
    boolean hasMore = true;
    long startMigrationJobFromId = current.getLastIdProcessed();

    while (hasMore) {
      PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
      Page<PlayerPlayTimeLimit> all = playerTimeLimitRepository.findAllByIdGreaterThan(pageRequest, startMigrationJobFromId);
      log.info("Playtime Migration found " + all.getContent().size() + " entries. Page " + all.getNumber() + " of " + all.getTotalPages());
      SW.start("PlayerMigrationPlayTimeLimit_pagerequest_" + page);

      for (PlayerPlayTimeLimit oldPlayTimeLimit : all.getContent()) {
        if (!oldPlayTimeLimit.getType().equals(LimitType.TYPE_PLAY_TIME_LIMIT_PENDING)) {
          current = self.doUserMigrationUpdate(tokenUtil, current, oldPlayTimeLimit);
        }
        current = migrationProgressRepository.save(current);
      }

      SW.stop();
      page++;
      if (!all.hasNext()) {
        hasMore = false;
        current.setRunning(false);
        current = self.v1DataMigrationProgressSave(current);
      }
      log.info("Playtime Migration done Page: " + all.getNumber() + " out of: " + all.getContent().size() + " entries.");
    }
    log.info("Playtime Migration currently completed at " + current.getLastIdProcessed() + " out of: " + page + " pages and " + size + " size");
  }

  @Transactional
  public PlayerPlaytimeLimitV1DataMigrationProgress doUserMigrationUpdate(LithiumTokenUtil tokenUtil,
      PlayerPlaytimeLimitV1DataMigrationProgress current, PlayerPlayTimeLimit oldPlayTimeLimit) throws Status550ServiceDomainClientException {
    User user = userService.findById(oldPlayTimeLimit.getUserId());

    Optional<PlayerPlaytimeLimitV2Config> userConfig = configRepository.findByUser(user);
    PlayerPlaytimeLimitV2Config config = userConfig.orElse(configRepository.save(PlayerPlaytimeLimitV2Config.builder().user(user).build()));

    PlayerPlaytimeLimitV2ConfigRevision activeRevision = createMigrationPlayerPlaytimeLimitV2ConfigRevision(
        oldPlayTimeLimit, user);
    activeRevision = configRevisionRepository.save(activeRevision);

    config.setCurrentConfigRevision(activeRevision);
    migrationChangelogs(tokenUtil, oldPlayTimeLimit, user, config, activeRevision);

    PlayerPlayTimeLimit oldPendingPlaytimeLimit = playerTimeLimitRepository.findByUserIdAndType(user.getId(), LimitType.TYPE_PLAY_TIME_LIMIT_PENDING);
    PlayerPlaytimeLimitV2ConfigRevision pendingRevision = null;
    if (oldPendingPlaytimeLimit != null) {
      pendingRevision = createMigrationPlayerPlaytimeLimitV2ConfigRevision(oldPendingPlaytimeLimit, user);
      pendingRevision = configRevisionRepository.save(pendingRevision);

      config.setPendingConfigRevision(pendingRevision);
      migrationChangelogs(tokenUtil, oldPendingPlaytimeLimit, user, config, pendingRevision);
    }

    configRepository.save(config);

    current.setLastIdProcessed(oldPlayTimeLimit.getId());
    return current;
  }

  private void migrationChangelogs(LithiumTokenUtil tokenUtil, PlayerPlayTimeLimit oldPlayTimeLimit,
      User user, PlayerPlaytimeLimitV2Config config,
      PlayerPlaytimeLimitV2ConfigRevision pendingRevision) {
    try {
      List<ChangeLogFieldChange> clfc = changeLogService.compare(pendingRevision, new PlayerPlaytimeLimitV2ConfigRevision(),
          new String[]{"createdDate", "effectiveFrom", "secondsAllocated"});
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user.playtimelimit", "create", user.getId(), tokenUtil.guid(), tokenUtil,
          "PlayTime limit Migrated: " + limitToString(oldPlayTimeLimit.getGranularity().type(), pendingRevision.getSecondsAllocated()), null,
          clfc, Category.RESPONSIBLE_GAMING, SubCategory.PLAY_TIME_LIMIT, 1, user.domainName());
      updateAndGetPlayerEntry(config.getUser().guid());
    } catch (Status414UserNotFoundException | Status426InvalidParameterProvidedException e) {
      log.error("Failed to migrate Playtime due to {}", e.getMessage(), e);
    } catch (Exception e) {
      log.warn("can't write Changelog for playtime limit migration for user: {}", user.guid(), e);
    }
  }

  private PlayerPlaytimeLimitV2ConfigRevision createMigrationPlayerPlaytimeLimitV2ConfigRevision(PlayerPlayTimeLimit oldPlayTimeLimit, User user)
      throws Status550ServiceDomainClientException {
    Optional<Granularity> granularity = granularityRepository.findById(Long.valueOf(oldPlayTimeLimit.getGranularity().granularity()));
    if (granularity.isEmpty() || isInActiveGranularity(oldPlayTimeLimit.getGranularity().type())) {
      log.warn("Invalid granularity for playtime limit migration for user: {}. Using maximum granularity", user.guid());
      granularity = granularityRepository.findById(Long.valueOf(lithium.service.client.objects.Granularity.GRANULARITY_MONTH.granularity()));
    }

    return PlayerPlaytimeLimitV2ConfigRevision.builder()
        .user(user).createdBy(user)
        .createdDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(oldPlayTimeLimit.getCreatedDate()), ZoneOffset.UTC))
        .secondsAllocated(oldPlayTimeLimit.getTimeInMinutes() * 60)
        .granularity(granularity.get())
        .effectiveFrom(ObjectUtils.isEmpty(oldPlayTimeLimit.getAppliedAt()) ?
            LocalDateTime.now(ZoneOffset.UTC).plusSeconds(getPendingPlaytimeLimitUpdateDelay(oldPlayTimeLimit.getDomainName())) :
            LocalDateTime.ofInstant(oldPlayTimeLimit.getAppliedAt().toDate().toInstant(), ZoneOffset.UTC))
        .build();
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = {ObjectOptimisticLockingFailureException.class}, exclude = {
      Exception.class})
  public PlayerPlaytimeLimitV1DataMigrationProgress v1DataMigrationProgressSave(PlayerPlaytimeLimitV1DataMigrationProgress dataMigrationProgress) {
    return migrationProgressRepository.save(dataMigrationProgress);
  }

  private String limitToString(String granularity, long secondsAllocated) {
    return granularity + " " + buildFormattedTime(secondsAllocated);
  }

  private String buildFormattedTime(long second) {
    Duration duration = Duration.ofSeconds(second);
    long day = duration.toDays();
    long hours = duration.toHours();
    int minutes = duration.toMinutesPart();
    int seconds = duration.toSecondsPart();
    return String.format("%02d Days %02d Hours %02d Minutes %02d Seconds ", day, hours, minutes, seconds);
  }

  public Integer getPendingPlaytimeLimitUpdateDelay(String domainName) throws Status550ServiceDomainClientException {
    Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    String result = domain.findDomainSettingByName(DomainSettings.PENDING_PLAYTIME_LIMIT_UPDATE_DELAY.key())
        .orElse(DomainSettings.PENDING_PLAYTIME_LIMIT_UPDATE_DELAY.defaultValue());

    return Integer.parseInt(result) * 60 * 60;
  }


  public Page<PlayerPlaytimeLimitV2Config> findAllPending(PageRequest pageRequest) {
    return configRepository.findAllByPendingConfigRevisionIsNotNull(pageRequest);
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = {ObjectOptimisticLockingFailureException.class}, exclude = {
      Exception.class})
  public void movePendingLimitToCurrent(PlayerPlaytimeLimitV2Config config) throws Status500LimitInternalSystemClientException {
    PlayerPlaytimeLimitV2ConfigRevision current = config.getCurrentConfigRevision();
    PlayerPlaytimeLimitV2ConfigRevision pending = config.getPendingConfigRevision();

    config.setCurrentConfigRevision(pending);
    config.setPendingConfigRevision(null);
    config = configRepository.save(config);

    try {
      userApiInternalClientService.pushUserUpdateToPubSubUserService(config.getUser().getGuid());
    } catch (UserClientServiceFactoryException | UserNotFoundException e) {
      log.error("Cant push playTime limit change for userGuid:" + config.getUser().getGuid() + " pubsub message because of " + e);
    }

    String comment =
        " PlayTime Limit changed FROM: " + limitToString(current.getGranularity().getType(), current.getSecondsAllocated()) + " TO: " + limitToString(
            pending.getGranularity().getType(), pending.getSecondsAllocated());
    try {
      List<ChangeLogFieldChange> changes = changeLogService.compare(current, pending,
          new String[]{"id", "createdDate", "effectiveFrom", "secondsAllocated"});
      changeLogService.registerChangesSystem("user.playtimelimit", "edit", config.getUser().getId(), comment, null, changes,
          Category.RESPONSIBLE_GAMING, SubCategory.PLAY_TIME_LIMIT, 1, config.domainName());
    } catch (Exception e) {
      throw new Status500LimitInternalSystemClientException(e);
    }
  }


  private boolean isInActiveGranularity(String type) {
    return Objects.equals(type, lithium.service.client.objects.Granularity.GRANULARITY_YEAR.type()) ||
        Objects.equals(type, lithium.service.client.objects.Granularity.GRANULARITY_HOUR.type()) ||
        Objects.equals(type, lithium.service.client.objects.Granularity.GRANULARITY_TOTAL.type());
  }

  @Cacheable(value = "lithium.service.user.services.data.find-all-active-time-limit-granularities", key = "#root.methodName", unless = "#result == null")
  public List<lithium.service.user.client.objects.Granularity> getActiveGranularities() {
    Iterable<Granularity> allGranularities = granularityRepository.findAll();

    List<Granularity> result = Lists.newArrayList(allGranularities);

    // LSPLAT-5758 Not all granularities are available so the filter will help remove them
    result.removeIf(granularity -> isInActiveGranularity(granularity.getType()));

    // LSPLAT-5758 Will want a user-friendly display returned to the Frontend without implicitly adding a new column in as well not manipulating on the Frontend
    return result.stream().map(granularity -> {
      lithium.service.user.client.objects.Granularity mapped = modelMapper.map(granularity, lithium.service.user.client.objects.Granularity.class);

      switch (Objects.requireNonNull(lithium.service.client.objects.Granularity.fromType(mapped.getType()))) {
        case GRANULARITY_DAY -> mapped.setName("Daily");
        case GRANULARITY_WEEK -> mapped.setName("Weekly");
        case GRANULARITY_MONTH -> mapped.setName("Monthly");
        default -> mapped.setName("Undefined");
      }
      return mapped;
    }).toList();
  }

  public long getGranularityType(String name) {
    name = name.toUpperCase();
    long searchType = 2;
    if ("DAILY".equals(name)) {
      searchType = 3;
    } else if ("WEEKLY".equals(name)) {
      searchType = 4;
    }
    return searchType;
  }

  public void removePendingPlayTimeLimitConfigurationById(long configId, LithiumTokenUtil tokenUtil) {
    PlayerPlaytimeLimitV2Config playerConfiguration = configRepository.findById(configId);
    log.info("removing pending playtime limit configuration with id: {} for player: {}", configId, playerConfiguration.getUser().guid());

    if (!ObjectUtils.isEmpty(playerConfiguration.getPendingConfigRevision())) {
      PlayerPlaytimeLimitV2ConfigRevision pendingConfigRevision = playerConfiguration.getPendingConfigRevision();
      playerConfiguration.setPendingConfigRevision(null);
      configRepository.save(playerConfiguration);
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user.playtimelimitconfig", "delete", playerConfiguration.getUser().getId(),
          tokenUtil.guid(), tokenUtil,
          "Pending " + limitToString(pendingConfigRevision.getGranularity().getType(), pendingConfigRevision.getSecondsAllocated())
              + " has been removed on player playtime configuration",
          null, null, Category.RESPONSIBLE_GAMING, SubCategory.PLAY_TIME_LIMIT, 1, playerConfiguration.getUser().domainName());
    }
  }

  public List<PlayTimeLimitPubSubDTO> getPubSubLimits(String guid)
      throws Status414UserNotFoundException, Status550ServiceDomainClientException, Status426InvalidParameterProvidedException {
    List<PlayerPlayTimeLimit> playerPlayTimeLimits = getUserPlayTimeLimits(guid);
    List<PlayTimeLimitPubSubDTO> pubSubDto = new ArrayList<>();

    for (PlayerPlayTimeLimit pl : playerPlayTimeLimits) {
      pubSubDto.add(
          new PlayTimeLimitPubSubDTO(pl.getGranularity().granularity(), pl.getTimeLimitRemainingSeconds() / 60,
              pl.getTimeLimitRemainingSeconds(), pl.getType().name()));
    }

    return pubSubDto;
  }

  public List<PlayerPlayTimeLimit> getUserPlayTimeLimits(String userGuid)
      throws Status414UserNotFoundException, Status550ServiceDomainClientException, Status426InvalidParameterProvidedException {
    PlayerPlaytimeLimitV2Config playerConfiguration;

    try {
      playerConfiguration = getPlayerConfiguration(userGuid);
    } catch (Status438PlayTimeLimitConfigurationNotFoundException e) {
      log.debug("Playtime Limits Configuration for player: {}", userGuid, e);
      return Collections.emptyList();
    }

    PlayerPlaytimeLimitV2Entry playerEntry = updateAndGetPlayerEntry(playerConfiguration.getUser().guid());

    List<PlayerPlayTimeLimit> limits = new ArrayList<>(List.of(
        PlayerPlayTimeLimit.builder()
            .id(playerConfiguration.getCurrentConfigRevision().getId())
            .userId(playerConfiguration.getUser().getId())
            .domainName(playerConfiguration.domainName())
            .type(LimitType.TYPE_PLAY_TIME_LIMIT_ACTIVE)
            .createdDate(playerConfiguration.getCurrentConfigRevision().getCreatedDate().toEpochSecond(ZoneOffset.UTC))
            .modifiedDate(playerConfiguration.getCurrentConfigRevision().getCreatedDate().toEpochSecond(ZoneOffset.UTC))
            .appliedAt(DateUtil.toDateTime(playerConfiguration.getCurrentConfigRevision().getEffectiveFrom()))
            .granularity(
                lithium.service.client.objects.Granularity.fromType(playerConfiguration.getCurrentConfigRevision().getGranularity().getType()))
            .timeInMinutes(playerConfiguration.getCurrentConfigRevision().getSecondsAllocated() / 60)
            .timeInMinutesUsed(playerEntry.getSecondsAccumulated() / 60)
            .timeLimitRemainingSeconds(playerConfiguration.getCurrentConfigRevision().getSecondsAllocated() - playerEntry.getSecondsAccumulated())
            .build()));

    if (!ObjectUtils.isEmpty(playerConfiguration.getPendingConfigRevision())) {
      limits.add(PlayerPlayTimeLimit.builder()
          .id(playerConfiguration.getPendingConfigRevision().getId())
          .userId(playerConfiguration.getUser().getId())
          .domainName(playerConfiguration.domainName())
          .type(LimitType.TYPE_PLAY_TIME_LIMIT_PENDING)
          .granularity(lithium.service.client.objects.Granularity.fromType(playerConfiguration.getPendingConfigRevision().getGranularity().getType()))
          .createdDate(playerConfiguration.getPendingConfigRevision().getCreatedDate().toEpochSecond(ZoneOffset.UTC))
          .modifiedDate(playerConfiguration.getPendingConfigRevision().getCreatedDate().toEpochSecond(ZoneOffset.UTC))
          .appliedAt(DateUtil.toDateTime(playerConfiguration.getPendingConfigRevision().getEffectiveFrom()))
          .timeInMinutes(playerConfiguration.getPendingConfigRevision().getSecondsAllocated() / 60)
          .build());
    }

    return limits;
  }

  public PlayerPlayTimeLimit setPlayTimeLimitForUser(Long playerId, int granularity, long durationInMins, LithiumTokenUtil tokenUtil)
      throws Status414UserNotFoundException, Status426InvalidParameterProvidedException, Status550ServiceDomainClientException, Status438PlayTimeLimitConfigurationNotFoundException {

    PlayerPlaytimeLimitV2Config playerConfiguration = self.setPlayerConfiguration(
        PlayerPlaytimeLimitConfigRequest.builder()
            .userId(playerId).granularity(granularity).secondsAllocated(durationInMins * 60)
            .build(), tokenUtil);

    PlayerPlaytimeLimitV2Entry playerEntry = updateAndGetPlayerEntry(playerConfiguration.getUser().guid());

    PlayerPlaytimeLimitV2ConfigRevision revision =
        !ObjectUtils.isEmpty(playerConfiguration.getPendingConfigRevision()) ? playerConfiguration.getPendingConfigRevision()
            : playerConfiguration.getCurrentConfigRevision();

    return PlayerPlayTimeLimit.builder()
        .userId(tokenUtil.id())
        .id(revision.getId())
        .domainName(playerConfiguration.domainName())
        .type(!ObjectUtils.isEmpty(playerConfiguration.getPendingConfigRevision()) ? LimitType.TYPE_PLAY_TIME_LIMIT_PENDING
            : LimitType.TYPE_PLAY_TIME_LIMIT_ACTIVE)
        .createdDate(revision.getCreatedDate().toEpochSecond(ZoneOffset.UTC))
        .modifiedDate(revision.getCreatedDate().toEpochSecond(ZoneOffset.UTC))
        .appliedAt(DateUtil.toDateTime(revision.getEffectiveFrom()))
        .granularity(lithium.service.client.objects.Granularity.fromType(revision.getGranularity().getType()))
        .timeInMinutes(revision.getSecondsAllocated() / 60)
        .timeInMinutesUsed(!ObjectUtils.isEmpty(playerConfiguration.getPendingConfigRevision()) ? 0 : playerEntry.getSecondsAccumulated() / 60)
        .timeLimitRemainingSeconds(!ObjectUtils.isEmpty(playerConfiguration.getPendingConfigRevision()) ? revision.getSecondsAllocated()
            : revision.getSecondsAllocated() - playerEntry.getSecondsAccumulated())
        .build();

  }

  public Period findOrCreatePeriod(String domainName, LocalDateTime date, long granularityId)
      throws Status550ServiceDomainClientException, Status426InvalidParameterProvidedException {
    Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    lithium.service.user.data.entities.Domain domain1 = domainService.findOrCreate(domainName);
    Optional<Granularity> granularity = granularityRepository.findById(granularityId);

    if (granularity.isEmpty() || isInActiveGranularity(granularity.get().getType())) {
      throw new Status426InvalidParameterProvidedException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.INVALID_GRANULARITY",
          new Object[]{new lithium.service.translate.client.objects.Domain(domain.getName())}, "Invalid granularity.",
          LocaleContextHolder.getLocale()));
    }

    return periodService.findOrCreatePeriod(date, domain1, granularity.get());
  }

  public PlayerPlaytimeLimitV2Entry updateSecondsAccumulated(User user, long secondsAccumulated) {

    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    PlayerPlaytimeLimitV2Config playerConfiguration = getPlayerConfiguration(user.getGuid());
    Period period = periodService.findOrCreatePeriod(now, user.getDomain(), playerConfiguration.getCurrentConfigRevision().getGranularity());
    PlayerPlaytimeLimitV2Entry entry = limitEntryRepository.findByPeriodAndUser(period, user);
    entry.setSecondsAccumulated(secondsAccumulated);

    if (entry.getSecondsAccumulated() < playerConfiguration.getCurrentConfigRevision().getSecondsAllocated()) {
      entry.setLimitReachedAt(null);
    }

    return limitEntryRepository.save(entry);
  }
}
