package lithium.service.user.controllers;

import static java.util.Objects.nonNull;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.UserService;
import lithium.service.user.services.notify.VerificationChangeNotificationService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userapiinternal")
@Slf4j
public class UserApiInternalController {
  @Autowired
  AutoRestrictionTriggerStream autoRestrictionTriggerStream;
  @Autowired
  UserService userService;
  @Autowired
  ChangeLogService changeLogService;
  @Autowired
  VerificationChangeNotificationService verificationChangeNotificationService;
  @Autowired
  PubSubUserController pubSubUserController;
  @Autowired
  LithiumTokenUtilService tokenService;
  @Autowired
  PubSubUserService pubSubUserService;

    @PostMapping("/user/markHasSelfExcludedAndOptOutComms")
    public User markHasSelfExcludedAndOptOutComms(@RequestParam("guid") String guid, LithiumTokenUtil util)
            throws UserNotFoundException {
        User user = userService.findFromGuid(guid);
        if (user == null) throw new UserNotFoundException();

        //Get the old communications channel values before modifying the user object
        List<ChangeLogFieldChange> changeLogList = new ArrayList<>();
        addChangedCommsFieldToChangeLogFieldChanges(changeLogList,  user.getEmailOptOut(), "emailOptOut");
        addChangedCommsFieldToChangeLogFieldChanges(changeLogList,  user.getLeaderboardOptOut(), "leaderboardOptOut");
        addChangedCommsFieldToChangeLogFieldChanges(changeLogList,  user.getPostOptOut(), "postOptOut");
        addChangedCommsFieldToChangeLogFieldChanges(changeLogList,  user.getSmsOptOut(), "smsOptOut");
        addChangedCommsFieldToChangeLogFieldChanges(changeLogList,  user.getCallOptOut(), "callOptOut");
        addChangedCommsFieldToChangeLogFieldChanges(changeLogList,  user.getPushOptOut(), "pushOptOut");


      user.setHasSelfExcluded(true);
        user.setEmailOptOut(true);
        user.setPostOptOut(true);
        user.setSmsOptOut(true);
        user.setCallOptOut(true);
        user.setPushOptOut(true);
        user.setLeaderboardOptOut(true);

        //Save the user changes before the changelogs
        userService.save(user);

        try {
                      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), util.guid(), null, null, null, changeLogList,
              Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());
        } catch (Exception ex) {
          log.error("ChangeLogService could not register changes for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
        }
        try {
          PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder builder = PubSubMarketingPreferences.builder();
          builder.pushOptOut(true);
          builder.leaderBoardOptOut(true);
          builder.callOptOut(true);
          builder.smsOptOut(true);
          builder.emailOptOut(true);
          builder.postOptOut(true);
          pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.SYSTEM, builder, util, PubSubEventType.MARKETING_PREFERENCES);
        } catch (Exception ex) {
          log.error("markHasSelfExcludedAndOptOutComms failed for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
        }

      return user;
    }

    public void addChangedCommsFieldToChangeLogFieldChanges(List<ChangeLogFieldChange> fieldChanges, Boolean current, String field) {
      String currentValueString = current != null ? current.toString() : "false";
      String optOut = String.valueOf(true);

      if(!currentValueString.equalsIgnoreCase(optOut)) {
        fieldChanges.add(ChangeLogFieldChange.builder().field(field).fromValue(currentValueString).toValue(optOut).build());
      }
    }

    @PostMapping("/user/saveverificationstatus")
    public Response<User> updateVerificationStatus(@RequestBody UserVerificationStatusUpdate statusUpdate, Principal principal)
            throws UserNotFoundException, Exception {
        User user = userService.findFromGuid(statusUpdate.getUserGuid());
        if (user == null) {
            log.warn("User not found: " + statusUpdate.getUserGuid());
            throw new UserNotFoundException();
        }

        List<ChangeLogFieldChange> clfc = new ArrayList<>();

        if (nonNull(statusUpdate.getStatusId())) {

            Long oldStatus = user.getVerificationStatus();
            Long newStatus = statusUpdate.getStatusId();

            user.setVerificationStatus(statusUpdate.getStatusId());
            ChangeLogFieldChange c = ChangeLogFieldChange.builder()
                    .field("verificationStatus")
                    .fromValue(oldStatus != null ? oldStatus.toString() : "NOT SETUP")
                    .toValue(newStatus.toString())
                    .build();
            clfc.add(c);
        }

        if (nonNull(statusUpdate.getAgeVerified())) {
            Boolean oldAgeVerified = user.getAgeVerified();
            Boolean newAgeVerified = statusUpdate.getAgeVerified();

            user.setAgeVerified(newAgeVerified);
            ChangeLogFieldChange c = ChangeLogFieldChange.builder()
                    .field("ageVerified")
                    .fromValue(oldAgeVerified != null ? oldAgeVerified.toString() : "NOT SETUP")
                    .toValue(newAgeVerified.toString())
                    .build();
            clfc.add(c);
        }

        if (nonNull(statusUpdate.getAddressVerified())) {
            Boolean oldAddressVerified = user.getAddressVerified();
            Boolean newAddressVerified = statusUpdate.getAddressVerified();

            user.setAddressVerified(newAddressVerified);
            ChangeLogFieldChange c = ChangeLogFieldChange.builder()
                    .field("addressVerified")
                    .fromValue(oldAddressVerified != null ? oldAddressVerified.toString() : "NOT SETUP")
                    .toValue(newAddressVerified.toString())
                    .build();
            clfc.add(c);
        }

        LithiumTokenUtil token = tokenService.getUtil(principal);

        changeLogService.registerChangesForNotesWithFullNameAndDomain(
                "user",
                "edit",
                user.getId(),
                token.guid(),
                token,
                statusUpdate.getComment(),
                null,
                clfc,
                Category.ACCOUNT,
                SubCategory.KYC,
                1,
                user.domainName()
        );

        user = userService.save(user);

        if (nonNull(statusUpdate.getStatusId())) {
            autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder().userGuid(user.guid()).build());
            verificationChangeNotificationService.sendSmsAndEmailNotification(user, statusUpdate.getSendSms());
        }
        return Response.<User>builder().data(user).status(Response.Status.OK).build();
    }


    @GetMapping("/users/list/findByGuid")
    public Response<List<lithium.service.user.client.objects.User>> getUsers(@RequestParam List<String> guids) {
        try {
            List<lithium.service.user.client.objects.User> users = new ArrayList<>();
            for (String guid : guids) {
                lithium.service.user.client.objects.User user = getUser(guid).getData();
                if (user != null)
                    users.add(user);
            }
            return Response.<List<lithium.service.user.client.objects.User>>builder().data(users).status(Response.Status.OK).build();
        } catch (Exception e) {
            return Response.<List<lithium.service.user.client.objects.User>>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/findByGuid")
    @Retryable(maxAttempts = 6, backoff = @Backoff(delay = 100, multiplier = 2.0))
    public Response<lithium.service.user.client.objects.User> getUser(@RequestParam String guid) {
      User userEntity = userService.findFromGuid(guid);
      if (userEntity == null) return Response.<lithium.service.user.client.objects.User>builder().status(Status.NOT_FOUND).build();
      try {
        lithium.service.user.client.objects.User userClient = userService.convert(userEntity);
        return Response.<lithium.service.user.client.objects.User>builder().data(userClient).status(Response.Status.OK).build();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return Response.<lithium.service.user.client.objects.User>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
    }

    @GetMapping("/get-user-by-email")
    public Response<lithium.service.user.client.objects.User> getUserByEmail(
        @RequestParam("domainName") String domainName,
        @RequestParam("email") String email
    ) {
      try {
        List<User> userEntities = userService.findByDomainNameAndEmail(domainName, email);
        if (userEntities.size() != 1) {
          return Response.<lithium.service.user.client.objects.User>builder().status(Response.Status.NOT_FOUND).build();
        }
        lithium.service.user.client.objects.User userClient = userService.convert(userEntities.get(0));
        return Response.<lithium.service.user.client.objects.User>builder().data(userClient).status(Response.Status.OK).build();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return Response.<lithium.service.user.client.objects.User>builder().status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
    }

    @GetMapping("/getUserByCellphoneNumber")
    public Response<lithium.service.user.client.objects.User> getUserByCellphoneNumber(
            @RequestParam("domainName") String domainName,
            @RequestParam("cellphoneNumber") String cellphoneNumber
    ) {
        try {
            List<User> userEntities = userService.findByDomainNameAndMobile(domainName, cellphoneNumber);
          if (userEntities.size() != 1) {
            return Response.<lithium.service.user.client.objects.User>builder().status(Response.Status.NOT_FOUND).build();
          }
          lithium.service.user.client.objects.User userClient = userService.convert(userEntities.get(0));
          return Response.<lithium.service.user.client.objects.User>builder().data(userClient).status(Response.Status.OK).build();
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          return Response.<lithium.service.user.client.objects.User>builder().status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

  @RequestMapping("/system/getByUserId")
  public Response<lithium.service.user.client.objects.User> getUserById(@RequestParam("id") Long id)  {
    try {
      User userEntity = userService.findById(id);
      if (userEntity == null) throw new Exception("Could not find user by id = " + id);
      lithium.service.user.client.objects.User userClient = userService.convert(userEntity);
      return Response.<lithium.service.user.client.objects.User>builder().data(userClient).status(Response.Status.OK).build();
    } catch (Exception e) {
      log.error("Cant get user by Id="+id+". Internal error: "+e.getMessage(), e);
      return Response.<lithium.service.user.client.objects.User>builder().message(e.getMessage()).status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/user")
  public Response<lithium.service.user.client.objects.User> updateUser(
      @RequestBody Map<String, String> map
  ) {
    log.debug("Saving user :: " + map);
    try {
      Long userId = Long.parseLong(map.remove("id"));
      User user = userService.findOne(userId);
      if (user.getPostalAddress() == null) {
        user.setPostalAddress(new Address());
      }
            if (user.getResidentialAddress() == null) user.setResidentialAddress(new Address());
            log.debug("db user :: " + user);
            log.info("db user :: " + user.guid());
            for (String key : map.keySet()) {
                try {
                    if (key.indexOf('.') == -1) {
                        //TODO: I know, it's horrible. Don't have time to fix now.
                        if (key.startsWith("dob")) {
                            PropertyUtils.setProperty(user, key, Integer.parseInt(map.get(key)));
                            continue;
                        }
                        PropertyUtils.setProperty(user, key, map.get(key));
                    } else {
                        PropertyUtils.setNestedProperty(user, key, map.get(key));
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.error("Could not find field to update :: " + key + " : " + map.get(key));
                    throw new Exception("Could not update user information");
                }
            }
            try {
                user.getResidentialAddress().setUserId(user.getId());
                if (!user.getResidentialAddress().isComplete()) {
                    log.debug("Not Saving Incomplete ResidentialAddress (" + user.getDomain().getName() + "/" + user.getUsername() + ") : " + user.getResidentialAddress());
                    user.setResidentialAddress(null);
                }
                user.getPostalAddress().setUserId(user.getId());
                if (!user.getPostalAddress().isComplete()) {
                    log.debug("Not Saving Incomplete PostalAddress (" + user.getDomain().getName() + "/" + user.getUsername() + ") : " + user.getPostalAddress());
                    user.setPostalAddress(null);
                }
                user = userService.save(user);
                log.debug("updated user :: " + user);
                log.info("updated user :: " + user.guid());
                pubSubUserController.pushToPubSub(user.guid(),null);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Response.<lithium.service.user.client.objects.User>builder().message(e.getMessage()).status(Response.Status.INVALID_DATA).build();
            }
            // FIXME: 2019/10/21 Not sure why no user object is returned but not going to mess with it now.
            return Response.<lithium.service.user.client.objects.User>builder().status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Response.<lithium.service.user.client.objects.User>builder().message(e.getMessage()).status(Response.Status.INVALID_DATA).build();
        }
    }

  @RequestMapping(method = RequestMethod.GET, value = "/user/block-iban-mismatch-user")
  public Response blockIBANMismatchUser(@RequestParam("userGuid") String userGuid,
      @RequestParam("accoutNoteMessage") String accoutNoteMessage,
      @RequestParam("financeNoteMessage") String financeNoteMessage) {
    userService.blockIBANMismatchUser(userGuid, accoutNoteMessage, financeNoteMessage);
    return Response.builder().status(Response.Status.OK).build();
  }
}
