package lithium.service.user.services;

import static lithium.service.user.client.objects.User.SYSTEM_GUID;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.LithiumMetricsService;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.EcosystemRelationshipTypes;
import lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship;
import lithium.service.translate.client.objects.LoginError;
import lithium.service.translate.client.objects.Module;
import lithium.service.user.client.objects.AccountStatusErrorCodeAndMessage;
import lithium.service.user.client.objects.AutoRegistration;
import lithium.service.user.controllers.UserProviderController;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.GRD;
import lithium.service.user.data.entities.Group;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserPasswordHashAlgorithm;
import lithium.service.user.data.repositories.AddressRepository;
import lithium.service.user.data.repositories.GRDRepository;
import lithium.service.user.services.notify.FailedLoginBlockNotificationService;
import lithium.util.Hash;
import lithium.util.HashSaltPosition;
import lithium.util.PasswordHashing;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class UserProviderService {

  AddressRepository addressRepository;
  CachingDomainClientService cachingDomainClientService;
  ChangeLogService changeLogService;
  FailedLoginBlockNotificationService failedLoginBlockNotificationService;
  GRDRepository grdRepository;
  LimitService limitService;
  LoginEventService loginEventService;
  LithiumMetricsService metrics;
  ModelMapper modelMapper;
  SignupService signupService;
  UserApiTokenService userApiTokenService;
  UserLinkService userLinkService;
  @Autowired
  UserProviderController userProviderController; // This will be fixed after the class rework
  UserService userService;
  @Value("${lithium.password.salt}")
  private String passwordSalt;
  MessageSource messageSource;
  DomainService domainService;
  UserEventService userEventService;
  UserPasswordHashAlgorithmService userPasswordHashAlgorithmService;

  @Autowired
  public UserProviderService(
      AddressRepository addressRepository,
      CachingDomainClientService cachingDomainClientService,
      ChangeLogService changeLogService,
      FailedLoginBlockNotificationService failedLoginBlockNotificationService,
      GRDRepository grdRepository,
      LimitService limitService,
      LoginEventService loginEventService,
      LithiumMetricsService metrics,
      ModelMapper modelMapper,
      SignupService signupService,
      UserApiTokenService userApiTokenService,
      UserLinkService userLinkService,
      UserService userService,
      MessageSource messageSource,
      DomainService domainService,
      UserEventService userEventService,
      UserPasswordHashAlgorithmService userPasswordHashAlgorithmService) {
    this.addressRepository = addressRepository;
    this.cachingDomainClientService = cachingDomainClientService;
    this.changeLogService = changeLogService;
    this.failedLoginBlockNotificationService = failedLoginBlockNotificationService;
    this.grdRepository = grdRepository;
    this.limitService = limitService;
    this.loginEventService = loginEventService;
    this.metrics = metrics;
    this.modelMapper = modelMapper;
    this.signupService = signupService;
    this.userApiTokenService = userApiTokenService;
    this.userLinkService = userLinkService;
    this.userService = userService;
    this.messageSource = messageSource;
    this.domainService = domainService;
    this.userEventService = userEventService;
    this.userPasswordHashAlgorithmService = userPasswordHashAlgorithmService;
  }

  private User userChangeLog(User user) {
    try {
      User userCopy = null;
      String type = "";
      if (user.getId() != null) {
        type = "edit";
        userCopy = userService.findOne(user.getId());
      } else {
        type = "create";
        userCopy = new User();
        user = userService.save(user);
        boolean bonusSaved = signupService.registerForSignupBonus(
            (user.getBonusCode() != null) ? user.getBonusCode() : "",
            user.guid(),
            null
        );
        if (!bonusSaved) {
          log.warn("Bonus not saved for : " + user.guid());
        }
        if (bonusSaved) {
          log.info("Bonus saved for : " + user.guid());
        }
      }
      List<ChangeLogFieldChange> clfc = changeLogService.copy(user, userCopy, new String[]{"firstName", "lastName",
          "countryCode", "email", "telephoneNumber", "cellphoneNumber", "dobDay", "dobMonth", "dobYear", "emailValidated", "cellphoneValidated"});
      if (!clfc.isEmpty()) {
        changeLogService.registerChangesForNotesWithFullNameAndDomain("user", type, user.getId(), SYSTEM_GUID, null,
            user.getComments(), null, clfc, Category.ACCOUNT,  SubCategory.ACCOUNT_CREATION, 1, user.domainName());
      }
    } catch (Exception e) {
      log.error("Could not save user update changelog. [" + user + "]", e);
    }
    return user;
  }

  public User save(User user) {
    if (user.getId() != null) {
      userChangeLog(user);
      return userService.save(user);
    } else {
      return userChangeLog(user);
    }
  }

  public Address saveAddress(Address address) {
    return addressRepository.save(address);
  }

  public User obfuscateUserData(String domainName, String username) {
    return obfuscateUserData(userService.findByUsernameThenEmailThenCell(domainName, username));
  }

  public void blockUserForExcessiveFailedLogins(lithium.service.user.client.objects.User user) {
    log.debug("Start blockUserForExcessiveFailedLogins: " + user);
    lithium.service.user.data.entities.User userEntity = null;
    if ((user != null) && (user.getId() != null)) {
      userEntity = userService.findOne(user.getId());
    }

    if (userEntity != null) {
      Boolean oldValue = userEntity.getExcessiveFailedLoginBlock();
      //The user block state is not really changing, just return.
      if (oldValue != null && oldValue.booleanValue() == true) {
        return;
      }
      userEntity.setExcessiveFailedLoginBlock(true);
      userEntity = userService.save(userEntity);
      failedLoginBlockNotificationService.sendEmailAndCellphoneUserBlockNotification(user);
      try {
        List<ChangeLogFieldChange> clfc = new ArrayList<>();
        clfc.add(ChangeLogFieldChange.builder()
            .field("excessiveFailedLoginBlock")
            .fromValue(oldValue == null ? "false" : oldValue.toString())
            .toValue("true")
            .build());
        if (!clfc.isEmpty()) {
          changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "excessiveLoginBlock", user.getId(), SYSTEM_GUID, null,
              user.getComments(), null, clfc, Category.ACCOUNT, SubCategory.FAILED_LOGINS, 1, user.getDomain().getName());
        }
      } catch (Exception ex) {
        log.error("Unable to perform changelog change for excessive login block on user: " + user, ex);
      }
    } else {
      log.debug("Failed to get user entity in blockUserForExcessiveFailedLogins: " + user);
    }

    log.debug("Complete blockUserForExcessiveFailedLogins: " + userEntity);
  }

  public User obfuscateUserData(User user) {
    if (user.getEmail() != null) {
      user.setDeletedEmail(user.getEmail());
      user.setEmail(null);
      user.setEmailValidated(false);
    }
    if (user.getTelephoneNumber() != null) {
      user.setDeletedTelephoneNumber(user.getTelephoneNumber());
      user.setTelephoneNumber(null);
    }
    if (user.getCellphoneNumber() != null) {
      user.setDeletedCellphoneNumber(user.getCellphoneNumber());
      user.setCellphoneNumber(null);
      user.setCellphoneValidated(false);
    }
    user.setDeleted(true);
    user.setUpdatedDate(new Date());
    user.setStatus(userService.findStatus(lithium.service.user.client.enums.Status.BLOCKED.statusName()));
    user.setStatusReason(userService.findStatusReason(lithium.service.user.client.enums.StatusReason.PLAYER_REQUEST.statusReasonName()));
    user.setEmailOptOut(true);
    user.setPostOptOut(true);
    user.setSmsOptOut(true);
    user.setCallOptOut(true);
    user = userService.save(user);
    return user;
  }

  /**
   * Applies the logic required to make the login process ecosystem-aware
   *
   * @param domainName
   * @param username
   * @param password
   * @param isSuccessful
   * @return The user response or null in the case where no ecosystem outcome is required
   * @throws Exception
   */
  public Response<lithium.service.user.client.objects.User> applyEcosystemLogicToLogin(
      String domainName,
      String username,
      String password,
      boolean isSuccessful,
      String ipAddress,
      String userAgent,
      String deviceId,
      Map<String, String> extraParameters,
      String locale) throws Exception {
    if (!isSuccessful && cachingDomainClientService.isDomainInAnyEcosystem(domainName)) {
      for (String domainItr : cachingDomainClientService.listDomainNamesInEcosystemByDomainName(domainName)) {
        Response<lithium.service.user.client.objects.User> userItrLocal = findLocalUser(domainItr, username);
        // There is a user in the ecosystem, just not on the specific domain
        if (!ObjectUtils.isEmpty(userItrLocal) && !ObjectUtils.isEmpty(userItrLocal.getData())) {
          // Workaround to get past isSuccessful checks when a players linked user status is not OPEN
          userItrLocal.setStatus(null);
          if (validatePassword(password, userItrLocal)) {
            // Password matches, so that means it is the legitimate owner of the account
            // so we can respond with upgrade response and frontend can do whatever they like with it
            ArrayList<EcosystemDomainRelationship> ecosystemDomainRelationships = cachingDomainClientService
                .listEcosystemDomainRelationshipsByDomainName(domainName);
            // Check for possible root domain scenario
            Optional<EcosystemDomainRelationship> possibleRootDomainRel = ecosystemDomainRelationships.stream()
                .filter(ecoDomRel -> {
                  if (ecoDomRel.getDomain().getName().equalsIgnoreCase(domainName)) {
                    // Its a root domain, thus it needs to be auto-registered
                    return ecoDomRel.getRelationship().getCode().contentEquals(EcosystemRelationshipTypes.ECOSYSTEM_ROOT.key());
                  }
                  return false;
                }).findAny();
            // Root domain scenario
            if (possibleRootDomainRel.isPresent()) {
              //TODO: create function to copy user data into pb, similar to user data sync (will work as is for now with sync hack)
              lithium.service.domain.client.objects.Domain rootDomain = possibleRootDomainRel.get().getDomain();
              User user = userService.findOne(userItrLocal.getData().getId());
              AutoRegistration existingUserInEcosystem = AutoRegistration.builder().password(password).deviceId(deviceId).userAgent(userAgent).ipAddress(ipAddress).build();

              // This will auto create the bi-directional User Links that is required via queue, the auto register is still needed to be done in the login thread, and should be skipped on the auto create in registration success  queue
              final User registeredUser = signupService.createUserOnRootEcosystemDomain(rootDomain.getName(), user, existingUserInEcosystem);
              //Triggering the auto-restrictions rulset execution synchronously to ensure restriction are placed on root from exclusive user
              limitService.triggerAutoRestrictions(user.guid());
              userEventService.streamUserRegistrationSuccessEvent(user.getId(), ipAddress, userAgent , deviceId, password);

              //Do some nice recursion to auth properly
              return userProviderController.auth(
                  registeredUser.getDomain().getName(),
                  username,
                  password,
                  ipAddress,
                  userAgent,
                  extraParameters,
                  locale);
            }

            if (cachingDomainClientService.isDomainNameOfEcosystemMutuallyExclusiveType(domainName)) {
              // First check if the user exist on another exclusive domain
              String ecosystemName = cachingDomainClientService.findEcosystemNameByEcosystemRootDomainName(domainName).get().getEcosystem().getName(); //Already inside an ecosystem
              Optional<String> registeredExclusiveDomainName = domainService.getRegisteredExclusiveDomainName(ecosystemName, username);
              if (registeredExclusiveDomainName.isPresent()) {
                return Response.<lithium.service.user.client.objects.User>builder()
                    .status(Status.MUTUAL_EXCLUSIVE_DOMAIN_EXIST)
                    .message(LoginError.USER_EXIST_IN_ECOSYSTEM_IN_OTHER_DOMAIN.getResponseMessageLocal(messageSource, domainName, new Object[] {registeredExclusiveDomainName.get()}))
                    .data(userItrLocal.getData())
                    .build();
              }
              // Non-root domain scenario, upgrade account.
              return Response.<lithium.service.user.client.objects.User>builder()
                  .status(Status.ACCOUNT_UPGRADE_REQUIRED)
                  .message(Module.SERVICE_USER.getResponseMessageLocal(messageSource, domainName, "SERVICE_USER.ACCOUNT_UPGRADE_REQUIRED", "You don't have an account on the current brand, please perform a registration upgrade."))
                  .data(userItrLocal.getData())
                  .build();
            }
          }
        }
      }
    }
    return null;
  }

  public boolean validatePassword(String password, Response<lithium.service.user.client.objects.User> localUser)
      throws Status500InternalServerErrorException {
    boolean success = false;
    if (localUser.isSuccessful() && localUser.getData() != null) {
      if (localUser.getData().getPasswordHash() != null) {
        if (!localUser.getData().getPasswordHash().isEmpty()) {
          Optional<UserPasswordHashAlgorithm> optUpHashAlgorithm = userPasswordHashAlgorithmService.getUserPasswordHashAlgorithm(
              localUser.getData().getId());
          String hashedPassword;
          if (optUpHashAlgorithm.isPresent()) {
            UserPasswordHashAlgorithm upHashAlgorithm = optUpHashAlgorithm.get();

            hashedPassword = PasswordHashing.hashPassword(password,
                Hash.Type.fromAlgorithm(upHashAlgorithm.getHashAlgorithm().algorithm()),
                upHashAlgorithm.getSalt(),
                HashSaltPosition.POST);
          } else if (localUser.getData().getPasswordHash().startsWith("st:")) {
            hashedPassword = PasswordHashing.hashPassword(password, passwordSalt);
          } else {
            hashedPassword = PasswordHashing.hashPassword(password, null);
          }

          if (localUser.getData().getPasswordHash().equals(hashedPassword)) {
            success = true;
          }
        }
      }
      if (localUser.getData().getPasswordPlaintext() != null) {
        if (!localUser.getData().getPasswordPlaintext().isEmpty()) {
          if (localUser.getData().getPasswordPlaintext().equals(password)) {
            success = true;
          }
        }
      }
    }
    return success;
  }

  public Response<lithium.service.user.client.objects.User> findLocalUser(String domain, String username) throws Exception {
    return metrics.timer(log).time("findLocalUser." + domain, (StopWatch sw) -> {
      log.debug("findLocalUser(domain: " + domain + " username: " + username + ")");
      sw.start("user: " + username);
      sw.stop();
      Response<lithium.service.user.client.objects.User> responseEntity = null;
      lithium.service.user.data.entities.User userEntity = userService.findByUsernameThenEmailThenCell(domain.toLowerCase(), username);
      if (userEntity == null) {
        responseEntity = Response.<lithium.service.user.client.objects.User>builder().status(Status.NOT_FOUND).build();
      } else {
        if (userEntity.isDeleted()) {
          responseEntity = Response.<lithium.service.user.client.objects.User>builder()
              .status(Status.DISABLED)
              .message(Module.SERVICE_USER.getResponseMessageLocal(messageSource, domain, "SERVICE_USER.ACCOUNT_DISABLED_MESSAGE", "Your account is currently disabled. Please contact customer support."))
              .build();
        } else if ((userEntity.getStatus() != null) && (!userEntity.getStatus().getUserEnabled())) {
          AccountStatusErrorCodeAndMessage errorCodeAndMessage = userService.getAccountStatusErrorCodeAndMessage(userEntity);
          lithium.service.user.client.objects.User userClient = modelMapper.map(userEntity, lithium.service.user.client.objects.User.class);
          responseEntity = Response.<lithium.service.user.client.objects.User>builder()
            .data(userClient)
            .data2(errorCodeAndMessage)
            .status(Status.CUSTOM.id(errorCodeAndMessage.getErrorCode()))
            .message(errorCodeAndMessage.getErrorMsg())
            .build();
        } else {
          List<Group> sourceGroups = new ArrayList<>();
          for (Group group : userEntity.getGroups()) {
            List<GRD> sourceGrds = grdRepository.findByGroup(group);
            group.setGrds(sourceGrds);
            sourceGroups.add(group);
          }
          userEntity.setGroups(sourceGroups);
          lithium.service.user.client.objects.User userClient = modelMapper.map(userEntity, lithium.service.user.client.objects.User.class);
          // TODO: 2019/07/19 Combine below to avoid double call to API token service
          userClient.setApiToken(
              userApiTokenService.saveApiToken(userClient.guid(), userApiTokenService.findOrGenerateApiToken(userClient.guid())).getToken());
          userClient.setShortGuid(userApiTokenService.findOrGenerateShortGuid(userClient.guid()));
          //// FIXME: 2019/09/03 This is where the shared code should come and live for /auth and /user methods for accessrules if it should be in both, I guess
          //userTarget.setGroups(buildResponseGroups(userSource.getGroups()));
          responseEntity = Response.<lithium.service.user.client.objects.User>builder().status(Status.OK).data(userClient).build();
        }
      }
      return responseEntity;
    });
  }
}
