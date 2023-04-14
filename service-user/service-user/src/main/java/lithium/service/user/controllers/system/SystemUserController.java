package lithium.service.user.controllers.system;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.EcosystemRelationshipTypes;
import lithium.service.user.client.SystemUserClient;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.ContactDetails;
import lithium.service.user.client.objects.DuplicateCheckRequestData;
import lithium.service.user.client.objects.EcosystemUserProfiles;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.UserBiometricsStatusUpdate;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.data.entities.CollectionDataRevisionEntry;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.BiometricsStatusService;
import lithium.service.user.services.CollectionDataService;
import lithium.service.user.services.LoginEventService;
import lithium.service.user.services.UserCategoryService;
import lithium.service.user.services.UserProfileService;
import lithium.service.user.services.UserService;
import lithium.service.user.services.UserVerificationStatusService;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequestMapping("/system/user")
public class SystemUserController implements SystemUserClient {

  @Autowired
  private UserService userService;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private LoginEventService loginEventService;
  @Autowired
  private UserVerificationStatusService userVerificationStatusService;
  @Autowired
  private UserCategoryService userCategoryService;
  @Autowired
  private LithiumTokenUtilService tokenService;
  @Autowired
  private UserProfileService userProfileService;
  @Autowired
  private CollectionDataService collectionDataService;
  @Autowired
  private BiometricsStatusService biometricsStatusService;


  @GetMapping("/get-ecosystem-user-profile")
  public Response<EcosystemUserProfiles> getEcosystemUserProfile(@RequestParam("id") User user) {
    try {
      return Response.<EcosystemUserProfiles>builder().data(EcosystemUserProfiles.builder()
          .ecosystemUserProfileList(userProfileService.buildEcosystemUserProfilesResponse(user))
          .build())
          .status(Status.OK_SUCCESS)
          .build();
    } catch (Exception e) {
      log.error("Failed to build EcosystemUserProfile for user: " + user.getGuid() + ". Message: " + e.getMessage() + ". Failed from: " + e.getStackTrace());
      return Response.<EcosystemUserProfiles>builder().message(e.getMessage()).status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/ecosystem/linked-user-guid/{relationship-type}")
  public Response<lithium.service.user.client.objects.User> getLinkedEcosystemUserGuid(@RequestParam("userGuid") String userGuid, @PathVariable("relationship-type") EcosystemRelationshipTypes relationshipType) {
    try {
      return Response.<lithium.service.user.client.objects.User>builder()
          .data(userProfileService.getLinkedEcosystemUserGuid(userGuid, relationshipType))
          .status(Status.OK_SUCCESS)
          .build();
    } catch (Exception e) {
      log.error("Failed to getLinkedEcosystemUserGuid for user: " + userGuid + ". Message: " + e.getMessage(), e);
      return Response.<lithium.service.user.client.objects.User>builder().message(e.getMessage()).status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/update-protection-of-customer-funds-version")
  public User updateProtectionOfCustomerFundsVersion(@RequestParam("userGuid") String userGuid) throws Status500InternalServerErrorException {
    return userService.updateProtectionOfCustomerFundsVersion(userGuid);
  }

  @GetMapping("/additionaldata")
  public Response<Map<String, String>> findUserLabelValues(@RequestParam("userGuid") String userGuid) throws UserNotFoundException {
    var user = userService.findFromGuid(userGuid);
    if (user == null) {
      throw new UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_NOT_FOUND",
          new Object[]{new lithium.service.translate.client.objects.Domain(userGuid.split("/")[0])}, "User not found or invalid user guid.",
          LocaleContextHolder.getLocale()));
    }

    HashMap<String, String> additionalData = new HashMap<>();
    if (user.getCurrent() != null) {
      for (UserRevisionLabelValue userLabelValue : user.getCurrent().getLabelValueList()) {
        additionalData.put(userLabelValue.getLabelValue().getLabel().getName(), userLabelValue.getLabelValue().getValue());
      }
    }
    return Response.<Map<String, String>>builder().data(additionalData).status(Status.OK).build();
  }

  @PostMapping("/additionaldata")
  public Response<String> updateOrAddUserLabelValues(@RequestParam("userGuid") String userGuid, @RequestBody Map<String, String> additionalData)
      throws UserNotFoundException {
    var user = userService.findFromGuid(userGuid);
    if (user == null) {
      throw new UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_NOT_FOUND",
          new Object[]{new lithium.service.translate.client.objects.Domain(userGuid.split("/")[0])}, "User not found or invalid user guid.",
          LocaleContextHolder.getLocale()));
    }

    userService.addOrUpdateDomainSpecificUserLabelValues(user, additionalData);
    return Response.<String>builder().data(Status.OK.name()).status(Status.OK).build();
  }

  @PostMapping("/validate-session")
  public void validateSession(@RequestParam("domainName") String domainName, @RequestParam("loginEventId") Long loginEventId)
      throws Status401UnAuthorisedException {
    loginEventService.validateSession(domainName, loginEventId);
  }

  @PostMapping("/validate-and-update-session")
  public void validateAndUpdateSession(@RequestParam("domainName") String domainName, @RequestParam("loginEventId") Long loginEventId)
      throws Status401UnAuthorisedException {
    loginEventService.validateAndUpdateSession(domainName, loginEventId);
  }

  @PostMapping("/logout")
  public void logout(@RequestParam("userGuid") String userGuid) {
    loginEventService.logout(userGuid, null); // Logout all active sessions
  }

  @PostMapping("/save-verification-status")
  public Response<User> updateVerificationStatus(
      @RequestParam ("forceUpdate") boolean forceUpdate,
      @RequestBody @Valid UserVerificationStatusUpdate userVerificationStatusUpdate,
      Principal principal) throws Exception {
    userVerificationStatusUpdate.setAuthorName(tokenService.getUtil(principal).guid());
    return Response.<User>builder()
        .status(Status.OK)
        .data(userVerificationStatusService.updateVerificationStatus(forceUpdate, userVerificationStatusUpdate, principal))
        .build();
  }

  @PutMapping("/biometrics-status")
  public Response<User> updateBiometricsStatus(@RequestBody @Valid UserBiometricsStatusUpdate userBiometricsStatusUpdate, Principal principal) {
    return Response.<User>builder()
        .status(Status.OK)
        .data(biometricsStatusService.updateUserBiometricsStatus(userBiometricsStatusUpdate, principal))
        .build();
  }

  @PostMapping(value = "/{id}/test-account/{isTestAccount}")
  public Response<User> setTest(@PathVariable("id") User user, @PathVariable("isTestAccount") boolean isTestAccount, Principal principal)
      throws Exception {
    return Response.<User>builder()
        .data(userService.setTest(user, isTestAccount, principal))
        .status(Status.OK)
        .build();
  }

  @PostMapping("/{id}/tag/add")
  public Response<User> categoryAddPlayer(
      @PathVariable("id") User user,
      @RequestParam(name = "tagIds") List<Long> tagIds,
      Principal principal
  ) throws Exception {
    List<UserCategory> userCategories = userCategoryService.findUserCategories(tagIds, user.getDomain().getName());
    log.debug("UserTag AddPlayer : " + user.getUsername() + " to : " + userCategories);

    return Response.<User>builder()
        .status(Status.OK)
        .data(userCategoryService.categoryAddPlayer(user, userCategories, principal))
        .build();
  }

  @DeleteMapping("/{id}/tag/remove")
  public Response<User> categoryRemovePlayer(
      @PathVariable("id") User user,
      @RequestParam(name = "tagIds") List<Long> tagIds,
      Principal principal
  ) throws Exception {
    List<UserCategory> userCategories = userCategoryService.findUserCategories(tagIds, user.getDomain().getName());
    log.debug("tagRemovePlayer : " + user.getUsername() + " from : " + userCategories);

    return Response.<User>builder()
        .status(Status.OK)
        .data(userCategoryService.categoryRemovePlayer(user, userCategories, principal))
        .build();
  }

  @DeleteMapping("/{id}/tag/remove/all")
  public Response<User> categoryRemoveAllPlayer(
      @PathVariable("id") User user,
      Principal principal
  ) throws Exception {
    List<UserCategory> userCategories = user.getUserCategories() != null ? user.getUserCategories() : new ArrayList<>();
    log.debug("tagRemovePlayer : " + user.getUsername() + " from : " + userCategories);

    return Response.<User>builder()
        .status(Status.OK)
        .data(userCategoryService.categoryRemovePlayer(user, userCategories, principal))
        .build();
  }

  @PostMapping(value = "/{id}/save-promotions-out-out")
  public Response<User> setPromotionsOptOut(@PathVariable("id") Long id, @RequestParam("optOut") boolean optOut, Principal principal) {
    return Response.<User>builder()
        .data(userService.setPromotionsOptOut(id, optOut, principal))
        .status(Status.OK)
        .build();
  }

  @PostMapping(value = "/find-userguids-with-birthdays-today")
  public SimplePageImpl<String> getUserGuidsWhosBirthdayIsToday(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
      @RequestParam(name = "limit", required = false, defaultValue = "1000") int limit,
      @RequestBody List<String> guids) {

    var pageRequest = PageRequest.of(page, limit);
    Page<String> results = userService.getUserGuidsWithBirthdayOn(guids, pageRequest, DateTime.now());

    return new SimplePageImpl<>(results.getContent(), page, limit, results.getTotalElements());
  }

  @PostMapping(value = "/find-user-duplicates")
  public Response<List<User>> findDuplicateUsers(@RequestBody DuplicateCheckRequestData data) {
    List<User> users;
    if (isNull(data.getPostcode())) {
      users = userService.findByDomainNameAndFirstNameAndLastNameAndBirthDayAndIdNot(data);
    } else {
      users = userService.findByDomainNameAndLastNameAndBirthDayAndPostcodeAndIdNot(data);
    }
    return Response.<List<User>>builder()
        .status(Status.OK)
        .data(users)
        .build();
  }

  @GetMapping(value = "/find-user-by-username-then-email-then-cell")
  public Response<User> findByUsernameThenEmailThenCell(@RequestParam("domainName") String domainName,
      @RequestParam("UsernameEmailOrCell") String usernameEmailOrCell ){
    return Response.<User>builder()
        .status(Status.OK)
        .data(userService.findByUsernameThenEmailThenCell(domainName, usernameEmailOrCell))
        .build();
  }

  @PostMapping("/collection-data")
  public Response<List<CollectionDataRevisionEntry>> collectionData(@RequestBody PlayerBasic playerBasic) {
    List<CollectionDataRevisionEntry> revisionEntries = collectionDataService.createOrUpdateCollectionData(playerBasic, playerBasic.getId());
    return Response.<List<CollectionDataRevisionEntry>>builder().data(revisionEntries).status(Status.OK_SUCCESS).build();
  }

  @PostMapping("/set-contact-details-validated")
  public lithium.service.user.client.objects.User validateContactDetails(@RequestBody ContactDetails contactDetails) {
    return userService.setContactDetailsValidated(contactDetails);
  }
}
