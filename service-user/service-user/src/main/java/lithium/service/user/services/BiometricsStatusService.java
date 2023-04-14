package lithium.service.user.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.user.client.enums.BiometricsStatus;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.UserBiometricsStatusUpdate;
import lithium.service.user.data.entities.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class BiometricsStatusService {

  private final UserService userService;
  private final PubSubUserService pubSubUserService;
  private final ChangeLogService changeLogService;

  public User updateUserBiometricsStatus(UserBiometricsStatusUpdate userBiometricsStatusUpdate, Principal principal) {
    var user = userService.findFromGuid(userBiometricsStatusUpdate.getUserGuid());
    var author = userService.findFromGuid(userBiometricsStatusUpdate.getAuthorGuid());
    String authorFullName = author.getFirstName() + " " + author.getLastName();

    return updateUserBiometricsStatus(
        user,
        BiometricsStatus.fromValue(userBiometricsStatusUpdate.getBiometricsStatus()),
        userBiometricsStatusUpdate.getComment(),
        userBiometricsStatusUpdate.getAuthorGuid(),
        authorFullName,
        principal
    );
  }

  public User updateUserBiometricsStatus(User user, BiometricsStatus status, String comment, String authorGuid, String authorFullName, Principal principal) {

    var currentStatus = user.getBiometricsStatus();

    user.setBiometricsStatus(status);

    user = userService.save(user);

    log.debug("Biometrics status for userGuid:" + user.getGuid() + " was updated with:" + user.getBiometricsStatus());

    addChangeLogs(user, currentStatus, status, comment, authorGuid, authorFullName);

    sendToPubSub(user, principal);

    return user;
  }

  private void sendToPubSub(User user, Principal principal) {
    try {
      pubSubUserService.buildAndSendPubSubAccountChange(user, principal, PubSubEventType.ACCOUNT_UPDATE);
    } catch (Exception e) {
      log.warn("Can't sent pub-sub message due " + e.getMessage());
    }
  }

  private void addChangeLogs(
      User user,
      BiometricsStatus fromStatus,
      BiometricsStatus toStatus,
      String comment,
      String authorGuid,
      String authorFullName) {

    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field("biometrics_status")
        .fromValue(fromStatus.getValue())
        .toValue(toStatus.getValue())
        .build();
    clfc.add(c);

    changeLogService.registerChangesWithDomainAndFullName(
        "user",
        "edit",
        user.getId(),
        authorGuid,
        comment,
        null,
        clfc,
        Category.ACCOUNT,
        SubCategory.KYC,
        0,
        user.domainName(),
        authorFullName);
  }
}
