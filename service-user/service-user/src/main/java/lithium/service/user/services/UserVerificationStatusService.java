package lithium.service.user.services;

import static lithium.service.user.client.objects.User.SYSTEM_GUID;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.notify.VerificationChangeNotificationService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserVerificationStatusService {

  private final UserService userService;
  private final LimitInternalSystemService limitInternalSystemService;
  private final ChangeLogService changeLogService;
  private final LithiumTokenUtilService lithiumTokenUtilService;
  private final AutoRestrictionTriggerStream autoRestrictionTriggerStream;
  private final VerificationChangeNotificationService verificationChangeNotificationService;
  private final PubSubUserService pubSubUserService;

  public User updateVerificationStatus(boolean forceUpdate, UserVerificationStatusUpdate userVerificationStatusUpdate, Principal principal)
      throws Exception {

    if (forceUpdate) {
      return updateVerificationStatusNotConsiderParameters(userVerificationStatusUpdate, principal);
    } else {
      return updateVerificationStatusBasedOnParameters(userVerificationStatusUpdate, principal);
    }
  }

  private User updateVerificationStatusNotConsiderParameters(UserVerificationStatusUpdate userVerificationStatusUpdate, Principal principal)
      throws Exception {
    User user = userService.findOne(userVerificationStatusUpdate.getUserId());

    Long oldStatus = user.getVerificationStatus();
    String oldStatusCode = null;
    if (oldStatus != null) {
      oldStatusCode = limitInternalSystemService.getVerificationStatusCode(oldStatus);
    }

    Long newStatus = userVerificationStatusUpdate.getStatusId();
    String newStatusCode = limitInternalSystemService.getVerificationStatusCode(newStatus);
    user.setVerificationStatus(newStatus);

    // if the status changes to a Age Only Verified.
    if (newStatus.equals(VerificationStatus.AGE_ONLY_VERIFIED.getId())) {
      user.setAgeVerified(true);
    }
    if (newStatus.equals(VerificationStatus.UNVERIFIED.getId())) {
      user.setAgeVerified(false);
    }

    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    String oldStatusCodeLogName = "NOT SETUP";
    if (oldStatusCode != null) {
      oldStatusCodeLogName = oldStatusCode;
    }
    clfc.add(createLogField("verification_status", oldStatusCodeLogName, newStatusCode));

    return applyVerificationStatusUpdate(userVerificationStatusUpdate, principal, user, clfc);
  }

  private User updateVerificationStatusBasedOnParameters(UserVerificationStatusUpdate userVerificationStatusUpdate, Principal principal) {

    User user = userService.findOne(userVerificationStatusUpdate.getUserId());

    boolean actualUserAgeVerified = user.getAgeVerified();
    boolean actualAddressVerified = user.getAddressVerified();

    Boolean newAgeVerified = userVerificationStatusUpdate.getAgeVerified();
    Boolean newAddressVerified = userVerificationStatusUpdate.getAddressVerified();

    if (!needToUpdate(actualAddressVerified, newAddressVerified) && !needToUpdate(actualUserAgeVerified, newAgeVerified)) {
      return user;
    }

    List<ChangeLogFieldChange> clfc = new ArrayList<>();

    if (needToUpdate(actualUserAgeVerified, newAgeVerified)) {
      user.setAgeVerified(newAgeVerified);
      clfc.add(createLogField("ageVerified", String.valueOf(actualUserAgeVerified), String.valueOf(newAgeVerified)));
      log.info(user.getGuid() + " AgeVerified changed from:" + actualUserAgeVerified + " to:" + newAgeVerified + " by:"
          + userVerificationStatusUpdate.getAuthorName());
    }

    if (needToUpdate(actualAddressVerified, newAddressVerified)) {
      user.setAddressVerified(newAddressVerified);
      clfc.add(createLogField("addressVerified", String.valueOf(actualAddressVerified), String.valueOf(newAddressVerified)));
      log.info(user.getGuid() + " AddressVerified changed from:" + actualAddressVerified + " to:" + newAddressVerified + " by:"
          + userVerificationStatusUpdate.getAuthorName());
    }

    VerificationStatus verificationStatus = resolveVerificationStatus(user.getVerificationStatus(),
        Optional.ofNullable(newAgeVerified).orElse(actualUserAgeVerified),
        Optional.ofNullable(newAddressVerified).orElse(actualAddressVerified)
    );

    if (verificationStatus.getId() != user.getVerificationStatus()) {
      String actualVerificationStatusCode = limitInternalSystemService.getVerificationStatusCode(user.getVerificationStatus());
      user.setVerificationStatus(verificationStatus.getId());
      clfc.add(createLogField("verification_status", actualVerificationStatusCode, verificationStatus.name()));
      log.info(user.getGuid() + " VerificationStatus changed from:" + actualVerificationStatusCode + " to:" + verificationStatus.name() + " by:"
          + userVerificationStatusUpdate.getAuthorName());
    }

    return applyVerificationStatusUpdate(userVerificationStatusUpdate, principal, user, clfc);

  }

  private static boolean needToUpdate(boolean actualStatus, Boolean newStatus) {
    if (newStatus == null) {
      return false;
    }
    return actualStatus != newStatus;
  }

  private User applyVerificationStatusUpdate(UserVerificationStatusUpdate userVerificationStatusUpdate, Principal principal, User user,
      List<ChangeLogFieldChange> clfc) {
    user = userService.save(user);

    autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder().userGuid(user.guid()).build());

    try {

      LithiumTokenUtil tokenUtil = null;
      if (!SYSTEM_GUID.equals(userVerificationStatusUpdate.getAuthorName())) {
        tokenUtil = lithiumTokenUtilService.getUtil(principal);
      }

      changeLogService.registerChangesForNotesWithFullNameAndDomain(
          "user",
          "edit",
          user.getId(),
          userVerificationStatusUpdate.getAuthorName(),
          tokenUtil,
          userVerificationStatusUpdate.getComment(),
          null,
          clfc,
          Category.ACCOUNT,
          SubCategory.KYC,
          0,
          user.domainName());
    } catch (Exception e) {
      log.error("Unable to log status change: " + e.getMessage());
    }

    verificationChangeNotificationService.sendSmsAndEmailNotification(user);

    try {
      pubSubUserService.buildAndSendPubSubAccountChange(user, principal, PubSubEventType.ACCOUNT_UPDATE);
    } catch (Exception e) {
      log.warn("can't sent pub-sub message" + e.getMessage());
    }

    return user;
  }

  private static VerificationStatus resolveVerificationStatus(long actualVerificationStatus, boolean ageVerified, boolean addressVerified) {

    if (wasVerified(actualVerificationStatus)
        && !ageVerified && addressVerified) {
      return VerificationStatus.MANUALLY_VERIFIED;
    }

    if (addressVerified && ageVerified) {
      return VerificationStatus.MANUALLY_VERIFIED;
    } else if (!addressVerified && ageVerified) {
      return VerificationStatus.AGE_ONLY_VERIFIED;
    } else {
      return VerificationStatus.UNVERIFIED;
    }
  }


  private static boolean wasVerified(long actualVerificationStatus) {
    return VerificationStatus.MANUALLY_VERIFIED.getId() == actualVerificationStatus;
  }

  private static ChangeLogFieldChange createLogField(String fieldName, String actualValue, String newValue) {
    return ChangeLogFieldChange.builder()
        .field(fieldName)
        .fromValue(actualValue)
        .toValue(newValue)
        .build();
  }
}
