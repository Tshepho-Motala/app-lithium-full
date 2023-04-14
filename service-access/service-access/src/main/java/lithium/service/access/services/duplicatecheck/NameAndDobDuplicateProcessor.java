package lithium.service.access.services.duplicatecheck;

import java.util.List;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.UserDuplicatesTypes;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.DuplicateCheckRequestData;
import lithium.service.user.client.objects.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class NameAndDobDuplicateProcessor implements UserDuplicateProcessor {
  private final LithiumServiceClientFactory services;

  @Override
  public UserDuplicatesTypes getType() {
    return UserDuplicatesTypes.NAME_AND_DOB;
  }

  @Override
  public List<User> findDuplicates(User user) throws Status551ServiceAccessClientException {

    DuplicateCheckRequestData requestData = buildDuplicateCheckRequestData(user);

    try {
      UserApiInternalClient userApiInternalClient = services.target(UserApiInternalClient.class, true);

      return userApiInternalClient.findDuplicateUsers(requestData).getData();

    } catch (LithiumServiceClientFactoryException e) {
      log.error("Problem accessing UserApiInternal client", e);
      throw new Status551ServiceAccessClientException(e);
    }
  }

  private DuplicateCheckRequestData buildDuplicateCheckRequestData(User user) {
    return DuplicateCheckRequestData.builder()
        .domainName(user.getDomain().getName())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .dobDay(user.getDobDay())
        .dobMonth(user.getDobMonth())
        .dobYear(user.getDobYear())
        .userOwnerId(user.getId())
        .build();
  }
}
