package lithium.service.user.controllers;

import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;
import static lithium.service.user.client.objects.User.SYSTEM_GUID;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.geo.client.GeoClient;
import lithium.service.geo.client.objects.Country;
import lithium.service.leaderboard.client.LeaderboardClient;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.pushmsg.client.PushMsgClient;
import lithium.service.user.client.enums.BiometricsStatus;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.AddressBasic;
import lithium.service.user.client.objects.LoginEventFE;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.client.objects.StatusUpdate;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.client.objects.UserChanges;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.Group;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.StatusReason;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import lithium.service.user.data.repositories.AddressRepository;
import lithium.service.user.data.repositories.GroupRepository;
import lithium.service.user.data.repositories.LoginEventRepository;
import lithium.service.user.data.repositories.StatusReasonRepository;
import lithium.service.user.data.repositories.StatusRepository;
import lithium.service.user.data.specifications.LoginEventSpecification;
import lithium.service.user.services.AccessRuleService;
import lithium.service.user.services.BiometricsStatusService;
import lithium.service.user.services.EmailValidationService;
import lithium.service.user.services.LoginEventService;
import lithium.service.user.services.PromotionsOptService;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.UserEventService;
import lithium.service.user.services.UserLinkService;
import lithium.service.user.services.UserProviderService;
import lithium.service.user.services.UserService;
import lithium.service.user.services.UserStatusService;
import lithium.service.user.services.UserVerificationStatusService;
import lithium.service.user.services.notify.PreValidationNotificationService;
import lithium.service.user.services.notify.VerificationChangeNotificationService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/{domain}/users/{id}")
public class UserController {
  @Autowired AutoRestrictionTriggerStream autoRestrictionTriggerStream;
  @Autowired AddressRepository addressRepository;
  @Autowired GroupRepository groupRepository;
  @Autowired StatusRepository statusRepository;
  @Autowired StatusReasonRepository statusReasonRepository;
  @Autowired ChangeLogService changeLogService;
  @Autowired ModelMapper modelMapper;
  @Autowired TokenStore tokenStore;
  @Autowired LithiumTokenUtilService tokenService;
  @Autowired LithiumServiceClientFactory services;
  @Autowired LoginEventRepository loginEventRepository;
  @Autowired UserStatusService userStatusService;
  @Autowired UserEventService userEventService;
  @Autowired UserProviderService userProviderService;
  @Autowired UserService userService;
  @Autowired AccessRuleService accessRuleService;
  @Autowired PreValidationNotificationService preValidationNotificationService;
  @Autowired LoginEventService loginEventService;
  @Autowired VerificationChangeNotificationService verificationChangeNotificationService;
  @Autowired PubSubUserService pubSubUserService;
  @Autowired UserLinkService userLinkService;
  @Autowired MessageSource messageSource;
  @Autowired UserVerificationStatusService verificationStatusService;
  @Autowired CachingDomainClientService cachingDomainClientService;
  @Autowired EmailValidationService emailValidationService;
  @Autowired BiometricsStatusService biometricsStatusService;


  @GetMapping
  public Response<User> get(@PathVariable("id") Long id, Authentication authentication) {
    log.debug("Retrieving User with id : " + id);
    User user = userService.findOne(id);
    if (ObjectUtils.isEmpty(user)) {
      return Response.<User>builder().status(NOT_FOUND).build();
    }

    if (cachingDomainClientService.isDomainNameOfEcosystemRootType(user.domainName())
        && !ObjectUtils.isEmpty(userLinkService.performDeletedLinkChecks(user))) {
      return Response.<User>builder().status(NOT_FOUND).build();
    }

    LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, authentication).build();

    if (tokenUtil.hasRolesForDomain(user.getDomain().getName(), "USER_VIEW", "PLAYER_VIEW")) {
      // Never send the password over the wire.
      user.clearPassword();
      log.debug(""+user);
      return Response.<User>builder().data(user).build();
    } else {
      return Response.<User>builder().data(null).status(NOT_FOUND).build();
    }
  }

  //FIXME: Move business logic to Service
  @PostMapping(value = "/savestatus")
  public Response<User> saveStatus(@RequestBody @Valid StatusUpdate statusUpdate, LithiumTokenUtil util) throws Exception {
    User user = userService.findOne(statusUpdate.getUserId());
    Status status = statusRepository.findOne(statusUpdate.getStatusId());
    StatusReason reason = null;
    boolean isSelfExcluded = false;
    String selfExclusionCreatedDate = null;
		if (statusUpdate.getStatusReasonId() != null) {
			reason = statusReasonRepository.findOne(statusUpdate.getStatusReasonId());
			if (reason.getName().equals(lithium.service.user.client.enums.StatusReason.SELF_EXCLUSION.statusReasonName())) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        isSelfExcluded = true;
        selfExclusionCreatedDate = (formatter.format(date));
      }
		}

    // FIXME: Add the appropriate category and sub-category here. I just changed the method signature hence the change,
    //        previous entry had the equivalent of null.
    user = userService.changeUserStatus(
        UserAccountStatusUpdate.builder()
            .userGuid(user.guid())
            .authorGuid(util.guid())
            .statusName(status.getName())
            .statusReasonName((reason == null)? "": reason.getName())
            .selfExcluded(isSelfExcluded)
            .selfExclusionCreated(selfExclusionCreatedDate)
            .comment(statusUpdate.getComment())
            .noteCategoryName(Category.ACCOUNT.getName())
            .noteSubCategoryName(SubCategory.STATUS_CHANGE.getName())
            .notePriority(70)
            .build(),
        util
    );

    // Triggering auto-restrictions on user status changes
    autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder().userGuid(user.guid()).build());

    return Response.<User>builder().status(OK).data(user).build();
  }

  @PostMapping("/saveverificationstatus")
  public Response<User> saveVerificationStatus(@RequestBody @Valid UserVerificationStatusUpdate userVerificationStatusUpdate, Principal principal)
      throws Exception {
    userVerificationStatusUpdate.setAuthorName(tokenService.getUtil(principal).guid());
    return Response.<User>builder()
        .status(OK)
        .data(verificationStatusService.updateVerificationStatus(true, userVerificationStatusUpdate, principal))
        .build();
  }

  @PutMapping("/biometrics-status")
  public Response<User> updateBiometricsStatus(@RequestBody @Valid StatusUpdate statusUpdate,  LithiumTokenUtil util) {
    log.debug("Biometrics Status change request:" + statusUpdate + " from:" + util.guid());
    var user = userService.findOne(statusUpdate.getUserId());
    BiometricsStatus newBiometricsStatus = BiometricsStatus.fromValue(statusUpdate.getStatusName());
    String comment = statusUpdate.getComment();
    return Response.<User>builder()
        .status(OK)
        .data(biometricsStatusService.updateUserBiometricsStatus(user, newBiometricsStatus, comment, util.guid(), util.userLegalName(), util.getAuthentication()))
        .build();
  }

  @PostMapping(value = "/saveaddress")
  public Response<User> saveAddress(
      @RequestBody @Valid AddressBasic addressBasic,
      LithiumTokenUtil util
  ) throws Exception {
    log.debug("AddressBasic : "+addressBasic);
    GeoClient geoClient = services.target(GeoClient.class, "service-geo", true);
    if(addressBasic.getCountry() == null || addressBasic.getCountry().trim().isEmpty()){

      String countryIso = cachingDomainClientService.getDomainClient().findByName(util.domainName()).getData().getDefaultCountry();

      if (addressBasic.getCountryCode() == null || addressBasic.getCountryCode().trim().isEmpty()) {
        if (countryIso != null) {
          Country country = geoClient.countryByIso(countryIso).getData();
          addressBasic.setCountry(country.getName());
          addressBasic.setCountryCode(country.getCode());
        }
      }
      else{
        addressBasic.setCountry(geoClient.countryByCode(addressBasic.getCountryCode()).getData().getName());
      }
    }
    if(addressBasic.getCountry() == null || addressBasic.getCountry().trim().isEmpty()){
      log.debug("error trying to update address with empty country field: {}", addressBasic);
      throw new Status500InternalServerErrorException("Failed to infer country from country code and or domain, please provide country field");
    }

    Address address = modelMapper.map(addressBasic, Address.class);
    log.debug("Address : "+address);

    if (address.getId() != null) {
      address.setId(null);
    }


    User user = userService.findOne(addressBasic.getUserId());
    User oldUser = new User();
    oldUser.setPostalAddress(user.getPostalAddress());
    oldUser.setResidentialAddress(user.getResidentialAddress());

    try {
      address.setManualAddress(oldUser.getResidentialAddress().getManualAddress());
    } catch (Exception e) {
      address.setManualAddress(false);
      log.warn("Unable to call set manual residential address on user details update for " + user.guid() + ". " + e.getMessage(), e);
    }
    address = addressRepository.save(address);
    log.debug("Saved Address : "+address);

    if (addressBasic.isPostalAddress()) {
      user.setPostalAddress(address);
    } else if (addressBasic.isResidentialAddress()) {
      user.setResidentialAddress(address);
    } else {
      return Response.<User>builder().status(Response.Status.INVALID_DATA).message("Invalid Parameter Provided {addressType = " + addressBasic.getAddressType() + "}.").build();
    }

    List<ChangeLogFieldChange> clfc = changeLogService.copy(user, oldUser, new String[]{"postalAddress", "residentialAddress"});
    changeLogService.registerChangesForNotesWithFullNameAndDomain(
        "user",
        "edit",
        user.getId(),
        util.getJwtUser().guid(),
        util,
        null,
        null,
        clfc,
        Category.ACCOUNT,
        SubCategory.EDIT_DETAILS,
        0,
        user.domainName()
    );

    user = userService.save(user);
    log.debug("Saved User : "+user);

    // Finished saving user data. Call service-access if domain config contains an access rule for update of user details.
    // We don't care about the outcome at this point.
    try {
      accessRuleService.userDetailsUpdateAccessRule(user);
    } catch (Exception e) {
      log.error("Unable to call access provider on user details update for " + user.guid() + ". " + e.getMessage(), e);
    }

    try {
      pubSubUserService.buildAndSendPubSubAccountChange(user, util.getAuthentication(), PubSubEventType.ACCOUNT_UPDATE);
    } catch (Exception e){
      log.warn("can't sent pub-sub message" + e.getMessage());
    }
    // User save takes care of this
    //userLinkService.applyEcosystemUserDataSynchronisation(user);
    return Response.<User>builder().status(OK).data(user).build();
  }

  private String[] getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

    Set<String> emptyNames = new HashSet();
    for(java.beans.PropertyDescriptor pd : pds) {
      //check if value of this property is null then add it to the collection
      Object srcValue = src.getPropertyValue(pd.getName());
      if (srcValue == null) emptyNames.add(pd.getName());
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }
  private String[] getNotNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

    Set<String> emptyNames = new HashSet();
    for(java.beans.PropertyDescriptor pd : pds) {
      //check if value of this property is null then add it to the collection
      Object srcValue = null;
      if (!pd.getName().equalsIgnoreCase("class")) srcValue = src.getPropertyValue(pd.getName());
      if (srcValue != null) emptyNames.add(pd.getName());
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }

  @PostMapping
  public Response<User> save(@PathVariable("id") User user, @RequestBody @Valid PlayerBasic userUpdate, BindingResult bindingResult, Principal principal) throws Exception {
    return userService.updateUser(user,userUpdate,bindingResult,principal);
  }

  private String[] excludePropertyNames(String[] notNullPropertyNames) {
    List<String> userData = Arrays.stream(notNullPropertyNames)
        .filter(s -> !s.equals("additionalData")
            && !s.equals("underAged"))
        .collect(Collectors.toList());
    String[] filtered = new String[userData.size()];
    return userData.toArray(filtered);
  }

  private boolean isDateOfBirthValid(Integer day, Integer month, Integer year) {
    boolean isValid = false;
    if(day == null || day <= 0 || day > 31){
      return false;
    }

    if(month == null || month <= 0 || month > 12){
      return false;
    }

    if(year == null || year < 1800){
      return false;
    }
    try {
      LocalDate dateOfBirth = LocalDate.of(year, month, day);
      if (dateOfBirth.isBefore(LocalDate.now())) {
        isValid = true;
      }
    } catch (Exception e) {
      log.debug("invalid date of birth received: day {}, month: {} year: {}", day, month, year);
    }

    return isValid;
  }

  @PostMapping(value="/changepassword")
  public Response<User> changePassword(LithiumTokenUtil util, @PathVariable("id") User user, @RequestBody String password) throws Exception {
    user = userService.changePassword(util.guid(), user.guid(), password, util);
    return Response.<User>builder().data(user).build();
  }

  @PostMapping(value = "/changedateofbirth")
  public Response<User> changeDateOfBirth(LithiumTokenUtil util,
      @PathVariable("id") User user,
      @RequestBody UserChanges statusUpdate
  ) throws Exception {

    user = userService.changeDateOfBirth(util.guid(), user.guid(), statusUpdate.getDateOfBirth(), statusUpdate.getComment(), util);
    return Response.<User>builder().data(user).build();
  }

  @PostMapping(value = "/updateplaceofbirth")
  public Response<User> updatePlaceOfBirth(LithiumTokenUtil util, @PathVariable("id") User user, @RequestBody UserChanges userChanges)
      throws Exception {
    return Response.<User>builder().data(userService.changePlaceOfBirth(util, user, userChanges)).build();
  }

  @PostMapping(value="/addgroup")
  public Response<User> addGroup(@PathVariable("id") User user, @RequestBody Long groupId, Principal principal) throws Exception {
    //TODO We really need to confirm that the group id is allowed to be added to this user. Possible hack to elevate privileges.
    final Group group = groupRepository.findOne(groupId);
    if (group == null) throw new Exception("Invalid group id");
    if (group.getDomain().getId() != user.getDomain().getId()) throw new Exception("Group not valid for this user");
    if (user.getGroups() == null) user.setGroups(new ArrayList<Group>());

    List<Group> oldGroups = new ArrayList<Group>();
    for (Group g: user.getGroups()) {
      if (g.getId() == groupId) {
        return Response.<User>builder().data(user).build();
      }
      oldGroups.add(g);
    }

    user.getGroups().add(group);

    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field("groups")
        .fromValue(oldGroups.toString())
        .toValue(user.getGroups().toString())
        .build();
    List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
    clfc.add(c);

    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
        null, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());

    userService.save(user);

    return Response.<User>builder().data(user).build();
  }

  @PostMapping(value="/removegroup")
  public Response<User> removeGroup(@PathVariable("id") User user, @RequestBody Long groupId, Principal principal) throws Exception {
    if (user.getGroups() == null) user.setGroups(new ArrayList<Group>());

    List<Group> oldGroups = new ArrayList<Group>();
    for (Group g: user.getGroups()) {
      oldGroups.add(g);
    }

    user.getGroups().removeIf(g -> (g.getId().compareTo(groupId) == 0));

    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field("groups")
        .fromValue(oldGroups.toString())
        .toValue(user.getGroups().toString())
        .build();
    List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
    clfc.add(c);

    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
        null, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());

    userService.save(user);

    return Response.<User>builder().data(user).build();
  }

  @PostMapping(value = "/toggleAutoWithdrawalAllowed")
  public Response<User> toggleAutoWithdrawalAllowed(@PathVariable("id") User user, Principal principal) throws Exception {
    boolean autoWithdrawalAllowed = (user.getAutoWithdrawalAllowed() != null) ? user.getAutoWithdrawalAllowed() : true;
    user.setAutoWithdrawalAllowed((autoWithdrawalAllowed) ? false : true);
    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field("autoWithdrawalAllowed")
        .fromValue(String.valueOf(autoWithdrawalAllowed))
        .toValue(String.valueOf((autoWithdrawalAllowed) ? false : true))
        .build();
    List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
    clfc.add(c);
    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
        null, null, clfc, Category.FINANCE, SubCategory.AUTO_WITHDRAW, 40, user.domainName());

    return Response.<User>builder().data(userService.save(user)).status(OK).build();
  }

  @PostMapping(value = "/toggleEmailValidation")
  public Response<User> toggleEmailValidation(@PathVariable("id") User user, Principal principal) throws Exception {
    return Response.<User>builder().data(userService.toggleEmailValidation(user, principal)).status(OK).build();
  }

  @PostMapping(value = "/toggle-sow-validation")
  public Response<User> toggleSowValidation(@PathVariable("id") User user, Principal principal) throws Exception {
    return Response.<User>builder().data(userService.toggleSowValidation(user, principal)).status(OK).build();
  }

  @PostMapping(value = "/toggleMobileValidation")
  public Response<User> toggleMobileValidation(@PathVariable("id") User user, Principal principal) throws Exception {
    boolean validated = user.isCellphoneValidated();
    user.setCellphoneValidated(validated? false : true);
    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field("cellphoneValidated")
        .fromValue(String.valueOf(validated))
        .toValue(String.valueOf(validated ? false : true))
        .build();
    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    clfc.add(c);

    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
        null, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());

    return Response.<User>builder().data(userService.save(user)).status(OK).build();
  }

  private Response<User> toggleCheckAgeOrAddress(User user, Principal principal, final String changeTypeName, boolean validated) throws Exception {
    if (user.getAgeVerified() && user.getAddressVerified()) {
      user = verificationStatusService.updateVerificationStatus(
          true,
          UserVerificationStatusUpdate.builder()
              .userId(user.getId())
              .statusId(VerificationStatus.MANUALLY_VERIFIED.getId())
              .comment("age and address verified, change verification status to Manually Verified by system")
              .userGuid(user.guid())
              .ageVerified(user.getAgeVerified())
              .addressVerified(user.getAddressVerified())
              .sendSms(true)
              .authorName(SYSTEM_GUID)
              .build(), 
              principal);
    } else if (user.getAgeVerified() && (!VerificationStatus.isVerified(user.getVerificationStatus()) || !user.getAddressVerified())) {
      user = verificationStatusService.updateVerificationStatus(
          true,
          UserVerificationStatusUpdate.builder()
              .userId(user.getId())
              .statusId(VerificationStatus.AGE_ONLY_VERIFIED.getId())
              .comment("age verified, change verification status to Age Only Verified by system")
              .userGuid(user.guid())
              .ageVerified(user.getAgeVerified())
              .addressVerified(user.getAddressVerified())
              .sendSms(true)
              .authorName(SYSTEM_GUID)
              .build(), 
              principal);
    } else if (!user.getAgeVerified() && !user.getAddressVerified()) {
      user = verificationStatusService.updateVerificationStatus(
          true,
          UserVerificationStatusUpdate.builder()
              .userId(user.getId())
              .statusId(VerificationStatus.UNVERIFIED.getId())
              .comment("age and address unverified, change verification status to Unverified by system")
              .userGuid(user.guid())
              .ageVerified(user.getAgeVerified())
              .addressVerified(user.getAddressVerified())
              .sendSms(true)
              .authorName(SYSTEM_GUID)
              .build(), 
              principal);
    } else {
      user = userService.save(user);
    }
    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field(changeTypeName)
        .fromValue(String.valueOf(validated))
        .toValue(String.valueOf(!validated))
        .build();
    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    clfc.add(c);
    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
        null, null, clfc, Category.ACCOUNT, SubCategory.KYC, 1, user.domainName());

    return Response.<User>builder().data(user).status(OK).build();
  }

  @PostMapping(value = "/toggleAgeVerification")
  public Response<User> toggleAgeVerification(@PathVariable("id") User user, Principal principal) throws Exception {
    boolean validated = user.getAgeVerified();
    user.setAgeVerified(!validated);
    return toggleCheckAgeOrAddress(user, principal, "AgeVerification", validated);
  }

  @PostMapping(value = "/toggleAddressVerification")
  public Response<User> toggleAddressVerification(@PathVariable("id") User user, Principal principal) throws Exception {
    boolean validated = user.getAddressVerified();
    user.setAddressVerified(!validated);
    return toggleCheckAgeOrAddress(user, principal, "AddressVerification",validated);
  }

  @PostMapping(value = "/test-account/{isTestAccount}")
  public Response<User> setTest(@PathVariable("id") User user, @PathVariable("isTestAccount") boolean isTestAccount, Principal principal) throws Exception {
    return Response.<User>builder()
        .data(userService.setTest(user, isTestAccount, principal))
        .status(OK)
        .build();
  }

  @PostMapping("/opt/{method}/{optOut}")
  public Response<User> opt(@PathVariable("id") User user, @PathVariable("method") String method, @PathVariable("optOut") Boolean optOut, LithiumTokenUtil util) throws Exception {
    ChangeLogFieldChange c = null;
    PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder pubSubMarketingPreferences = PubSubMarketingPreferences.builder();
    switch (method) {
      case PromotionsOptService.PROMOTION_METHOD_EMAIL:
        c = ChangeLogFieldChange.builder()
            .field("emailOptOut")
            .fromValue(String.valueOf(user.getEmailOptOut()))
            .toValue(optOut.toString())
            .build();
        user.setEmailOptOut(optOut);
        pubSubMarketingPreferences.emailOptOut(optOut);
        pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
        pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
        pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
        pubSubMarketingPreferences.pushOptOut(user.getPushOptOut());
        pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
        break;

      case PromotionsOptService.PROMOTION_METHOD_POST:
        c = ChangeLogFieldChange.builder()
            .field("postOptOut")
            .fromValue(String.valueOf(user.getPostOptOut()))
            .toValue(optOut.toString())
            .build();
        user.setPostOptOut(optOut);
        pubSubMarketingPreferences.postOptOut(optOut);
        pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
        pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
        pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
        pubSubMarketingPreferences.pushOptOut(user.getPushOptOut());
        pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
        break;

      case PromotionsOptService.PROMOTION_METHOD_SMS:
        c = ChangeLogFieldChange.builder()
            .field("smsOptOut")
            .fromValue(String.valueOf(user.getSmsOptOut()))
            .toValue(optOut.toString())
            .build();
        user.setSmsOptOut(optOut);
        pubSubMarketingPreferences.smsOptOut(optOut);
        pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
        pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
        pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
        pubSubMarketingPreferences.pushOptOut(user.getPushOptOut());
        pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
        break;

      case PromotionsOptService.PROMOTION_METHOD_CALL:
        c = ChangeLogFieldChange.builder()
            .field("callOptOut")
            .fromValue(String.valueOf(user.getCallOptOut()))
            .toValue(optOut.toString())
            .build();
        user.setCallOptOut(optOut);
        pubSubMarketingPreferences.callOptOut(optOut);
        pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
        pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
        pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
        pubSubMarketingPreferences.pushOptOut(user.getPushOptOut());
        pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
        break;

      case PromotionsOptService.PROMOTION_METHOD_PUSH:
        c = ChangeLogFieldChange.builder()
            .field("pushOptOut")
            .fromValue(String.valueOf(user.getPushOptOut()))
            .toValue(optOut.toString())
            .build();
        user.setPushOptOut(optOut);
        toggleSvcPushMsgOptOut(user);
        pubSubMarketingPreferences.pushOptOut(optOut);
        pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
        pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
        pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
        pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
        pubSubMarketingPreferences.leaderBoardOptOut(user.getLeaderboardOptOut());
        break;

      case PromotionsOptService.PROMOTION_METHOD_LEADERBOARD:
        c = ChangeLogFieldChange.builder()
            .field("leaderboardOptOut")
            .fromValue(String.valueOf(user.getLeaderboardOptOut()))
            .toValue(optOut.toString())
            .build();
        user.setLeaderboardOptOut(optOut);
        toggleSvcLeaderboardOptOut(user);
        pubSubMarketingPreferences.leaderBoardOptOut(optOut);
        pubSubMarketingPreferences.emailOptOut(user.getEmailOptOut());
        pubSubMarketingPreferences.postOptOut(user.getPostOptOut());
        pubSubMarketingPreferences.smsOptOut(user.getSmsOptOut());
        pubSubMarketingPreferences.callOptOut(user.getCallOptOut());
        pubSubMarketingPreferences.pushOptOut(user.getLeaderboardOptOut());
        break;
    }

    user = userService.save(user);
    if (c != null) {
      try {
        List<ChangeLogFieldChange> changeLogFieldList = new ArrayList<>();
        changeLogFieldList.add(c);

        changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), util.getJwtUser().guid(), util,
            null, null, changeLogFieldList, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());
      } catch (Exception ex) {
        log.error("Register field changes failed for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
      }
      try {
        pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.BACK_OFFICE, pubSubMarketingPreferences, util, PubSubEventType.MARKETING_PREFERENCES);
      } catch (Exception ex) {
        log.error("OptOutMethod failed for : " + user.getGuid() + " :: " + ex.getMessage() , ex);
      }
    }

    return Response.<User>builder().data(user).status(OK).build();
  }

  private void toggleSvcPushMsgOptOut(User user) {
    try {
      PushMsgClient pushMsgClient = services.target(PushMsgClient.class, true);
      pushMsgClient.toggleOptOut(user.domainName(), user.guid());
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Could not toggle optout on svc-pushmsg", e);
    } catch(Exception e) {
      log.error("The svc-pushmsg has not been configured", e);
    }
  }

  private void toggleSvcLeaderboardOptOut(User user) {
    try {
      LeaderboardClient leaderboardClient = services.target(LeaderboardClient.class, true);
      leaderboardClient.optout(user.guid());
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Could not toggle optout on svc-pushmsg", e);
    } catch(Exception e) {
      log.error("The svc-pushmsg has not been configured", e);
    }
  }

    @GetMapping(value = "/loginevents/table")
    public DataTableResponse<LoginEventFE> loginEvents(
            @PathVariable("id") User user,
            @RequestParam(name = "dateRangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateRangeStart,
            @RequestParam(name = "dateRangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateRangeEnd,
            DataTableRequest request) {
        Specification<LoginEvent> spec = Specification.where(LoginEventSpecification.user(user));
        if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {

      if (request.getSearchValue().contains("%")) {
        request.setSearchValue(request.getSearchValue().replaceAll("%", ""));
      }

            Specification<LoginEvent> s = Specification.where(LoginEventSpecification.anyContains(request.getSearchValue()));
            spec = (spec == null) ? s : spec.and(s);
        }

    spec = addToSpec(dateRangeStart, false, spec, LoginEventSpecification::loginDateRangeStart);
    spec = addToSpec(dateRangeEnd, true, spec, LoginEventSpecification::loginDateRangeEnd);

    Page<LoginEventFE> loginEventFEPage =
        loginEventService.mapLoginEventListToLoginEventFullList(loginEventRepository.findAll(spec, request.getPageRequest()));
    return new DataTableResponse<>(request, loginEventFEPage);
  }

  @GetMapping(value = "/changelogs")
  public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
    return changeLogService.listLimited(ChangeLogRequest.builder()
        .entityRecordId(id)
        .entities(new String[] {
            "user", "user.depositlimit", "user.balancelimit", "user.comment", "affiliate", "user.limit", "user.notifications.intervention",
            "user.exclusion", "user.cooloff", "user.realitycheck", "user.restriction%", "user.verification", "user.bonus", "user.document", "user.send_template", "user.reward%", "user.promotion%",
            "user.collectiondata%", "threshold.notification"
        })
        .page(p)
        .build()
    );
  }

  private Specification<LoginEvent> addToSpec(final Date hopefullyADate, boolean addDay, Specification<LoginEvent> spec, Function<Date, Specification<LoginEvent>> predicateMethod) {
    if (hopefullyADate != null) {
      DateTime someDate = new DateTime(hopefullyADate);
      if (addDay) {
        someDate = someDate.plusDays(1).withTimeAtStartOfDay();
      } else {
        someDate = someDate.withTimeAtStartOfDay();
      }
      Specification<LoginEvent> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  @GetMapping("/additionaldata")
  public Response<Map<String, String>> findUserLabelValues(@PathVariable("domain") String domainName, @PathVariable("id") Long id) throws UserNotFoundException {
    User user = userService.findById(id);
    if (user == null)
      throw new UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_NOT_FOUND", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "User not found or invalid user guid.", LocaleContextHolder.getLocale()));

    if (user.getCurrent() == null) return Response.<Map<String, String>>builder().status(NOT_FOUND).build();

    HashMap<String, String> additionalData = new HashMap<>();
    for (UserRevisionLabelValue userLabelValue : (user.getCurrent() != null ? user.getCurrent().getLabelValueList() : new ArrayList<UserRevisionLabelValue>())) {
      additionalData.put(userLabelValue.getLabelValue().getLabel().getName(), userLabelValue.getLabelValue().getValue());
    }
    return Response.<Map<String, String>>builder().data(additionalData).status(Response.Status.OK).build();
  }

  @PostMapping("/additionaldata")
  public Response<String> updateOrAddUserLabelValues(@PathVariable("domain") String domainName, @PathVariable("id") Long id, @RequestBody Map<String, String> additionalData)
      throws UserNotFoundException {
    User user = userService.findById(id);
    if (user == null)
      throw new UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_NOT_FOUND", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "User not found or invalid user guid.", LocaleContextHolder.getLocale()));

    userService.addOrUpdateDomainSpecificUserLabelValues(user, additionalData);
    return Response.<String>builder().data(Response.Status.OK.name()).status(Response.Status.OK).build();
  }

  @PostMapping(value="/update-failed-login-block/{status}")
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = {ObjectOptimisticLockingFailureException.class}, exclude = {
      Exception.class})
  public Response<User> updateFailedLoginBlock(LithiumTokenUtil util, @PathVariable("id") User user, @PathVariable("status") Boolean blockStatus) throws Exception {
    return Response.<User>builder().data(userService.updateFailedLoginBlock(user, util, blockStatus)).build();
  }

}
