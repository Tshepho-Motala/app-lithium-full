package lithium.service.reward.service;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.client.objects.Granularity;
import lithium.service.promo.client.services.PromotionsClientService;
import lithium.service.reward.data.entities.Domain;
import lithium.service.reward.data.entities.Reward;
import lithium.service.reward.data.entities.RewardRevision;
import lithium.service.reward.data.entities.RewardRevisionType;
import lithium.service.reward.data.entities.RewardRevisionTypeGame;
import lithium.service.reward.data.entities.RewardRevisionTypeValue;
import lithium.service.reward.data.entities.RewardType;
import lithium.service.reward.data.entities.User;
import lithium.service.reward.data.repositories.RewardRepository;
import lithium.service.reward.data.repositories.RewardRevisionRepository;
import lithium.service.reward.data.repositories.RewardRevisionTypeGameRepository;
import lithium.service.reward.data.repositories.RewardRevisionTypeValueRepository;
import lithium.service.reward.data.repositories.RewardTypeFieldRepository;
import lithium.service.reward.dto.SimpleRewardType;
import lithium.service.reward.dto.SimpleRewardTypeGame;
import lithium.service.reward.dto.SimpleRewardTypeValue;
import lithium.service.reward.dto.requests.CreateRewardRequest;
import lithium.service.reward.dto.requests.RewardEditRequest;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Service
public class RewardService {

  @Autowired
  RewardTypeService rewardTypeService;
  @Autowired
  RewardRepository rewardRepository;
  @Autowired
  RewardRevisionRepository rewardRevisionRepository;
  @Autowired
  RewardRevisionTypeValueRepository rewardRevisionTypeValueRepository;
  @Autowired
  RewardRevisionTypeGameRepository rewardRevisionTypeGameRepository;

  @Autowired
  RewardTypeFieldRepository rewardTypeFieldRepository;

  @Autowired
  DomainService domainService;

  @Autowired
  UserService userService;

  @Autowired RewardRevisionTypeService rewardRevisionTypeService;
  @Autowired PlayerRewardHistoryService playerRewardHistoryService;

  @Autowired
  PromotionsClientService promotionsClientService;

  @Autowired
  private ChangeLogService changeLogService;

  @Autowired
  RewardNotificationService rewardNotificationService;

  @Retryable(value = Exception.class, maxAttempts = 5)
  @Transactional
  public Reward create(CreateRewardRequest request, LithiumTokenUtil util) {
    log.debug("Creating reward with from request: {}", request);

    Domain domain = domainService.findOrCreate(request.getDomainName());
    User user = userService.findOrCreate(util.guid());

    Reward reward = rewardRepository.save(Reward.builder()
                    .domain(domain)
                    .editUser(user)
            .build());

    RewardRevision rewardRevision = RewardRevision.builder()
            .name(request.getName())
            .code(request.getCode())
            .enabled(true)
            .description(request.getDescription())
            .reward(reward)
            .validFor(request.getValidFor())
            .validForGranularity(Granularity.fromGranularity(request.getValidForGranularity()))
            .build();

    rewardRevisionRepository.save(rewardRevision);

    List<RewardRevisionType> types = new ArrayList<>();

    for (SimpleRewardType simpleRewardType: request.getRewardTypes()) {
      RewardType rewardType = rewardTypeService.findByUrlAndName(simpleRewardType.url(), simpleRewardType.rewardTypeName());

      RewardRevisionType rewardRevisionType = RewardRevisionType.builder()
              .rewardType(rewardType)
              .rewardRevision(rewardRevision)
              .notificationMessage(simpleRewardType.notificationMessage())
              .instant(simpleRewardType.instant())
              .build();

      rewardRevisionTypeService.save(rewardRevisionType);
      rewardNotificationService.registerRewardTypeNotification(rewardRevisionType);

      for (SimpleRewardTypeValue rewardTypeValue: simpleRewardType.rewardTypeValues()) {

        RewardRevisionTypeValue rewardRevisionTypeValue = RewardRevisionTypeValue.builder()
                .rewardRevisionType(rewardRevisionType)
                .value(rewardTypeValue.value())
                .rewardTypeField(rewardTypeFieldRepository.findByRewardTypeAndName(rewardType, rewardTypeValue.rewardTypeFieldName()))
                .build();

        rewardRevisionTypeValueRepository.save(rewardRevisionTypeValue);

      }

      for (SimpleRewardTypeGame rewardTypeGame: simpleRewardType.rewardTypeGames()) {

        RewardRevisionTypeGame rewardRevisionTypeGame = RewardRevisionTypeGame.builder()
                .guid(rewardTypeGame.guid())
                .rewardRevisionType(rewardRevisionType)
                .gameName(rewardTypeGame.gameName())
                .gameId(rewardTypeGame.gameId())
                .build();

        rewardRevisionTypeGameRepository.save(rewardRevisionTypeGame);
      }

      types.add(rewardRevisionType);
    }


    rewardRevision.setRevisionTypes(types);
    rewardRevisionRepository.save(rewardRevision);
    reward.setCurrent(rewardRevision);
    rewardRepository.save(reward);

    try {
      List<ChangeLogFieldChange> changes = changeLogService.copy(reward, new Reward(), new String[]{"id","domain", "current", "editUser"});
      changeLogService.registerChangesWithDomainAndFullName("reward", "edit", reward.getId(), util.guid(), null, null, changes, Category.REWARDS, SubCategory.REWARD_CREATED, 100, domain.getName(), util.userLegalName());
    } catch (Exception e) {
      log.error("Failed to register changelogs after creating reward with id {}, user {}", reward.getId(), util.guid());
    }

    log.info("Create reward {}", reward);
    return reward;
  }

  @Transactional(rollbackOn=Exception.class)
  @Retryable(value = Exception.class, maxAttempts = 5)
  public Reward modify(Reward reward, String userGuid) {
    if (reward.getEdit() == null) {
      RewardRevision edit = RewardRevision.builder().build();
      copy(reward.getCurrent(), edit);
      reward.setEdit(edit);
      reward.setEditUser(userService.findOrCreate(userGuid));
      reward = rewardRepository.save(reward);
    }

    return reward;
  }

  @Transactional(rollbackOn=Exception.class)
  @Retryable(value = Exception.class, maxAttempts = 5)
  public Reward modify(Reward reward, RewardEditRequest rewardEditRequest) throws Exception {
    RewardRevision editTo = reward.getEdit();
    editTo.setEnabled(rewardEditRequest.getEnabled() != null ? rewardEditRequest.getEnabled() : editTo.isEnabled());
    editTo.setName(rewardEditRequest.getName());
    editTo.setDescription(rewardEditRequest.getDescription());
    editTo.setValidFor(rewardEditRequest.getValidFor());
    editTo.setValidForGranularity(Granularity.fromGranularity(rewardEditRequest.getValidForGranularity()));
    editTo.setActivationNotificationName(rewardEditRequest.getActivationNotificationName());
    rewardRevisionRepository.save(editTo);
    return rewardRepository.save(reward);
  }

  @Transactional
  public Reward modifyAndSaveCurrent(Reward reward, RewardEditRequest rewardEditRequest) throws Exception {
    reward = modify(reward, rewardEditRequest);
    reward.setCurrent(reward.getEdit());
    reward.setEdit(null);
    return rewardRepository.save(reward);
  }

  @Transactional
  @Retryable(value = Exception.class, maxAttempts = 5)
  public Reward saveCurrent(Reward reward) {
    if(reward.getEdit() != null) {
      reward.setCurrent(reward.getEdit());
      reward.setEdit(null);
      return rewardRepository.save(reward);
    }

    return reward;
  }

//  @Transactional(rollbackOn=Exception.class)
//  @Retryable(value = Exception.class, maxAttempts = 5)
//  public RewardRevisionTypeValue saveRewardRevisionTypeValue(RewardRevision rewardRevision, SimpleRewardTypeValue simpleRewardTypeValue) {
//    RewardType rewardType = rewardTypeService.findByUrlAndName(simpleRewardTypeValue.url(), simpleRewardTypeValue.rewardTypeName());
//    RewardRevisionType rewardRevisionType = findOrCreateRewardRevisionType(rewardRevision, rewardType, simpleRewardTypeValue.instant());
//    RewardRevisionTypeValue rewardRevisionTypeValue = RewardRevisionTypeValue.builder()
//            .rewardRevisionType(rewardRevisionType)
//            .value(simpleRewardTypeValue.value())
//            .rewardTypeField(rewardTypeFieldRepository.findByRewardTypeAndName(rewardType, simpleRewardTypeValue.rewardTypeFieldName()))
//            .build();
//
//    return rewardRevisionTypeValueRepository.save(rewardRevisionTypeValue);
//  }

//  @Transactional(rollbackOn=Exception.class)
//  @Retryable(value = Exception.class, maxAttempts = 5)
//  public RewardRevisionTypeGame saveRewardRevisionTypeGame(RewardRevision rewardRevision, SimpleRewardTypeGame simpleRewardTypeGame){
//    RewardType rewardType = rewardTypeService.findByUrlAndName(simpleRewardTypeGame.url(), simpleRewardTypeGame.rewardTypeName());
//    RewardRevisionType rewardRevisionType = findOrCreateRewardRevisionType(rewardRevision, rewardType, true);
//    RewardRevisionTypeGame rewardRevisionTypeGame = rewardRevisionTypeGameRepository.findByRewardRevisionTypeIdAndGuidAndDeletedFalse(rewardRevisionType.getId(), simpleRewardTypeGame.guid());
//    if(rewardRevisionTypeGame == null){
//      rewardRevisionTypeGame = RewardRevisionTypeGame.builder()
//            .guid(simpleRewardTypeGame.guid())
//            .rewardRevisionType(rewardRevisionType)
//            .build();
//      rewardRevisionTypeGame = rewardRevisionTypeGameRepository.save(rewardRevisionTypeGame);
//    }
//
//    return rewardRevisionTypeGame;
//  }

  public RewardRevision findRevision(long id) {
    return rewardRevisionRepository.findOne(id);
  }

  public RewardRevision findCurrentRevision(Domain domain, Long rewardId) {
    Optional<Reward> reward = Optional.ofNullable(rewardRepository.findByDomainAndCurrentId(domain, rewardId));
    return (reward.isPresent()) ? reward.get().getCurrent() : null;
  }

  public List<RewardRevisionType> findRewardTypes(RewardRevision rewardRevision) {
    List<RewardRevisionType> revisionTypeList = rewardRevisionTypeService.findByRewardRevision(rewardRevision);
    //    List<RewardRevisionTypeValue> fieldValues = rewardRevisionTypeValueRepository.findByRewardRevision(rewardRevision);
    //    List<RewardRevisionTypeValue> distinctByRewardType = fieldValues.stream().filter(distinctByKey(fv -> fv.getRewardTypeField().getRewardType()))
    //        .collect(Collectors.toList());

    //    Set<RewardType> rewardTypes = ConcurrentHashMap.newKeySet();
    //    //    distinctByRewardType.forEach(fv -> rewardTypes.add(fv.getRewardTypeField().getRewardType()));
    //    log.debug("RewardRevisionTypeValue:: " + rewardTypes);
    return revisionTypeList;
  }

  public RewardRevisionTypeValue findRewardRevisionTypeValues(Long rewardRevisionTypeId, Long rewardTypeFieldId) {
    return rewardRevisionTypeValueRepository.findByRewardRevisionTypeIdAndRewardTypeFieldIdAndDeletedFalse(rewardRevisionTypeId, rewardTypeFieldId);
  }

  public List<RewardRevisionTypeValue> findByRewardRevisionType(Long rewardRevisionTypeId) {
    return rewardRevisionTypeValueRepository.findByRewardRevisionTypeIdAndDeletedFalse(rewardRevisionTypeId);
  }

  public List<RewardRevisionTypeGame> findGamesByRewardRevisionType(Long rewardRevisionTypeId) {
    return rewardRevisionTypeGameRepository.findByRewardRevisionTypeIdAndDeletedFalse(rewardRevisionTypeId);
  }

  public RewardRevision findRevisionByDomainAndCode(Domain domain, String code) {
    return rewardRevisionRepository.findByRewardDomainAndCode(domain, code);
  }

  public RewardRevisionType findOrCreateRewardRevisionType(RewardRevision rewardRevision, RewardType rewardType, boolean instant) {
    RewardRevisionType rewardRevisionType = RewardRevisionType.builder()
            .rewardRevision(rewardRevision)
            .rewardType(rewardType)
            .instant(instant)
            .build();

    return rewardRevisionTypeService.saveOrUpdate(rewardRevisionType);
  }

  public Reward findReward(Long id){
    return rewardRepository.findById(id).orElseThrow();
  }

  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  private void copy(RewardRevision from, RewardRevision to) {
    to.setReward(from.getReward());
    to.setName(from.getName());
    to.setDescription(from.getDescription());
    to.setEnabled(from.isEnabled());
    to.setCode(from.getCode());
    to.setValidFor(from.getValidFor());
    to.setValidForGranularity(from.getValidForGranularity());
    to.setActivationNotificationName(from.getActivationNotificationName());
    to = rewardRevisionRepository.save(to);

    List<RewardRevisionType> rewardRevisionTypes = new ArrayList<>();
    for (RewardRevisionType rewardRevisionType : from.getRevisionTypes()) {
      RewardRevisionType rewardRevisionTypeCopy = RewardRevisionType.builder()
            .rewardType(rewardRevisionType.getRewardType())
            .rewardRevision(to)
            .instant(rewardRevisionType.isInstant())
            .build();
      rewardRevisionTypeCopy = rewardRevisionTypeService.save(rewardRevisionTypeCopy);
      rewardRevisionTypes.add(rewardRevisionTypeCopy);

      List<RewardRevisionTypeValue> rewardRevisionTypeValues = findByRewardRevisionType(rewardRevisionType.getId());
      for(RewardRevisionTypeValue rewardRevisionTypeValue : rewardRevisionTypeValues){
        RewardRevisionTypeValue rewardRevisionTypeValueCopy = RewardRevisionTypeValue.builder()
              .rewardRevisionType(rewardRevisionTypeCopy)
              .rewardTypeField(rewardRevisionTypeValue.getRewardTypeField())
              .value(rewardRevisionTypeValue.getValue())
              .rewardRevisionType(rewardRevisionTypeCopy)
            .build();
        rewardRevisionTypeValueCopy = rewardRevisionTypeValueRepository.save(rewardRevisionTypeValueCopy);
      }

      List<RewardRevisionTypeGame> rewardRevisionTypeGames = findGamesByRewardRevisionType(rewardRevisionType.getId());
      for (RewardRevisionTypeGame rewardRevisionTypeGame : rewardRevisionTypeGames){
        RewardRevisionTypeGame rewardRevisionTypeGameCopy = RewardRevisionTypeGame.builder()
              .guid(rewardRevisionTypeGame.getGuid())
              .rewardRevisionType(rewardRevisionTypeCopy)
              .build();
        rewardRevisionTypeGameCopy = rewardRevisionTypeGameRepository.save(rewardRevisionTypeGameCopy);
      }
    }

    to.setRevisionTypes(rewardRevisionTypes);
    to = rewardRevisionRepository.save(to);
  }

  public void cancel(Reward reward)
  throws Exception
  {
    RewardRevision rewardRevision = reward.getCurrent();
    List<Long> promotionsLinkedToReward = promotionsClientService.promotionsLinkedToReward(rewardRevision.getId());
    if ((promotionsLinkedToReward != null) && (!promotionsLinkedToReward.isEmpty())) {
      log.error("{} promotion(s) linked to this reward. Cannot cancel.", promotionsLinkedToReward.size());
      //TODO: Implement proper Status code exception.
      throw new Exception(promotionsLinkedToReward.size()+" promotion(s) linked to this reward. Cannot cancel.");
    }
    playerRewardHistoryService.processRewardCancellation(rewardRevision);
    rewardRevision.setEnabled(false);
    rewardRevisionRepository.save(rewardRevision);
    log.debug("RewardRevision has been disabled: {}", rewardRevision.toShortString());
  }

  public boolean removeRevisionTypeGame(RewardRevisionType rewardRevisionType, String gameGuid) {
    RewardRevisionTypeGame rewardRevisionTypeGame = rewardRevisionTypeGameRepository.findByRewardRevisionTypeIdAndGuidAndDeletedFalse(rewardRevisionType.getId(), gameGuid);
    if(rewardRevisionTypeGame != null) {
      rewardRevisionTypeGame.setDeleted(true);
      rewardRevisionTypeGameRepository.save(rewardRevisionTypeGame);
    }

    log.debug("remove game from reward revision: {}", gameGuid);
    return true;
  }

  public List<RewardRevision> findRevisions(Reward reward) {
    return rewardRevisionRepository.findRewardRevisionsByReward(reward);
  }

  public Boolean removeRevisionTypeValue(RewardRevisionType rewardRevisionType, Long rewardRevisionTypeValueId) {
    Optional<RewardRevisionTypeValue> rewardRevisionTypeValue = rewardRevisionTypeValueRepository.findById(rewardRevisionTypeValueId);
    if(rewardRevisionTypeValue.isPresent()){
      RewardRevisionTypeValue value = rewardRevisionTypeValue.get();
      value.setDeleted(true);
      rewardRevisionTypeValueRepository.save(value);
    }

    return true;
  }

  public Response<ChangeLogs> changelogs(Reward reward, String[] entities, Integer page) throws Exception {
    return changeLogService.listLimited(ChangeLogRequest.builder()
            .entityRecordId(reward.getId())
            .page(page)
            .entities(entities)
            .build());
  }
}
