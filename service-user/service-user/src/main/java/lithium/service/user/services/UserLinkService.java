package lithium.service.user.services;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.exceptions.Status469InvalidInputException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.user.client.enums.UserLinkTypes;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserLink;
import lithium.service.user.data.entities.UserLinkType;
import lithium.service.user.data.repositories.UserLinkRepository;
import lithium.service.user.data.repositories.UserLinkTypeRepository;
import lithium.service.user.exceptions.Status100InvalidInputDataException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class UserLinkService {

  private final AutoRestrictionTriggerStream autoRestrictionTriggerStream;
  private final UserService userService;
  private final UserLinkRepository userLinkRepository;
  private final UserLinkTypeRepository userLinkTypeRepository;
  private final CachingDomainClientService cachingDomainClientService;
  private final ChangeLogService changeLogService;
  private final PubSubUserService pubSubUserService;
  private final MessageSource messageSource;

  @Autowired
  public UserLinkService(
      AutoRestrictionTriggerStream autoRestrictionTriggerStream,
      UserService userService,
      UserLinkRepository userLinkRepository,
      UserLinkTypeRepository userLinkTypeRepository,
      CachingDomainClientService cachingDomainClientService,
      ChangeLogService changeLogService,
      //LSPLAT-5637 PLAT-6344 FIXME need a better way around circular dependencies, for now @Lazy will do just fine
      @Lazy PubSubUserService pubSubUserService,
      MessageSource messageSource) {
    this.autoRestrictionTriggerStream = autoRestrictionTriggerStream;
    this.userService = userService;
    this.userLinkRepository = userLinkRepository;
    this.userLinkTypeRepository = userLinkTypeRepository;
    this.cachingDomainClientService = cachingDomainClientService;
    this.changeLogService = changeLogService;
    this.pubSubUserService = pubSubUserService;
    this.messageSource = messageSource;
  }

  public DataTableResponse<UserLink> findUserLinks(
      String ecosystemName, String[] domainNames, DataTableRequest dataTableRequest, LithiumTokenUtil tokenUtil)
      throws
      Status469InvalidInputException,
      Status550ServiceDomainClientException {
    if (ecosystemName != null) {
      domainNames = cachingDomainClientService.listDomainNamesInEcosystemByEcosystemName(ecosystemName).stream()
          .toArray(String[]::new);
    }
    List<Domain> domainList = userService.filterRequestedDomainsForToken(domainNames, tokenUtil);

    if (domainList.isEmpty()) {
      return new DataTableResponse<>(dataTableRequest, Collections.emptyList());
    }

    Page<UserLink> userLinkPage = find(domainList, dataTableRequest.getSearchValue(), dataTableRequest.getPageRequest());
    for (UserLink userLink : userLinkPage) {
      userLink.getPrimaryUser().setFirstName(escapeHtml(userLink.getPrimaryUser().getFirstName()));
      userLink.getSecondaryUser().setFirstName(escapeHtml(userLink.getSecondaryUser().getFirstName()));
      userLink.getPrimaryUser().setLastName(escapeHtml(userLink.getPrimaryUser().getLastName()));
      userLink.getSecondaryUser().setLastName(escapeHtml(userLink.getSecondaryUser().getLastName()));
    }

    return new DataTableResponse<>(dataTableRequest, userLinkPage);
  }

  private Page<UserLink> find(List<Domain> domainList, String searchValue, Pageable pageable) {
    //FIXME: do specification things for user link
    return userLinkRepository.findByPrimaryUserDomainInAndDeletedFalse(domainList, pageable);
//    return new PageImpl<UserLink>(new ArrayList<UserLink>(), pageable, 0);
  }

  /**
   * Primary user link lookup
   *
   * @param user
   * @return
   * @throws Status404UserNotFoundException
   */
  public ArrayList<UserLink> findUserLinksByUser(User user)
      throws Status100InvalidInputDataException {

    if (user == null) {
      throw new Status100InvalidInputDataException("Unable to find user with guid: " + user.guid());
    }

    return userLinkRepository.findByPrimaryUserAndDeletedFalse(user);
  }

  /**
   * Primary user link lookup per user link type
   *
   * @param userGuid
   * @return
   * @throws Status404UserNotFoundException
   */
  public ArrayList<UserLink> findUserLinksByUserAndUserLinkType(String userGuid, UserLinkType userLinkType)
      throws Status100InvalidInputDataException {

    return userLinkRepository.findByPrimaryUserAndDeletedFalseAndUserLinkType(userService.findFromGuid(userGuid), userLinkType);
  }

  //TODO: Add cache if required (evict on save)
  public ArrayList<UserLinkType> getUserLinkTypeList() {
    return userLinkTypeRepository.findByEnabledTrueAndDeletedFalse();
  }

  public UserLink addUserLink(String primaryUserGuid, String secondaryUserGuid, String userLinkTypeCode, String linkNote, LithiumTokenUtil util)
      throws Status100InvalidInputDataException {

    User primaryUser = userService.findFromGuid(primaryUserGuid);

    User secondaryUser = userService.findFromGuid(secondaryUserGuid);
    UserLinkType linkType = userLinkTypeRepository.findByCode(userLinkTypeCode);
    if (linkType == null) {
      throw new Status100InvalidInputDataException("User link type code invalid: " + userLinkTypeCode);
    }

    UserLink userLink = userLinkRepository.save(UserLink.builder()
        .primaryUser(primaryUser)
        .secondaryUser(secondaryUser)
        .userLinkType(linkType)
        .linkNote(linkNote)
        .build());

    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    ChangeLogFieldChange clfchange = ChangeLogFieldChange.builder()
        .field("linkNote")
        .fromValue("")
        .toValue(linkNote)
        .build();
    clfc.add(clfchange);
    try {
      changeLogService.registerChangesForNotesWithFullNameAndDomain(
          "user",
          "edit",
          primaryUser.getId(),
          util.guid(),
          util,
          "Created a new user link (" + linkType.getCode().toLowerCase() + ") from primary (" + primaryUserGuid + ") to secondary (" + secondaryUserGuid + ")",
          null,
          clfc,
          Category.ACCOUNT,
          SubCategory.USER_LINK,
          0,
          primaryUser.domainName());
    } catch (Exception e) {
      log.debug("Unable to create change log for user link: " + userLink);
    }

    pubSubUserService.buildAndSendAccountLinkCreate(userLink, PubSubEventType.LINK_CREATE);
    return userLink;
  }

  public UserLink updateUserLinkNote(Long userLinkId, String linkNote, Boolean deleted, Principal principal)
      throws Status100InvalidInputDataException {
    UserLink userLink = userLinkRepository.findOne(userLinkId);
    String defaultLocale = cachingDomainClientService.domainLocale(userLink.getPrimaryUser().getDomain().getName());
    String comment = messageSource.getMessage("Edited player link note", null, Locale.forLanguageTag(defaultLocale));
    Long playerId = userLink.getPrimaryUser().getId();
    String changeLogType = "edit";
    String oldLinkNote = userLink.getLinkNote();
    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    if (userLink == null) {
      throw new Status100InvalidInputDataException("Invalid user link id: " + userLinkId);
    }
    if (deleted != null && deleted.booleanValue() != userLink.getDeleted().booleanValue()) {
      userLink.setDeleted(deleted);
      comment = messageSource.getMessage("Deleted player link", null, Locale.forLanguageTag(defaultLocale));
      changeLogType = "delete";
      linkNote = "";
    }
    ChangeLogFieldChange clfchange = ChangeLogFieldChange.builder()
        .field("linkNote")
        .fromValue(oldLinkNote)
        .toValue(linkNote)
        .build();
    clfc.add(clfchange);
    userLink.setLinkNote(linkNote);
    try {
      changeLogService.registerChangesWithDomain(
          "user",
          changeLogType,
          playerId,
          principal.getName(),
          comment,
          null,
          clfc,
          Category.ACCOUNT,
          SubCategory.USER_LINK,
          0,
          userLink.getPrimaryUser().domainName()
      );
    } catch (Exception e) {
      log.debug("Unable to complete changelog for uselink edit: " + userLink);
    }

    UserLink result = userLinkRepository.save(userLink);

    pubSubUserService.buildAndSendAccountLinkNoteUpdate(result);

    return result;
    //TODO: add changelog entry
  }

  public UserLink updateUserLink(Long userLinkId, String linkTypeCode, String linkNote, Principal principal, boolean sync)
      throws Status100InvalidInputDataException {
    String comment = "Player link updated";

    UserLink userLink = userLinkRepository.findOne(userLinkId);
    if (userLink == null) {
      throw new Status100InvalidInputDataException("Invalid user link id: " + userLinkId);
    }

    UserLinkType linkType = userLinkTypeRepository.findByCode(linkTypeCode);
    if (linkType == null) {
      throw new Status100InvalidInputDataException("User link type Id invalid: " + linkTypeCode);
    }

    UserLinkType fromLinkType = userLink.getUserLinkType();
    List<ChangeLogFieldChange> clfc = new ArrayList<>();

    if(!userLink.getLinkNote().equalsIgnoreCase(linkNote)) {
      ChangeLogFieldChange clfchange = ChangeLogFieldChange.builder()
          .field("linkNote")
          .fromValue(userLink.getLinkNote())
          .toValue(linkNote)
          .build();
      clfc.add(clfchange);
    }

    if(!fromLinkType.getCode().equalsIgnoreCase(linkTypeCode)) {
      comment = String.format("Updated user link between primary (%s) and secondary (%s) from %s to %s",
          userLink.getPrimaryUser().getGuid(), userLink.getSecondaryUser().getGuid(), fromLinkType.getCode(), linkTypeCode);

      ChangeLogFieldChange clfchange = ChangeLogFieldChange.builder()
          .field("user_link_type_id")
          .fromValue(fromLinkType.getId().toString())
          .toValue(linkType.getId().toString())
          .build();
      clfc.add(clfchange);
    }

    userLink.setLinkNote(linkNote);
    userLink.setUserLinkType(linkType);
    UserLink result = userLinkRepository.save(userLink);

    try {


      if(clfc.size() > 0) {

        changeLogService.registerChangesWithDomain(
            "user",
            "edit",
            userLink.getPrimaryUser().getId(),
            principal.getName(),
            comment,
            null,
            clfc,
            Category.ACCOUNT,
            SubCategory.USER_LINK,
            0,
            userLink.getPrimaryUser().domainName()
        );
      }
    } catch (Exception e) {
      log.debug("Unable to complete changelog for uselink edit: " + userLink);
    }

    pubSubUserService.buildAndSendAccountLinkNoteUpdate(result);

    if(sync) {
      Optional<UserLink> link = userLinkRepository.findOneByPrimaryUserAndSecondaryUserAndDeletedFalseAndUserLinkTypeCode(result.getSecondaryUser(), result.getPrimaryUser(), fromLinkType.getCode());
      if(link.isPresent()) {
        return updateUserLink(link.get().getId(), linkTypeCode, linkNote, principal, false);
      }
    }

    return result;
  }



  //FIXME: Remember that if email is changed, the sync is lost, if we need to keep emails in sync we need another solution
  //FIXME: Remember if we want to run on phone number syncing we will also need another solution
  public boolean applyEcosystemUserDataSynchronisation(User user)
      throws
      Status550ServiceDomainClientException {
    boolean syncHappened = false;
    // This is here to avoid cache misses in the domain list retrievals
    // Thinking about this now, it is useless in this instance since the lists would be empty lists, not null, so it would be cached. :`(
    if (!cachingDomainClientService.isDomainInAnyEcosystem(user.getDomain().getName())) {
      log.debug("The domain is not in an ecosystem, no synchronisation needed: " + user);
      return syncHappened;
    }

    try {
      ArrayList<String> ecosystemDomains = cachingDomainClientService.listDomainNamesInEcosystemByDomainName(user.getDomain().getName());
      if (ecosystemDomains.isEmpty() || ecosystemDomains.size() == 1) {
        log.debug("Only one domain in the ecosystem, this no synchronisation needed on user: " + user);
        return syncHappened;
      }

      //TODO: When africa needs to be done we need to cater for cellphone lookups as well
      if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
        log.debug("User email is not populated, not going to do user synchronisation: " + user);
        return syncHappened;
      }
      List<User> existingUsers = userService.findByDomainNameAndEmailEcosystemAware(user.getDomain().getName(), user.getEmail());
      if (user.getId() != null) {
        ArrayList<UserLink> userLinksByUser = findUserLinksByUserAndUserLinkType(user.guid(), findUserLinkType(UserLinkTypes.CROSS_DOMAIN_LINK));
        // LSPLAT-2090 - Recovery method whenever the email has changed and the linkage broke on the unique ecosystem identifier; allowing for the email change to sync accross to the rest of the domains who now have different email addresses
        // By the time we get to this point, we know that the email uniqueness checks already passed prior to updating the user that triggered this ecosystem sync
        for (UserLink userLink : userLinksByUser) {
          if (!existingUsers.stream().anyMatch(u -> u.getId().equals(userLink.getPrimaryUser().getId()))) {
            existingUsers.add(userLink.getPrimaryUser());
          }
          if (!existingUsers.stream().anyMatch(u -> u.getId().equals(userLink.getSecondaryUser().getId()))) {
            existingUsers.add(userLink.getSecondaryUser());
          }
        }
      }
      for (User u : existingUsers) {
        if (u.getId() == user.getId()) {
          continue;
        }


        log.debug("User modification (before): " + u);
        synchroniseUserData(user, u);
        log.debug("User modification (after): " + u);
        User updatedUser = userService.save(u, true); // No ecosystem sync or we will loop forever

        // This needs to be here to enforce consistency across all domains the user is on
        log.debug("User modification reverse (before): " + user);
        synchroniseUserData(updatedUser, user);
        log.debug("User modification reverse (after): " + user);
        user = userService.save(user, true);
        syncHappened = true;
      }

    } catch (Status469InvalidInputException e) {
      // This is a domain that does not form part of an ecosystem, nothing to do
    } catch (Status100InvalidInputDataException e) {
      log.warn("Problem with provided user data objects, fix your code: " + user.guid(), e);
    }
    return syncHappened;
  }

  public User synchroniseUserData(final User sourceUser, User destinationUser)
      throws
      Status100InvalidInputDataException {
    ArrayList<String> fieldsToExclude = new ArrayList<>();
    fieldsToExclude.add("id");
    fieldsToExclude.add("guid");
    fieldsToExclude.add("domain");
    fieldsToExclude.add("status");
    fieldsToExclude.add("groups");
    fieldsToExclude.add("userCategories");
    fieldsToExclude.add("createdDate");
    fieldsToExclude.add("updatedDate");
    fieldsToExclude.add("bonusCode");
    fieldsToExclude.add("welcomeEmailSent");
    fieldsToExclude.add("lastLogin");
    fieldsToExclude.add("session");
    fieldsToExclude.add("userEvents");
    fieldsToExclude.add("emailOptOut");
    fieldsToExclude.add("smsOptOut");
    fieldsToExclude.add("callOptOut");
    fieldsToExclude.add("postOptOut");
    fieldsToExclude.add("pushOptOut");
    fieldsToExclude.add("leaderboardOptOut");
    fieldsToExclude.add("promotionsOptOut");
    fieldsToExclude.add("welcomeSmsSent");
    fieldsToExclude.add("userApiToken");
    fieldsToExclude.add("excessiveFailedLoginBlock");
    fieldsToExclude.add("failedResetCount");
    fieldsToExclude.add("commsOptInComplete");
    fieldsToExclude.add("termsAndConditionsVersion");
    fieldsToExclude.add("version");
    fieldsToExclude.add("deleted");
    fieldsToExclude.add("statusReason");
    fieldsToExclude.add("protectionOfCustomerFundsVersion");

    //TODO: Find a more elegant way to not reduce verification status in future for syncing
    // Verification Status syncing has been excluded as part of a hotfix to prod - For more details, please see LSPLAT-3127(PLAT-3855) as well as LSPLAT-5380(PLAT-6087)
    fieldsToExclude.add("verificationStatus");
    fieldsToExclude.add("ageVerified");
    fieldsToExclude.add("addressVerified");
    fieldsToExclude.add("hasSelfExcluded");
    fieldsToExclude.add("emailValidated");
    fieldsToExclude.add("cellphoneValidated");

    // Affiliate and external authorization data
    if (sourceUser.getCurrent() == null) {
      fieldsToExclude.add("current");
    }

    if (sourceUser.getResidentialAddress() == null) {
      fieldsToExclude.add("residentialAddress");
    }
    if (sourceUser.getPostalAddress() == null) {
      fieldsToExclude.add("postalAddress");
    }
    if (sourceUser.getCurrentCollectionDataRevId() == null) {
      fieldsToExclude.add("currentCollectionDataRevId");
    }
    User oldUser = User.builder().build();
    BeanUtils.copyProperties(destinationUser, oldUser);
    return copyNonNullProperties(sourceUser, destinationUser, oldUser, fieldsToExclude);
  }

  //TODO: Make this more generic and move it to utils
  //TODO: Build in an optional exclusion of id field copies traversing the objects (do a recursion)
  private <T extends User> T copyNonNullProperties(final T in, T target, final T targetUnchanged, ArrayList<String> fieldsToExclude)
      throws
      Status100InvalidInputDataException {
    if (in == null || target == null || target.getClass() != in.getClass()) {
      throw new Status100InvalidInputDataException("Invalid user objects provided");
    }

    final BeanWrapper source = new BeanWrapperImpl(in);
    final BeanWrapper destination = new BeanWrapperImpl(target);

    ArrayList<String> fieldChangeList = new ArrayList<>();
    for (final Field property : target.getClass().getDeclaredFields()) {
      try {
        if (fieldsToExclude.contains(property.getName())) {
          log.debug("Skipping field in object copy: " + property.getName());
          continue;
        }
        Object providedObject = source.getPropertyValue(property.getName());
//      if (providedObject != null && !(providedObject instanceof Collection<?>)) {
        fieldChangeList.add(property.getName());
        destination.setPropertyValue(
            property.getName(),
            providedObject);
        log.debug("Adding property to destination object: " + property.getName());
//      }
      } catch (NotReadablePropertyException nrpe) {
        log.debug("Non readable property when synchronising user information: " + property.getName());
      }
    }
    try {
      List<ChangeLogFieldChange> changes = changeLogService.compare(target, targetUnchanged, fieldChangeList.stream().toArray(String[]::new));
      if (target.getId() != null && changes != null && !changes.isEmpty()) {
        changeLogService.registerChangesSystem("user", "synchronization", target.getId(),
            "This player was synchronized with player " + in.guid() + " due to ecosystem rules." , null, changes,
            Category.ACCOUNT, SubCategory.ECOSYSTEM_SYNCHRONIZATION, 0, target.getDomain().getName());
      }
      } catch (Exception e) {
      log.warn("Unable to log changes for user data synchronization: source: " + source + " target: " + target,  e);
    }
    return target;
  }

  public UserLinkType findUserLinkType(UserLinkTypes userLinkTypes) {
    return userLinkTypeRepository.findByCode(userLinkTypes.code());
  }

  public UserLink findAndUpdateOrCreateUserLink(User primaryUser, User secondaryUser, UserLinkTypes userLinkTypes, String linkNote) {
    UserLinkType linkType = findUserLinkType(userLinkTypes);

    Optional<UserLink> link = userLinkRepository.findByPrimaryUserAndSecondaryUserAndUserLinkTypeCode(primaryUser, secondaryUser, userLinkTypes.code());
    UserLink userLink;
    if (link.isPresent()) {
      userLink = link.get();
      if(userLink.getDeleted().equals(true)) {
        userLink.setDeleted(false);
        userLink.setLinkNote(linkNote);
        userLink = userLinkRepository.save(userLink);
      }
    } else {
      userLink = userLinkRepository.save(UserLink.builder()
          .primaryUser(primaryUser)
          .secondaryUser(secondaryUser)
          .userLinkType(linkType)
          .linkNote(linkNote)
          .build());
    }

    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    ChangeLogFieldChange clfchange = ChangeLogFieldChange.builder()
        .field("linkNote")
        .fromValue("")
        .toValue(linkNote)
        .build();
    clfc.add(clfchange);
    try {
      changeLogService.registerChangesSystem(
          "user",
          "edit",
          primaryUser.getId(),
          "Created a new user link (" + linkType.getCode().toLowerCase() + ") from primary (" + primaryUser.getGuid() + ") to secondary (" + secondaryUser.getGuid() + ")",
          null,
          clfc,
          Category.ACCOUNT,
          SubCategory.USER_LINK,
          0,
          primaryUser.domainName());
    } catch (Exception e) {
      log.debug("Unable to create change log for auto user link: " + userLink);
    }

    pubSubUserService.buildAndSendAccountLinkCreate(userLink, PubSubEventType.AUTOLINK_CREATE);

    /**
     * Triggering auto-restrictions rerun; this should happen twice on link changes;
     * first with root user as primary and second run with exclusive user as primary
     */
    autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder().userGuid(primaryUser.guid()).build());

    return userLink;
  }

  public void syncCommunicationChannelsForRoot(User rootUser, User user, boolean optOut) throws Status550ServiceDomainClientException {

    //Get single opt setting for LSBET domain
    Optional<String> singledOptInSetting = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName()).findDomainSettingByName(DomainSettings.SINGLE_OPT_ALL_CHANNELS.key());

    if(singledOptInSetting.isPresent() && singledOptInSetting.get().equalsIgnoreCase("true")) {
      rootUser.setPushOptOut(optOut);
      rootUser.setEmailOptOut(optOut);
      userService.save(rootUser, true);

      try {
        pubSubUserService.buildAndSendPubSubAccountCreate(rootUser, PubSubEventType.ACCOUNT_CREATE);
        PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder pubSubMarketingPreferences = PubSubMarketingPreferences.builder();
        pubSubMarketingPreferences.callOptOut(rootUser.getCallOptOut());
        pubSubMarketingPreferences.leaderBoardOptOut(rootUser.getLeaderboardOptOut());
        pubSubMarketingPreferences.pushOptOut(rootUser.getPushOptOut());
        pubSubMarketingPreferences.emailOptOut(rootUser.getEmailOptOut());
        pubSubMarketingPreferences.postOptOut(rootUser.getPostOptOut());
        pubSubMarketingPreferences.smsOptOut(rootUser.getSmsOptOut());
        pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.USER, pubSubMarketingPreferences, null, PubSubEventType.MARKETING_PREFERENCES);
      } catch (Exception ex) {
        log.error("pubSub message failed for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
      }

      List<ChangeLogFieldChange> changeLogFieldChanges =  new ArrayList<>();

      try {
        changeLogFieldChanges = changeLogService.copy(rootUser, new User(),
            new String[]{ "emailOptOut","pushOptOut"});
      } catch (Exception e) {
        log.error("Could not copy change field values from " + rootUser, e);
      }

      for(ChangeLogFieldChange changeLogFieldChange: changeLogFieldChanges) {
        String type = changeLogFieldChange.getField().substring(0, changeLogFieldChange.getField().indexOf("OptOut"));
        String comment = String.format("You have successfully opted-%s to %s", Boolean.parseBoolean(changeLogFieldChange.getToValue()) ? "out": "in", type);

        changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", rootUser.getId(), rootUser.guid(), null, comment,
            null, Arrays.asList(changeLogFieldChange), Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, rootUser.getDomain().getName());
      }
    }
  }

  public User getLinkedEcosystemUser(User user) {
    User linkedUser = null;
    try {
      if (cachingDomainClientService.isDomainInAnyEcosystem(user.domainName())) {
        List<UserLink> userLinks = findUserLinksByUserAndUserLinkType(user.guid(), findUserLinkType(UserLinkTypes.CROSS_DOMAIN_LINK));
        Optional<UserLink> userLink = userLinks.stream().filter(link -> Objects.equals(link.getPrimaryUser().getId(), user.getId())).findFirst();
        if (userLink.isPresent()) {
          linkedUser = userLink.get().getSecondaryUser();
        }
      }
    } catch (Status100InvalidInputDataException | Status550ServiceDomainClientException e) {
      log.error("Could not retrieve linked ecosystem user for {} due to error: {}", user.guid(), e.getMessage());
    }

    return linkedUser;
  }

  @Transactional
  public void deleteRootUserLink(User user) throws Status403AccessDeniedException {

    if (!cachingDomainClientService.isDomainNameOfEcosystemRootType(user.domainName())) {
      throw new Status403AccessDeniedException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.PROFILE_DELETION_DENIED",
          new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName()), user.domainName()}, LocaleContextHolder.getLocale()));
    }

    UserLinkType linkType = findUserLinkType(UserLinkTypes.CROSS_DOMAIN_LINK);
    UserLinkType deletedLinkType = findUserLinkType(UserLinkTypes.DELETED_ROOT_DOMAIN_LINK);

    String linkNote = "User account has been deleted";

    UserLink rootLink = userLinkRepository.findByPrimaryUserAndDeletedFalseAndUserLinkTypeCode(user, linkType.getCode());

    if(!ObjectUtils.isEmpty(rootLink)){
      User exclusiveUser = rootLink.getSecondaryUser();

      UserLink exclusiveAccountLink = userLinkRepository.findByPrimaryUserAndSecondaryUserAndDeletedFalseAndUserLinkType(exclusiveUser, user,
          linkType);

        rootLink.setUserLinkType(deletedLinkType);
        rootLink.setLinkNote(linkNote);
        deleteRootUserLinkNotesAndPubSub(user, exclusiveUser, linkNote, deletedLinkType, rootLink, false);

      if (!ObjectUtils.isEmpty(exclusiveAccountLink)) {
        exclusiveAccountLink.setUserLinkType(deletedLinkType);
        exclusiveAccountLink.setLinkNote(linkNote);
        deleteRootUserLinkNotesAndPubSub(exclusiveUser, user, linkNote, deletedLinkType, exclusiveAccountLink, false);
      }

    } else {
      UserLink newLink = UserLink.builder().primaryUser(user).userLinkType(deletedLinkType).linkNote(linkNote).build();
      deleteRootUserLinkNotesAndPubSub(user, null, linkNote, deletedLinkType, newLink, true);
    }
  }

  private void deleteRootUserLinkNotesAndPubSub(User primaryUser, User secondaryUser, String linkNote, UserLinkType linkType, UserLink userLink, boolean isDeletedRootUser) {
    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    ChangeLogFieldChange clfchange = ChangeLogFieldChange.builder()
        .field("linkNote")
        .fromValue("")
        .toValue(linkNote)
        .build();
    clfc.add(clfchange);
    try {
      userLinkRepository.save(userLink);
      changeLogService.registerChangesForNotesWithFullNameAndDomain(
          "user",
          "edit",
          primaryUser.getId(),
          primaryUser.guid(),
          null,
          "Root user account was deleted userLink (" + linkType.getCode().toLowerCase() + ") from primary (" + primaryUser.getGuid() + ") to secondary ("
              .concat(isDeletedRootUser ? "": secondaryUser.getGuid()).concat(")"),
          null,
          clfc,
          Category.ACCOUNT,
          SubCategory.USER_LINK,
          0,
          primaryUser.domainName());
    } catch (Exception e) {
      log.debug("Unable to create change log for delete root domain user link: " + userLink);
    }

    pubSubUserService.buildAndSendAccountLinkCreate(userLink, PubSubEventType.ACCOUNT_DELETED);
  }

  public User performDeletedLinkChecks(User user) {
    UserLink found = userLinkRepository.findByPrimaryUserAndDeletedFalseAndUserLinkTypeCode(user, UserLinkTypes.DELETED_ROOT_DOMAIN_LINK.code());

    if(!ObjectUtils.isEmpty(found)){
      return found.getPrimaryUser();
    }

    return null;
  }
}
