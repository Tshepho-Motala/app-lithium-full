package lithium.service.user.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lithium.client.changelog.ChangeLogService;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.CollectionDataRepository;
import lithium.service.user.data.repositories.CollectionDataRevisionEntryRepository;
import lithium.service.user.data.repositories.CollectionDataRevisionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;

@Slf4j
class CollectionDataServiceTest {
  private UserService userServiceMock = Mockito.mock(UserService.class);
  private CollectionDataRevisionRepository collectionDataRevRepositoryMock = Mockito.mock(CollectionDataRevisionRepository.class);
  private CollectionDataRevisionEntryRepository collectionDataRevEntryRepositoryMock = Mockito.mock(CollectionDataRevisionEntryRepository.class);
  private ChangeLogService changeLogServiceMock = Mockito.mock(ChangeLogService.class);
  private CollectionDataRepository collectionDataRepositoryMock = Mockito.mock(CollectionDataRepository.class);
  private MessageSource messageSource = Mockito.mock(MessageSource.class);
  private CollectionDataService collectionDataService = new CollectionDataService(collectionDataRepositoryMock, collectionDataRevRepositoryMock, collectionDataRevEntryRepositoryMock, userServiceMock, changeLogServiceMock, messageSource);
  private lithium.service.user.client.objects.CollectionData collectionData;
  private final String COLLECTION_NAME = "kickbox";
  private Map<String, User> userMap;
  private List<lithium.service.user.data.entities.CollectionDataRevision> collectionDataRevisionList = new ArrayList<>();
  private List<lithium.service.user.data.entities.CollectionData> collectionDataList = new ArrayList<>();
  private List<lithium.service.user.client.objects.CollectionData> collectionDataObjectList = new ArrayList<>();
  private List<lithium.service.user.data.entities.CollectionDataRevisionEntry> collectionDataRevisionEntryList = new ArrayList<>();
  private Domain d;

  @BeforeEach
  public void initialize() {
    userMap = new HashMap<>();
    d = Domain.builder()
        .name("livescore_nl")
        .id(1L)
        .build();

    String guid = d.getName() + "/" + 1;
    User user1 = User.builder()
        .id(1L)
        .guid(guid)
        .firstName("Test1")
        .username("testing_1")
        .domain(d)
        .email("testing1@wonderlabz.com")
        .lastName("Lithium")
        .build();
    userMap.put(guid, user1);
  }

  @Test
  void createCollectionDataUsingUser_Then_SetCollectionRevisionAndLastUpdatedRevisionId_To_CurrentRevision() {
    Map<String, String> data = new HashMap<>();
    data.put("result", "undeliverable");
    data.put("reason", "rejected_email");
    data.put("role", "false");
    data.put("free", "false");
    data.put("disposable", "false");
    data.put("accept_all", "false");
    data.put("did_you_mean", "bill.lumbergh@gmail.com");
    data.put("sendex", "0.23");
    data.put("email", "bill.lumbergh@gamil.com");
    data.put("user", "bill.lumbergh");
    data.put("domain", "gmail.com");
    data.put("success", "true");
    data.put("message", "null");

    collectionData = lithium.service.user.client.objects.CollectionData.builder()
        .collectionName(COLLECTION_NAME)
        .data(data)
        .build();
    collectionDataObjectList.add(collectionData);
    PlayerBasic playerBasic = PlayerBasic.builder()
        .id(1L)
        .firstName("Test1")
        .username("testing_1")
        .domainName("livescore_nl")
        .email("testing1@wonderlabz.com")
        .lastName("Lithium")
        .collectionData(collectionDataObjectList)
        .build();

    Mockito.when(userServiceMock.findById(Mockito.any(Long.class))).then( s -> {
      Long userId = (Long) s.getArguments()[0];
      User foundUser = userMap.get("livescore_nl/" + userId);
      return foundUser;
    });

    Mockito.when(collectionDataRevRepositoryMock.findFirstByUserIdOrderByIdDesc(Mockito.any(Long.class))).then(s -> null);

    Mockito.when(collectionDataRevRepositoryMock.save(Mockito.any(lithium.service.user.data.entities.CollectionDataRevision.class))).then( s -> {
      lithium.service.user.data.entities.CollectionDataRevision collectionDataRevision = (lithium.service.user.data.entities.CollectionDataRevision)s.getArguments()[0];
      Long id = collectionDataRevisionList.size() + 1L;
      collectionDataRevision.setId(id);
      collectionDataRevisionList.add(collectionDataRevision);
      return collectionDataRevision;
    });

    Mockito.when(userServiceMock.save(Mockito.any(User.class))).then( s -> {
      User user1 = (User)s.getArguments()[0];
      user1.setCurrentCollectionDataRevId(Long.parseLong("" + collectionDataRevisionList.size()));
      return user1;
    });

    Mockito.when(collectionDataRepositoryMock.save(Mockito.any(lithium.service.user.data.entities.CollectionData.class))).then(s -> {
      lithium.service.user.data.entities.CollectionData collectionData = (lithium.service.user.data.entities.CollectionData) s.getArguments()[0];
      Long id = collectionDataList.size() + 1L;
      collectionData.setId(id);
      return collectionData;
    });

    Mockito.when(collectionDataRevEntryRepositoryMock.save(Mockito.any(lithium.service.user.data.entities.CollectionDataRevisionEntry.class))).then(s -> {
      lithium.service.user.data.entities.CollectionDataRevisionEntry dataRevisionEntry = (lithium.service.user.data.entities.CollectionDataRevisionEntry) s.getArguments()[0];
      Long id = collectionDataRevisionEntryList.size() + 1L;
      dataRevisionEntry.setId(id);
      collectionDataRevisionEntryList.add(dataRevisionEntry);
      return dataRevisionEntry;
    });

    collectionDataService.createOrUpdateCollectionData(playerBasic, userMap.get("livescore_nl/1").getId());

    Assert.assertEquals(java.util.Optional.of(13).get(), java.util.Optional.of(collectionDataRevisionEntryList.size()).get());
    Assert.assertEquals(java.util.Optional.of(1L).get(), collectionDataRevisionEntryList.get(0).getId());
  }

  @Test
  public void When_CollectionDataKeysAreSuppliedInSnakeCase_Then_ReturnCollectionDataList() {
    Map<String, String> data = new HashMap<>();
    data.put("result", "undeliverable");
    data.put("reason", "rejected_email");
    data.put("role", "false");
    data.put("free", "false");
    data.put("disposable", "false");
    data.put("accept_all", "false");
    data.put("did_you_mean", "bill.lumbergh@gmail.com");
    data.put("sendex", "0.23");
    data.put("email", "bill.lumbergh@gamil.com");
    data.put("user", "bill.lumbergh");
    data.put("domain", "gmail.com");
    data.put("success", "true");
    data.put("message", "null");

    collectionData = lithium.service.user.client.objects.CollectionData.builder()
        .collectionName(COLLECTION_NAME)
        .data(data)
        .build();
    collectionDataObjectList.add(collectionData);
    PlayerBasic playerBasic = PlayerBasic.builder()
        .id(1L)
        .firstName("Test1")
        .username("testing_1")
        .domainName("livescore_nl")
        .email("testing1@wonderlabz.com")
        .lastName("Lithium")
        .collectionData(collectionDataObjectList)
        .build();
    List<lithium.service.user.client.objects.CollectionData> collectionData1 = collectionDataService.validateCollectionDataInput(playerBasic);
    Assert.assertEquals(true, !ObjectUtils.isEmpty(collectionData1));
    Assert.assertEquals(13, collectionData1.get(0).getData().size());
  }

  @Test
  public void When_CollectionDataKeysAreSuppliedNotAsSnakeCase_Then_ThrowStatus426InvalidParameterProvidedException() {
    Map<String, String> data = new HashMap<>();
    data.put("resultOfTheResult", "undeliverable");
    data.put("reason", "rejected_email");
    data.put("role", "false");
    data.put("free", "false");

    collectionData = lithium.service.user.client.objects.CollectionData.builder()
        .collectionName(COLLECTION_NAME)
        .data(data)
        .build();
    collectionDataObjectList.add(collectionData);
    PlayerBasic playerBasic = PlayerBasic.builder()
        .id(1L)
        .firstName("Test1")
        .username("testing_1")
        .domainName("livescore_nl")
        .email("testing1@wonderlabz.com")
        .lastName("Lithium")
        .collectionData(collectionDataObjectList)
        .build();
    Status426InvalidParameterProvidedException exception = Assert.assertThrows(Status426InvalidParameterProvidedException.class, ()-> {
      collectionDataService.validateCollectionDataInput(playerBasic);
    });

    Assert.assertEquals(426, exception.getCode());
  }
}
