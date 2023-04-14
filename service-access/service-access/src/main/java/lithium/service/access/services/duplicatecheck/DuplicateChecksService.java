package lithium.service.access.services.duplicatecheck;

import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.UserDuplicatesTypes;
import lithium.service.access.data.repositories.ValueRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;


@Slf4j
@Service
public class DuplicateChecksService {

  private LithiumServiceClientFactory services;
  private ValueRepository valueRepository;
  private Map<UserDuplicatesTypes, UserDuplicateProcessor> duplicateProcessorsMap;

  public DuplicateChecksService(List<UserDuplicateProcessor> duplicateProcessors, LithiumServiceClientFactory services,
      ValueRepository valueRepository) {
    this.services = services;
    this.valueRepository = valueRepository;
    this.duplicateProcessorsMap = duplicateProcessors.stream()
        .collect(Collectors.toMap(UserDuplicateProcessor::getType, processor -> processor));
  }

  public Set<User> findUserDuplicates(String userGuid, lithium.service.access.data.entities.List list)
      throws Status551ServiceAccessClientException, UserDuplicateCheckFailedException {

    User user = getUser(userGuid);

    List<UserDuplicateProcessor> processors = getProcessors(list);

    Set<User> foundDuplicatedUsers = new HashSet<>();

    for (UserDuplicateProcessor processor : processors) {
      foundDuplicatedUsers.addAll(processor.findDuplicates(user));
    }
    return foundDuplicatedUsers;
  }

  private List<UserDuplicateProcessor> getProcessors(lithium.service.access.data.entities.List list) throws UserDuplicateCheckFailedException {
    List<UserDuplicateProcessor> processors = new ArrayList<>();
    List<UserDuplicatesTypes> typesNeedToCheck = findDuplicateTypes(list);
    for (UserDuplicatesTypes type : typesNeedToCheck) {
      UserDuplicateProcessor processor = getDuplicateProcessorByType(type).orElseThrow(UserDuplicateCheckFailedException::new);
      processors.add(processor);
    }
    return processors;
  }

  public Optional<UserDuplicateProcessor> getDuplicateProcessorByType(UserDuplicatesTypes type) {
    return ofNullable(duplicateProcessorsMap.get(type));
  }

  private List<UserDuplicatesTypes> findDuplicateTypes(lithium.service.access.data.entities.List list) {
    return valueRepository.findAllByList(list).stream()
        .map(value -> UserDuplicatesTypes.getFromName(value.getData()))
        .collect(Collectors.toList());
  }


  private User getUser(String userGuid) throws Status551ServiceAccessClientException {
    try {
      UserApiInternalClient userApiInternalClient = services.target(UserApiInternalClient.class, true);
      return userApiInternalClient.getUser(userGuid).getData();
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Problem accessing UserApiInternal client", e);
      throw new Status551ServiceAccessClientException(e);
    }
  }

}
