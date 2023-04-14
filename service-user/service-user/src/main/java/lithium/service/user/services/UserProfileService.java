package lithium.service.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.exceptions.Status408DomainMethodDisabledException;
import lithium.exceptions.Status469InvalidInputException;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.client.objects.placeholders.PlaceholderBuilder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.EcosystemRelationshipTypes;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.notifications.client.NotificationInternalSystemService;
import lithium.service.notifications.client.objects.InboxSummary;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.objects.AccountStatusErrorCodeAndMessage;
import lithium.service.user.client.objects.AddressBasic;
import lithium.service.user.client.objects.EcosystemUserProfile;
import lithium.service.user.client.objects.EcosystemUserProfileDomain;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.Restrictions;
import lithium.service.user.client.objects.TermsAndConditionsVersion;
import lithium.service.user.controllers.PlayerController;
import lithium.service.user.controllers.PlayersController;
import lithium.service.user.controllers.UserController;
import lithium.service.user.data.dto.EcosystemMarketingCommsDto;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import lithium.service.user.services.notify.PreValidationNotificationService;
import lithium.tokens.LithiumTokenUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
public class UserProfileService {

  @Autowired UserController userController;
  @Autowired UserLabelValueService userLabelValueService;
  @Autowired PlayersController playersController;
  @Autowired PlayerController playerController;
  @Autowired TokenStore tokenStore;
  @Autowired UserService userService;
  @Autowired PromotionsOptService promotionsOptService;
  @Autowired UserLinkService userLinkService;
  @Autowired LimitInternalSystemService limitInternalSystemService;
  @Autowired EmailValidationService emailValidationService;
  @Autowired PubSubUserService pubSubUserService;
  @Autowired LoginEventService loginEventService;
  @Autowired LimitService limitService;
  @Autowired MessageSource messageSource;
  @Autowired NotificationInternalSystemService notificationInternalSystemService;
  @Autowired CachingDomainClientService cachingDomainClientService;
  @Autowired ObjectMapper mapper;
  @Autowired
  ModelMapper modelMapper;
  @Autowired UserProfileService self;
  @Autowired @Setter ChangeLogService changeLogService;
  @Autowired private PreValidationNotificationService preValidationNotificationService;


  private static final String EXCLUSIVE_ECOSYSTEM_RELATIONSHIP = "exclusive";
  private static final String ROOT_ECOSYSTEM_RELATIONSHIP = "root";

  public static final String AFFILIATE_GUID = "affiliateGuid";

  public Response get(Authentication authentication) throws Exception {

    log.debug("Get Profile.");
    LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authentication).build();
    User user = userService.findOne(util.id());
    if (user == null) return Response.builder().status(Status.NOT_FOUND).build();

    if (user.getStatusReason() != null) {
      AccountStatusErrorCodeAndMessage accountStatusErrorCodeAndMessage = userService.getAccountStatusErrorCodeAndMessage(user);
      limitInternalSystemService.checkAccountStatus(util.guid(), accountStatusErrorCodeAndMessage.getErrorCode());
    }

    user.clearPassword();
    user.setSession(user.getLastLogin());
    user.setLastLogin(loginEventService.loginEventBefore(user.guid(), user.getSession().getId()));
    log.debug(""+user);
    try {
      Integer verificationLevel = limitInternalSystemService.getVerificationStatusLevel(user.getVerificationStatus(), user.domainName());
      InboxSummary inboxSummary = notificationInternalSystemService.getInboxSummaryByUserGuid(user.getGuid());

      Map<String, Object> userMap = mapper.convertValue(user, Map.class);
      userMap.put("verificationLevel", verificationLevel);
      userMap.put("ageVerified", user.getAgeVerified());
      userMap.put("addressVerified", user.getAddressVerified());
      userMap.put("contraAccountSet", limitInternalSystemService.isContraAccountSet(user.guid()));
      userMap.put("inboxSummary", inboxSummary);
      userMap.put("restrictions", limitInternalSystemService.checkAccess(user.guid()));
      userMap.put("lossLimitsVisibility", limitInternalSystemService.getLossLimitVisibility(user.guid()).getLossLimitsVisibility());
      userMap.put("ecosystemUserProfiles", self.buildEcosystemUserProfilesResponse(user));

      enrichAffiliateDataInUserMap(userMap, user);

      return Response.builder().data(userMap).data2(limitService.findPlayerLimit(user.getDomain().getName(), user.guid(), authentication)).build();
    } catch (Status500LimitInternalSystemClientException e) {
      throw new Status500LimitInternalSystemClientException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new lithium.service.translate.client.objects.Domain(user.getDomain().getName())}, "Internal server error.", LocaleContextHolder.getLocale()), e.getStackTrace());
    }
  }

  private void enrichAffiliateDataInUserMap(Map<String, Object> userMap, User user) {
    // This was added on request from PO for a quick win for Cheltenham 2023 - LSPLAT-10788
    // This injects the affiliateGuid label value as a user_category/tag
    try {
      if (userMap.containsKey("userCategories")) {
        List<UserCategory> tags = (List<UserCategory>) userMap.get("userCategories");
        UserCategory uc = buildUserCategory(getAffiliateLabels(user));
        if (uc != null) {
          tags.add(uc);
        }
      } else {
        UserCategory uc = buildUserCategory(getAffiliateLabels(user));
        if (uc != null) {
          userMap.put("userCategories", uc);
        }
      }
    } catch (Exception e) {
      log.error("Could not add affiliate data to player {}", user.guid(), e);
    }
  }

  private UserCategory buildUserCategory(UserRevisionLabelValue userRevisionLabelValue) {
    if (userRevisionLabelValue == null) return null;
    return UserCategory.builder()
        .dwhVisible(true)
        .name(userRevisionLabelValue.getLabelValue().getValue())
        .description(AFFILIATE_GUID)
        .domain(userRevisionLabelValue.getUserRevision().getUser().getDomain())
        .build();
  }

  private UserRevisionLabelValue getAffiliateLabels(User user) {
    return userLabelValueService.findByUserAndLabel(user, AFFILIATE_GUID);
  }

  private String getDomainTcVersion(Domain domain) {
    String domainTcVersion;
    Optional<String> domainTcVersionSetting = domain.findDomainSettingByName(DomainSettings.TERMS_AND_CONDITIONS_VERSION.name());
    if (domainTcVersionSetting.isPresent()) {
      domainTcVersion = domainTcVersionSetting.get();
    } else {
      domainTcVersion = DomainSettings.TERMS_AND_CONDITIONS_VERSION.defaultValue();
    }
    return domainTcVersion;
  }

  @TimeThisMethod
  public List<EcosystemUserProfile> buildEcosystemUserProfilesResponse(User user)
      throws Status550ServiceDomainClientException, Status469InvalidInputException, Status500LimitInternalSystemClientException {
    List<EcosystemUserProfile> ecosystemUserProfiles = new ArrayList<>();

    if (cachingDomainClientService.isDomainInAnyEcosystem(user.domainName())) {
      boolean isRootDomain = cachingDomainClientService.isDomainNameOfEcosystemRootType(user.domainName());
      User linkedUser = null;
      Domain linkedUserDomain = null;
      Access linkedEcosystemAccessRestrictions = null;
      String linkedUserDomainTcVersion = null;
      if (isRootDomain) {
        List<String> domains = cachingDomainClientService.listMutuallyExclusiveDomainsWithinAnEcosystem(
            cachingDomainClientService.findEcosystemNameByEcosystemRootDomainName(user.domainName()).get().getEcosystem().getName());
        if (!ObjectUtils.isEmpty(domains)) {
          //mutuallyExclusiveDomainUser
          linkedUser = userService.findByDomainNameInAndEmail(domains, user.getEmail()).orElse(null);
        }
      } else {
        //rootDomainUser
        linkedUser = userService.deriveRootEcosystemUser(user.getId());
      }

      if (linkedUser != null && !Objects.equals(user.domainName(), linkedUser.domainName())) {
        linkedUserDomain = cachingDomainClientService.retrieveDomainFromDomainService(linkedUser.domainName());
        linkedEcosystemAccessRestrictions = limitInternalSystemService.checkAccess(linkedUser.getGuid());
        linkedUserDomainTcVersion = getDomainTcVersion(linkedUserDomain);
        log.debug("Retrieving Ecosystem User Profile for user: " + user.getGuid());
        EcosystemUserProfileDomain ecoSystemDomain = EcosystemUserProfileDomain.builder()
            .id(linkedUserDomain.getId())
            //.version(linkedUserDomain.getVers)
            .name(linkedUserDomain.getName())
            .defaultLocale(linkedUserDomain.getDefaultLocale())
            .defaultCountry(linkedUserDomain.getDefaultCountry())
            .defaultCurrency(linkedUserDomain.getCurrency())
            .build();

        Restrictions ecoSystemRestrictions = Restrictions.builder()
            .casinoAllowed(linkedEcosystemAccessRestrictions.isCasinoAllowed())
            .casinoSystemPlaced(linkedEcosystemAccessRestrictions.isCasinoSystemPlaced())
            .casinoErrorMessage(linkedEcosystemAccessRestrictions.getCasinoErrorMessage())
            .loginAllowed(linkedEcosystemAccessRestrictions.isLoginAllowed())
            .loginErrorMessage(linkedEcosystemAccessRestrictions.getLoginErrorMessage())
            .depositAllowed(linkedEcosystemAccessRestrictions.isDepositAllowed())
            .depositErrorMessage(linkedEcosystemAccessRestrictions.getDepositErrorMessage())
            .withdrawAllowed(linkedEcosystemAccessRestrictions.isWithdrawAllowed())
            .withdrawErrorMessage(linkedEcosystemAccessRestrictions.getWithdrawErrorMessage())
            .betPlacementAllowed(linkedEcosystemAccessRestrictions.isBetPlacementAllowed())
            .betPlacementErrorMessage(linkedEcosystemAccessRestrictions.getBetPlacementErrorMessage())
            .compsAllowed(linkedEcosystemAccessRestrictions.isCompsAllowed())
            .compsSystemPlaced(linkedEcosystemAccessRestrictions.isCompsSystemPlaced())
            .compsErrorMessage(linkedEcosystemAccessRestrictions.getCompsErrorMessage())
            .f2pAllowed(linkedEcosystemAccessRestrictions.isF2pAllowed())
            .f2pErrorMessage(linkedEcosystemAccessRestrictions.getF2pErrorMessage())
            .build();

        TermsAndConditionsVersion ecoSystemTermsAndConditions = TermsAndConditionsVersion.builder()
            .acceptedUserVersion(linkedUser.getTermsAndConditionsVersion())//the one the user accepted upon initial registration
            .currentDomainVersion(linkedUserDomainTcVersion)//domainSettings
            .build();

        lithium.service.user.client.objects.Status ecoSystemStatus = lithium.service.user.client.objects.Status.builder()
            .id(linkedUser.getStatus().getId())
            //.version(linkedUser.getVersion())
            .name(linkedUser.getStatus().getName())
            .description(linkedUser.getStatus().getDescription())
            .userEnabled(linkedUser.getStatus().getUserEnabled())
            .deleted(linkedUser.getStatus().getDeleted())
            .build();

        lithium.service.user.client.objects.StatusReason ecoSystemStatusReason = null;
        if (linkedUser.getStatusReason() != null) {
          ecoSystemStatusReason = lithium.service.user.client.objects.StatusReason.builder()
              .id(linkedUser.getStatusReason().getId())
              .version(linkedUser.getStatusReason().getVersion())
              .name(linkedUser.getStatusReason().getName())
              .description(linkedUser.getStatusReason().getDescription())
              .build();
        }

        EcosystemUserProfile linkedLSBDetails = EcosystemUserProfile.builder()
            .domain(ecoSystemDomain)
            .ecosystemRelationshipType(isRootDomain ? EXCLUSIVE_ECOSYSTEM_RELATIONSHIP : ROOT_ECOSYSTEM_RELATIONSHIP)
            .emailOptOut(linkedUser.getEmailOptOut())
            .postOptOut(linkedUser.getPostOptOut())
            .smsOptOut(linkedUser.getSmsOptOut())
            .callOptOut(linkedUser.getCallOptOut())
            .pushOptOut(linkedUser.getPushOptOut())
            .leaderboardOptOut(linkedUser.getLeaderboardOptOut())
            .status(ecoSystemStatus)
            .statusReason(ecoSystemStatusReason)
            .verificationLevel(limitInternalSystemService.getVerificationStatusLevel(linkedUser.getVerificationStatus(), linkedUser.getDomain().getName()))
            .ageVerified(linkedUser.getAgeVerified() != null && linkedUser.getAgeVerified())
            .addressVerified(linkedUser.getAddressVerified() != null && linkedUser.getAddressVerified())
            .emailValidated(linkedUser.isEmailValidated())
            .cellphoneValidated(linkedUser.isCellphoneValidated())
            .contraAccountSet(limitInternalSystemService.isContraAccountSet(linkedUser.guid()))
            .commsOptInComplete(linkedUser.getCommsOptInComplete() != null && linkedUser.getCommsOptInComplete())
            .hasSelfExcluded(linkedUser.getHasSelfExcluded() != null && linkedUser.getHasSelfExcluded())
            .autoWithdrawalAllowed(linkedUser.getAutoWithdrawalAllowed() != null && linkedUser.getAutoWithdrawalAllowed())
            .termsAndConditionsVersion(ecoSystemTermsAndConditions)
            .restrictions(ecoSystemRestrictions)
            .build();

        ecosystemUserProfiles.add(linkedLSBDetails);
      }
    }
    return ecosystemUserProfiles;
  }

  public User updateAdminProfile(PlayerBasic userUpdate, LithiumTokenUtil util) throws Exception {

    Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(userUpdate.getDomainName());

    if(domain.getPlayers()){
      throw new Status408DomainMethodDisabledException("An invalid domain was provided");
    }

    User user = userService.findOne(userUpdate.getId());

    User oldUser = new User();
    BeanUtils.copyProperties(user, oldUser);
    userUpdate.setId(user.getId());
    BeanUtils.copyProperties(userUpdate, user);

    if (ObjectUtils.isEmpty(user)) {
      throw new Status411UserNotFoundException(messageSource.getMessage("SERVICE_USER.SYSTEM.USERGUID404", null,  LocaleContextHolder.getLocale()));
    }

    boolean emailAddressChanged = userService.isEmailAddressChanged(user, oldUser, userUpdate);

    userUpdate.setEmailValidated(!emailAddressChanged && user.isEmailValidated());
    user.setEmailValidated(!emailAddressChanged && user.isEmailValidated());

    String[] fieldsToCopy = Stream.of(userService.excludePropertyNames(userService.getNotNullPropertyNames(userUpdate)))
        .filter(f -> !f.equalsIgnoreCase("channelsOptOut"))
        .toArray(String[]::new);

    List<ChangeLogFieldChange> clfc = changeLogService.compare(
        user, oldUser, fieldsToCopy
    );

    if(clfc.size() > 0) {
      user.setUpdatedDate(new Date());
      user = userService.save(user, true);

      preValidationNotificationService.sendEmailOrCellphoneChangeNotification(oldUser.getEmail(), null, user);

      //New change log register to handle the addition of a category and sub-category
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), util.guid(),
          util, userUpdate.getComments(), null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());

      try {
        pubSubUserService.publishAccountChange(user, util.getAuthentication());
      } catch (Exception e){
        log.warn("can't sent pub-sub message" + e.getMessage());
      }
    }

    return user;
  }

  public Response savev2(PlayerBasic userUpdate,PlayerBasic parentUpdate, BindingResult bindingResult, Principal principal) throws Exception {
    LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
    User user = userService.findOne(util.id());
    if (user == null) return Response.builder().status(Status.UNAUTHORIZED).build();

    if(!StringUtils.isEmpty(user.getEmail()) && StringUtils.isEmpty(userUpdate.getEmail())){
      userUpdate.setEmail(user.getEmail());
    }

    Response<User> updatedUserResponse = userService.updateUser(user, userUpdate, bindingResult, principal);

    // When inside an ecosystem, also update root marketing preferences, unless the user is from the root domain
    if (cachingDomainClientService.isDomainInAnyEcosystem(util.domainName()) && !cachingDomainClientService.isDomainNameOfEcosystemRootType(util.domainName())) {
      User parentUser = userService.deriveRootEcosystemUser(updatedUserResponse.getData().getId());

      if (!ObjectUtils.isEmpty(parentUser)) {
        userService.updateUser(parentUser, parentUpdate, bindingResult, principal);
      }
    }
    Map<String, Object> userMap = mapper.convertValue(user, Map.class);
    userMap.put("ecosystemUserProfiles", self.buildEcosystemUserProfilesResponse(user));

    enrichAffiliateDataInUserMap(userMap, user);

    return Response.builder().status(Status.OK).data(userMap).build();
  }

  public Response<User> saveAddress(
      AddressBasic addressBasic,
      LithiumTokenUtil util
  ) throws Exception {
    log.debug("addressBasic.getUserId(): "+addressBasic.getUserId()+" util.id(): "+util.id()+" ["+addressBasic+"]");
    if (!addressBasic.getUserId().equals(util.id())) {
      log.warn("UNAUTHORIZED");
      return Response.<User>builder().status(Status.UNAUTHORIZED).build();
    }
    Response<User> response = userController.saveAddress(addressBasic, util);
    log.debug("Response: "+response);
    return response;
  }

  public Response<User> changePassword(LithiumTokenUtil util, String password) throws Exception {
    User user = userService.findOne(util.id());
    if (user == null) return Response.<User>builder().status(Status.UNAUTHORIZED).build();
    return userController.changePassword(util, user, password);
  }

  public String deletePlayerAccount(User user, LithiumTokenUtil tokenUtil) throws Exception {
    //due to erasure of player info will be using the object here to be able to send the email
    Set<Placeholder> placeholders = generatePlaceHolder(user);
    String email = user.getEmail();
    loginEventService.logout(user.guid(), null);
    userLinkService.deleteRootUserLink(user);
    user = userService.clearPlayerPersonalInfo(user, tokenUtil);
    //all changelog messages should be in english
    promotionsOptService.groupOptOut(user, true, tokenUtil, true, "The Player has successfully opted out of all communications due to account deletion");
    emailValidationService.sendAccountDeletionEmail(placeholders, email, user.getGuid(), user.domainName());
    return messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.PROFILE_DELETION_SUCCESSFUL", new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, LocaleContextHolder.getLocale());
  }

  private Set<Placeholder> generatePlaceHolder(User user) {
    Set<Placeholder> placeholders = new HashSet<>(
        Arrays.asList(PlaceholderBuilder.USER_GUID.from(user.guid()), PlaceholderBuilder.USER_EMAIL_ADDRESS.from(user.getEmail()),
            PlaceholderBuilder.DOMAIN_NAME.from(user.domainName()))
    );

    if (!ObjectUtils.isEmpty(user.getFirstName())){
      placeholders.add(PlaceholderBuilder.USER_FIRST_NAME.from(user.getFirstName()));
    }

    if (!ObjectUtils.isEmpty(user.getLastNamePrefix())){
      placeholders.add(PlaceholderBuilder.USER_LAST_NAME_PREFIX.from(user.getLastNamePrefix()));
    }

    if (!ObjectUtils.isEmpty(user.getLastName())){
      placeholders.add(PlaceholderBuilder.USER_LAST_NAME.from(user.getLastName()));
    }

    if (!ObjectUtils.isEmpty(user.getCellphoneNumber())){
      placeholders.add(PlaceholderBuilder.USER_CELLPHONE_NUMBER.from(user.getCellphoneNumber()));
    }

    return placeholders;
  }

  public int migrateDeletedAccounts(LithiumTokenUtil util) throws Exception {
    List<User> deletedUsers = userService.findAllByDeletedAndPendingDeleteStatus();
    int count = 0;
    if (deletedUsers.isEmpty()) {
      throw new Status404UserNotFoundException("No deleted users with pending status update");
    }

    for (User user : deletedUsers) {
      if (cachingDomainClientService.isDomainNameOfEcosystemRootType(user.domainName())) {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.domainName());
        Optional<String> migrateDeletedPlayersSetting = domain.findDomainSettingByName("migrate_deleted_players");

        if (migrateDeletedPlayersSetting.isPresent()) {
          deletePlayerAccount(user, util);
          count++;
        } else {
          log.debug("Domain setting not available for user: {}", user.guid());
        }
      }
    }

    log.debug("Migrated {} out of {} for deleted users", count, deletedUsers.size());
    return count;
  }

  public lithium.service.user.client.objects.User getLinkedEcosystemUserGuid(String userGuid, EcosystemRelationshipTypes ecosystemRelationshipType) {

    User user = userService.findFromGuid(userGuid);

    if (!cachingDomainClientService.isDomainInAnyEcosystem(user.domainName())) {
      return null;
    }

    ArrayList<String> ecosystemDomainNames = cachingDomainClientService.listEcosystemDomainRelationshipsByDomainName(user.domainName()).stream()
        .filter(dr -> dr.getRelationship().getCode().contentEquals(ecosystemRelationshipType.key()))
        .map(dr -> dr.getDomain().getName())
        .collect(Collectors.toCollection(ArrayList::new));

    Optional<User> ecosystemUser = userService.findByDomainNameInAndEmail(ecosystemDomainNames.stream().toList(), user.getEmail());
    if (!ecosystemUser.isPresent()) {
      return null;
    }

    return mapper.convertValue(ecosystemUser, lithium.service.user.client.objects.User.class);
  }
}
