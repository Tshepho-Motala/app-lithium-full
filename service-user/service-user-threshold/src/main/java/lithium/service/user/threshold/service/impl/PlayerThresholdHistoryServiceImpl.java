package lithium.service.user.threshold.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.accounting.objects.Period;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.limit.client.objects.PlayerLimitV2Dto;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryDto;
import lithium.service.user.threshold.client.dto.PlayerThresholdHistoryRequest;
import lithium.service.user.threshold.data.context.ProcessingContext;
import lithium.service.user.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.service.user.threshold.data.entities.ThresholdRevision;
import lithium.service.user.threshold.data.entities.User;
import lithium.service.user.threshold.data.repositories.PlayerThresholdHistoryRepository;
import lithium.service.user.threshold.data.specifications.PlayerThresholdHistorySpecification;
import lithium.service.user.threshold.service.NotificationService;
import lithium.service.user.threshold.service.PlayerThresholdHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlayerThresholdHistoryServiceImpl implements PlayerThresholdHistoryService {

  @Autowired
  private PlayerThresholdHistoryRepository playerThresholdHistoryRepository;
  @Autowired
  NotificationService notificationService;
  @Autowired
  private ModelMapper modelMapper;
  @Autowired
  CachingDomainClientService cachingDomainClientService;

  @Override
  public PlayerThresholdHistory savePlayerThresholdHistory(ProcessingContext context, PlayerLimitV2Dto limit, boolean sendNotifications)
  throws Status500InternalServerErrorException
  {
    return savePlayerThresholdHistory(context, limit, null, null, null, null, sendNotifications);
  }

  @Override
  public PlayerThresholdHistory savePlayerThresholdHistory(ProcessingContext context, PlayerLimitV2Dto limit, BigDecimal amount,
      BigDecimal depositAmount, BigDecimal withdrawalAmount, BigDecimal netLifetimeDepositAmount, boolean sendNotifications)
  throws Status500InternalServerErrorException
  {
    Threshold threshold = context.getThreshold();
    Period period = context.getPeriod();
    User user = context.getUser();
    List<PlayerThresholdHistory> findByUserAndThresholdRevisionAndPeriod = findByUserAndThresholdRevisionAndPeriod(user, threshold, period);
    if (findByUserAndThresholdRevisionAndPeriod.isEmpty()) {
      ThresholdRevision thresholdRevision = threshold.getCurrent();
      PlayerThresholdHistory playerThresholdHistory = PlayerThresholdHistory.builder()
          .thresholdHitDate(new Date())
          .thresholdRevision(thresholdRevision)
          .amount(amount)
          .depositAmount(depositAmount)
          .withdrawalAmount(withdrawalAmount)
          .netLifetimeDepositAmount(netLifetimeDepositAmount)
          .user(user)
          .build();

      switch (threshold.getGranularity()) {
        case GRANULARITY_DAY -> {
          playerThresholdHistory.setDailyLossLimit(limit.getLimitAmount());
          playerThresholdHistory.setDailyLossLimitUsed(limit.getNetLossAmount());
        }
        case GRANULARITY_WEEK -> {
          playerThresholdHistory.setWeeklyLossLimit(limit.getLimitAmount());
          playerThresholdHistory.setWeeklyLossLimitUsed(limit.getNetLossAmount());
        }
        case GRANULARITY_MONTH -> {
          playerThresholdHistory.setMonthlyLossLimit(limit.getLimitAmount());
          playerThresholdHistory.setMonthlyLossLimitUsed(limit.getNetLossAmount());
        }
        default -> log.warn("Not implemented");
      }
      context.setPlayerThresholdHistory(playerThresholdHistory);
      if (sendNotifications) {
        notifications(context);
      }
      return save(playerThresholdHistory);
    } else {
      // This will send a threshold reached message to player only if loss limit visibility is enabled.
      PlayerThresholdHistory pth = findByUserAndThresholdRevisionAndPeriod.get(0);
      if (!pth.getThresholdReachedMessage()) {
        pth.setThresholdReachedMessage(Boolean.TRUE);
        pth = save(pth);
        context.setPlayerThresholdHistory(pth);
        notificationService.sendToPlayerInbox(context);
      }
    }
    log.debug("Threshold breach already recorded, not saving another record and sending more notifications.");
    return findByUserAndThresholdRevisionAndPeriod.get(0);
  }

  private void notifications(ProcessingContext context) {
    try {
      log.debug("Sending threshold notifications to player({}) type: {}, granularity: {}, playerThresholdHistory: {}", context.getUser().getGuid(),
          context.getThreshold().getType().getName(), context.getThreshold().getGranularity().name(), context.getPlayerThresholdHistory());
      notificationService.sendToPlayerInbox(context);
      log.debug("Sending threshold notifications to external for player({}) type: {}, granularity: {}, playerThresholdHistory: {}",
          context.getUser().getGuid(), context.getThreshold().getType().getName(), context.getThreshold().getGranularity().name(),
          context.getPlayerThresholdHistory());
      notificationService.sendToExternal(context);
    } catch (Status512ProviderNotConfiguredException e) {
      log.debug("Provider not configured for external notifications. {}", e.getMessage());
    } catch (Exception e) {
      log.error("Could not send threshold notifications to external for player({}) type: {}, granularity: {}, playerThresholdHistory: {}",
          context.getUser().getGuid(), context.getThreshold().getType().getName(), context.getThreshold().getGranularity().name(),
          context.getPlayerThresholdHistory(), e);
    }
  }

  @Override
  public DataTableResponse<PlayerThresholdHistoryDto> find(PlayerThresholdHistoryRequest request)
  throws Status500InternalServerErrorException
  {
    DataTableRequest dataTableRequest = getDefaultTableRequest();
    if (request.getTableRequest() == null) {
      request.setTableRequest(dataTableRequest);
    }
    Specification<PlayerThresholdHistory> spec = PlayerThresholdHistorySpecification.findBy(request, request.getTableRequest());
    Page<PlayerThresholdHistory> page = playerThresholdHistoryRepository.findAll(spec, request.getTableRequest().getPageRequest());
    Page<PlayerThresholdHistoryDto> pageDto = page.map(this::convertToPlayerThresholdHistoryDto);
    log.debug("Response: {}, data: {}", pageDto, pageDto.getContent());
    return new DataTableResponse<>(request.getTableRequest(), pageDto);
  }

  private PlayerThresholdHistoryDto convertToPlayerThresholdHistoryDto(PlayerThresholdHistory playerThresholdHistory) {
    PlayerThresholdHistoryDto dto = modelMapper.map(playerThresholdHistory, PlayerThresholdHistoryDto.class);
    String defaultDomainCurrencySymbol = cachingDomainClientService.getDefaultDomainCurrencySymbol(
        playerThresholdHistory.getUser().getDomain().getName());
    dto.setDefaultDomainCurrencySymbol(defaultDomainCurrencySymbol);
    dto.setTriggerType(playerThresholdHistory.getThresholdRevision().getThreshold().getType().getName());
    dto.setAccountCreationDate(playerThresholdHistory.getUser().getAccountCreationDate());
    dto.setGranularity(playerThresholdHistory.getThresholdRevision().getThreshold().getGranularity().type());
    return dto;
  }

  private List<PlayerThresholdHistory> findByUserAndThresholdRevisionAndPeriod(User user, Threshold threshold, Period period) {
    return playerThresholdHistoryRepository.findByUserAndThresholdRevisionAndThresholdHitDateBetween(user, threshold.getCurrent(),
        period.getDateStart(), period.getDateEnd());
  }

  private PlayerThresholdHistory save(PlayerThresholdHistory playerThresholdHistory) {
    return playerThresholdHistoryRepository.save(playerThresholdHistory);
  }

  private DataTableRequest getDefaultTableRequest() {
    DataTableRequest dataTableRequest = new DataTableRequest();
    dataTableRequest.setEcho("1");
    dataTableRequest.setPageRequest(PageRequest.of(0, 1000));
    return dataTableRequest;

  }

}
