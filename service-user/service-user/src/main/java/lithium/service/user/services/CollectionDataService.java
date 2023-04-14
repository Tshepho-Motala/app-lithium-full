package lithium.service.user.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.data.entities.CollectionData;
import lithium.service.user.data.entities.CollectionDataRevision;
import lithium.service.user.data.entities.CollectionDataRevisionEntry;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.CollectionDataRepository;
import lithium.service.user.data.repositories.CollectionDataRevisionEntryRepository;
import lithium.service.user.data.repositories.CollectionDataRevisionRepository;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class CollectionDataService {

  public CollectionDataRevisionRepository collectionDataRevRepository;
  public CollectionDataRepository collectionDataRepository;
  public CollectionDataRevisionEntryRepository collectionDataRevEntryRepository;
  public ChangeLogService changeLogService;
  public UserService userService;
  public MessageSource messageSource;

@Autowired
  public CollectionDataService(CollectionDataRepository collectionDataRepository, CollectionDataRevisionRepository collectionDataRevRepository, CollectionDataRevisionEntryRepository collectionDataRevEntryRepository,
      UserService userService, ChangeLogService changeLogService, MessageSource messageSource) {
    this.collectionDataRepository = collectionDataRepository;
    this.collectionDataRevEntryRepository = collectionDataRevEntryRepository;
    this.collectionDataRevRepository = collectionDataRevRepository;
    this.userService = userService;
    this.changeLogService = changeLogService;
    this.messageSource = messageSource;
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public List<CollectionDataRevisionEntry> createOrUpdateCollectionData(PlayerBasic playerBasic, long userId) {

    User user = null;

    if (hasCollectionData(playerBasic)) {
      user = userService.findById(userId);
      if (ObjectUtils.isEmpty(user)) {
        return null;
      }
    }

    List<CollectionDataRevisionEntry> collectionDataRevisionEntries = new ArrayList<>();
    Locale locale = LocaleContextHolder.getLocale();
    /**
     * Check if the collection name or data key names are snake_case
     */
    final List<lithium.service.user.client.objects.CollectionData> collectionData = validateCollectionDataInput(playerBasic);

    CollectionDataRevision prevCollectionDataRevEntity = null;
    if (!ObjectUtils.isEmpty(user.getCurrentCollectionDataRevId())) {
      prevCollectionDataRevEntity = collectionDataRevRepository.findById(user.getCurrentCollectionDataRevId()).orElse(null);
    }
    CollectionDataRevision collectionDataRevEntity = collectionDataRevRepository.save(CollectionDataRevision.builder().user(user).creationDate(new Date()).build());
    user.setCurrentCollectionDataRevId(collectionDataRevEntity.getId());
    user = userService.save(user);

    for (lithium.service.user.client.objects.CollectionData cd : collectionData) {
      Map<String, String> playerBasicMap = cd.getData();
      String collectionName = cd.getCollectionName();
      Set<String> keySet = playerBasicMap.keySet();
      List<ChangeLogFieldChange> changeLogFieldChange = new ArrayList<>();
      for (String key : keySet) {
        String value = playerBasicMap.get(key);
        CollectionData myData = findOrCreateCollectionData(collectionName, key, value);
        if(!ObjectUtils.isEmpty(myData)) {
          Long previousRevId = !ObjectUtils.isEmpty(prevCollectionDataRevEntity) && !ObjectUtils.isEmpty(prevCollectionDataRevEntity.getId())
              ? prevCollectionDataRevEntity.getId() : 0L;
          CollectionDataRevisionEntry prevUserDataCollRevEntry = collectionDataRevEntryRepository.findByCollectionRevisionIdAndCollectionDataId(
              previousRevId, myData.getId());
          CollectionDataRevision collDataRevEntity = !ObjectUtils.isEmpty(prevUserDataCollRevEntry) && !ObjectUtils.isEmpty(prevUserDataCollRevEntry.getId())
                  ? prevUserDataCollRevEntry.getLastUpdatedRevision() : collectionDataRevEntity;
          createCollectionDataRevisionEntries(collDataRevEntity, collectionDataRevEntity, collectionDataRevisionEntries, myData);
          changeLogEntries(changeLogFieldChange, key, myData.getDataValue(), value, prevUserDataCollRevEntry);
        }
      }
      if(!changeLogFieldChange.isEmpty()) {
        boolean changed = !ObjectUtils.isEmpty(prevCollectionDataRevEntity);
        createCollectionDataChangelog(user.getId(), user.domainName(), user.guid(), changeLogFieldChange, cd.getCollectionName(), locale, changed);
      }
    }
    return collectionDataRevisionEntries;
  }

  private boolean hasCollectionData(PlayerBasic playerBasic) {
    if(!ObjectUtils.isEmpty(playerBasic.getCollectionData()) && !playerBasic.getCollectionData().isEmpty()) {
      return playerBasic.getCollectionData().stream()
          .filter(s -> s.getCollectionName() != null && !ObjectUtils.isEmpty(s.getData()) && s.getData().size() > 0).count() > 0;
    }
    return false;
  }

  private CollectionData findOrCreateCollectionData(String collectionName, String key, String value) {
    CollectionData myData = collectionDataRepository.findByCollectionNameAndDataKeyAndAndDataValue(collectionName, key, value);
    if (ObjectUtils.isEmpty(myData)) {
      CollectionData cData = CollectionData.builder()
          .dataKey(key)
          .dataValue(value)
          .collectionName(collectionName)
          .build();
      myData = collectionDataRepository.save(cData);
    }
    return myData;
  }

  public List<lithium.service.user.client.objects.CollectionData> validateCollectionDataInput(PlayerBasic pb) {
    List<lithium.service.user.client.objects.CollectionData> collectionData = new ArrayList<>();
    if(!ObjectUtils.isEmpty(pb) && !ObjectUtils.isEmpty(pb.getCollectionData())) {
      collectionData = pb.getCollectionData().stream().filter(s -> !ObjectUtils.isEmpty(s.getCollectionName()) && isSnakeCase(s.getCollectionName()) && !ObjectUtils.isEmpty(s.getData()) && isSnakeCase(s.getData())).toList();
      if(ObjectUtils.isEmpty(collectionData) || collectionData.size() <= 0) {
        throw new Status426InvalidParameterProvidedException(
            RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, pb.getDomainName(), "ERROR_DICTIONARY.MY_ACCOUNT.COLLECTION_DATA_INVALID_INPUT", "Data collection name or keys are not snake_case"));
      }
    }
    return collectionData;
  }

  private List<CollectionDataRevisionEntry> createCollectionDataRevisionEntries(CollectionDataRevision lastUpdatedRevision, CollectionDataRevision collectionDataRevEntity, List<CollectionDataRevisionEntry> collectionDataRevisionEntries, CollectionData collectionDataEntityUpdate) {
    CollectionDataRevisionEntry collectionDataRevEntry = CollectionDataRevisionEntry.builder()
        .collectionData(collectionDataEntityUpdate)
        .collectionRevision(collectionDataRevEntity)
        .lastUpdatedRevision(lastUpdatedRevision)
        .build();
    CollectionDataRevisionEntry savedEntries = collectionDataRevEntryRepository.save(collectionDataRevEntry);
    collectionDataRevisionEntries.add(savedEntries);
    return collectionDataRevisionEntries;
  }

  private void createCollectionDataChangelog(Long userId, String domainName, String userGuid, List<ChangeLogFieldChange> changeLogFieldChange, String collectionName, Locale locale,
      boolean changed) {
    String comment = messageSource.getMessage("SERVICE_USER.GLOBAL.DATA_COLLECTIONS_CHANGELOG", new Object[]{collectionName}, locale);
    changeLogService.registerChangesForNotesWithFullNameAndDomain("user.collectiondata." + collectionName.replace("_", ""), changed ? "edit" : "create", userId, userGuid, null, comment, null,
        changeLogFieldChange, Category.ACCOUNT, SubCategory.ACCOUNT_CREATION, 0, domainName);
  }

  private List<ChangeLogFieldChange> changeLogEntries(List<ChangeLogFieldChange> changeLogFieldChange, String key, String fromValue, String toValue,
      CollectionDataRevisionEntry prevUserDataCollRevEntry) {
    if(ObjectUtils.isEmpty(prevUserDataCollRevEntry)) {
      fromValue = null;
    }
    ChangeLogFieldChange clf = ChangeLogFieldChange.builder()
        .field(key)
        .fromValue(fromValue)
        .toValue(toValue)
        .build();
    changeLogFieldChange.add(clf);
    return changeLogFieldChange;
  }

  private boolean isSnakeCase(Map<String, String> collectionDataMap) {
    Set<String> keys = collectionDataMap.keySet();
    boolean result = false;
    for(String key : keys) {
      result = isSnakeCase(key);
      if(!result) {
        break;
      }
    }
    return result;
  }

  private boolean isSnakeCase(String key) {
    Pattern pattern = Pattern.compile("^[a-z]+(?:_[a-z]+)*$");
    if(StringUtil.isEmpty(key)) {
      return false;
    }
    Matcher matcher = pattern.matcher(key);
    return matcher.matches();
  }
}
