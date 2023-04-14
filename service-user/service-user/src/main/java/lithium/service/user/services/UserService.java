package lithium.service.user.services;

import com.codahale.passpol.BreachDatabase;
import com.codahale.passpol.PasswordPolicy;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status428AccountFrozenGamstopSelfExcludedException;
import lithium.exceptions.Status433AccountBlockedPlayerRequestException;
import lithium.exceptions.Status434AccountBlockedResponsibleGamingException;
import lithium.exceptions.Status435AccountBlockedAMLException;
import lithium.exceptions.Status436AccountBlockedDuplicatedAccountException;
import lithium.exceptions.Status437AccountBlockedOtherException;
import lithium.exceptions.Status446AccountFrozenCRUKSSelfExcludedException;
import lithium.exceptions.Status447AccountFrozenException;
import lithium.exceptions.Status448AccountBlockedException;
import lithium.exceptions.Status450AccountFrozenSelfExcludedException;
import lithium.exceptions.Status453EmailNotUniqueException;
import lithium.exceptions.Status455AccountBlockedFraudException;
import lithium.exceptions.Status469InvalidInputException;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.UserGuidStrategy;
import lithium.service.accounting.client.service.AccountingClientService;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.AdjustMultiRequest;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.casino.client.SportsBookClient;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.EcosystemRelationshipTypes;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.objects.PromotionRestrictionTriggerData;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.limit.client.stream.PromotionRestrictionTriggerStream;
import lithium.service.translate.client.objects.Module;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.ServiceUserApplication;
import lithium.service.user.client.enums.BiometricsStatus;
import lithium.service.user.client.objects.AccountCode;
import lithium.service.user.client.objects.AccountStatusErrorCodeAndMessage;
import lithium.service.user.client.objects.ContactDetails;
import lithium.service.user.client.objects.DuplicateCheckRequestData;
import lithium.service.user.client.objects.FrontendPlayerBasic;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.PasswordBasic;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.client.objects.TermsAndConditionsVersion;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.client.objects.UserAccountStatusUpdateBasic;
import lithium.service.user.client.objects.UserAttributesData;
import lithium.service.user.client.objects.UserChanges;
import lithium.service.user.client.objects.UserEventBasic;
import lithium.service.user.client.stream.UserAttributesTriggerStream;
import lithium.service.user.client.stream.synchronize.UserSynchronizeStream;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.Group;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.StatusReason;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.entities.UserProjection;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import lithium.service.user.data.repositories.AddressRepository;
import lithium.service.user.data.repositories.StatusReasonRepository;
import lithium.service.user.data.repositories.StatusRepository;
import lithium.service.user.data.repositories.UserRepository;
import lithium.service.user.data.specifications.UserSpecifications;
import lithium.service.user.exceptions.Status420InvalidEmailException;
import lithium.service.user.exceptions.Status421InvalidCellphoneException;
import lithium.service.user.exceptions.Status422InvalidDateOfBirthException;
import lithium.service.user.exceptions.Status423InvalidPasswordException;
import lithium.service.user.exceptions.Status431UserExistsInEcosystemException;
import lithium.service.user.exceptions.Status454CellphoneNotUniqueException;
import lithium.service.user.exceptions.Status456NewPasswordsMismatchException;
import lithium.service.user.exceptions.Status457CurrentAndNewPasswordMatchException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.notify.PasswordChangeNotificationService;
import lithium.service.user.services.notify.PreValidationNotificationService;
import lithium.service.user.validators.UserValidatorProperties;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lithium.util.DateUtil;
import lithium.util.ExceptionMessageUtil;
import lithium.util.JsonStringify;
import lithium.util.PasswordHashing;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.collection.spi.PersistentCollection;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
public class UserService {
  @Value("${lithium.password.salt}") @Setter
  private String passwordSalt;

  @Autowired private CachingDomainClientService cachingDomainClientService;
  @Autowired DomainService domainService;
  @Autowired @Setter UserRepository userRepository;
  @Autowired StatusRepository statusRepository;
  @Autowired StatusReasonRepository statusReasonRepository;
  @Autowired ModelMapper modelMapper;
  @Autowired @Setter MessageSource messageSource;
  @Autowired @Setter PasswordChangeNotificationService passwordChangeNotificationService;
  @Autowired @Setter ChangeLogService changeLogService;
  @Autowired @Setter LimitInternalSystemService limitService;
  @Autowired private UserStatusService userStatusService;
  @Autowired private UserEventService userEventService;
  @Autowired private PubSubUserService pubSubUserService;
  @Autowired @Setter UserLinkService userLinkService;
  @Autowired AddressRepository addressRepository;
  @Autowired UserLabelValueService userLabelValueService;
 // @Autowired @Setter ExternalDomainService externalDomainService;
  @Autowired LoginEventService loginEventService;
  @Autowired LithiumTokenUtilService tokenService;
  @Autowired EntityManager entityManager;
  @Autowired private UserAttributesTriggerStream userAttributesTriggerStream;
  @Autowired private CachingDomainClientService domainClientService;
  @Autowired private AccessRuleService accessRuleService;
  @Autowired private PreValidationNotificationService preValidationNotificationService;
  @Autowired private ModelMapper mapper;
  @Autowired private TokenStore tokenStore;
  @Autowired private UserService self;
  @Autowired private EmailValidationService emailValidationService;
  @Autowired private SMSValidationService smsValidationService;  ;
  @Autowired private UserSynchronizeStream userSynchronizeStream;
  @Autowired AutoRestrictionTriggerStream autoRestrictionTriggerStream;
  @Autowired LimitInternalSystemService limitInternalSystemService;
  @Autowired LithiumServiceClientFactory lithiumServiceClientFactory;
  @Autowired PromotionRestrictionTriggerStream promotionRestrictionTriggerStream;
  @Autowired AccountingClientService accountingClientService;
  @Autowired UserProfileService userProfileService;
  @Autowired @Setter UserPasswordHashAlgorithmService userPasswordHashAlgorithmService;

//	@PersistenceContext
//	private EntityManager entityManager;
  /**
   * Saving a user entity
   * If you need to avoid ecosystem syncing, pass in skipEcosystemSync flag as true
   * @param user
   * @param skipEcosystemSync Pass as true to avoid ecosystem sync
   * @return User
   */
  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User save(User user, boolean skipEcosystemSync) {
    boolean syncHappened = false;
    if (!skipEcosystemSync) {
      try {
        syncHappened = userLinkService.applyEcosystemUserDataSynchronisation(user);
      } catch (Status550ServiceDomainClientException e) {
        log.warn("Unable to apply ecosystem sync during user save: " +
            user);
      }
    }

    if (!syncHappened) {
      return userRepository.save(user);
    } else {
      return userRepository.findOne(user.getId());
    }
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User save(User user) {
    return save(user, false); // Default is to perform ecosystem sync
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User saveStatus(final long userId, final String strStatus, final String strReason) {
    Status status = statusRepository.findByName(strStatus);
    StatusReason reason = statusReasonRepository.findByName(strReason);
    return saveStatus(userId, status, reason);
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User saveVerificationStatus(final long userId, final long verificationStatusId) {
    User latestUser = userRepository.findOne(userId);
    latestUser.setVerificationStatus(verificationStatusId);
    return save(latestUser);
  }

  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public lithium.service.user.client.objects.User setContactDetailsValidated(ContactDetails contactDetails) {
    lithium.service.user.client.objects.User mappedUser = null;
    try {
      if(!ObjectUtils.isEmpty(contactDetails.getUserGuid())) {
        User user = findFromGuid(contactDetails.getUserGuid());
        user.setAddressVerified(contactDetails.getAddress().isAddressVerified());
        user.setEmailValidated(contactDetails.getEmailValidate().isEmailValidated());
        user.setCellphoneValidated(contactDetails.getCellphoneNumberValidate().isCellphoneValidated());
        User savedUser = save(user);
        mappedUser = modelMapper.map(savedUser, lithium.service.user.client.objects.User.class);
      }
    } catch (Exception ex) {
      log.error("Contact details for {} could not be validated : ", contactDetails.getUserGuid(), ex.getMessage(), ex);
    }
    return mappedUser;
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User saveStatusIfUserEnabled(long userId, final String strStatus, final String strReason) {
    User latestUser = userRepository.findOne(userId);
    Status status = statusRepository.findByName(strStatus);
    StatusReason reason = statusReasonRepository.findByName(strReason);
    if (latestUser.getStatus().getUserEnabled()) {
      log.debug("User enabled, modifying status to : " + status +" reason: " + reason + " user: " + latestUser);
      latestUser.setStatus(status);
      latestUser.setStatusReason(reason);
      return save(latestUser);
    } else {
      log.debug("User already disabled, not setting status: " + latestUser);
    }
    return latestUser;
  }

  private User saveStatus(long userId, final Status status, final StatusReason reason) {
    User latestUser = userRepository.findOne(userId);
    latestUser.setStatus(status);
    latestUser.setStatusReason(reason);
    return save(latestUser);
  }

  public User get(String domainName, String userNameOrId) {
    switch (ServiceUserApplication.GUID_STRATEGY) {
      case ID: {
        if (StringUtils.isNumeric(userNameOrId)) {
          return findOne(Long.parseLong(userNameOrId));
        } else {
          //Perform a username lookup since the info passed was not a numeric value
          log.info("Performed username lookup even though we should be using the guid ID strategy, +"
              + "the caller needs to be fixed: domain: " + domainName + " userNameOrId: " + userNameOrId);
          return findByDomainNameAndUsername(domainName, userNameOrId);
        }
      }
      case USERNAME:
        return findByDomainNameAndUsername(domainName, userNameOrId);
      default:
        throw new IllegalArgumentException("User with domainName (" + domainName + ") and userNameOrId (" + userNameOrId + ") does not exist for strategy: "+ServiceUserApplication.GUID_STRATEGY);
    }
  }

  @TimeThisMethod
  public User findByUsernameThenEmailThenCell(String domainName, String search) {
    log.debug("findByUsernameThenEmailThenCell("+domainName+", "+search+")");
    SW.start("username");
    User user = userRepository.findByDomainNameAndUsername(domainName.toLowerCase(), search.toLowerCase());
    SW.stop();
    if (user == null) {
      SW.start("username");
      List<User> l = userRepository.findByDomainNameAndEmail(domainName.toLowerCase(), search.toLowerCase());
      SW.stop();
      if (l.size() == 1) user = l.get(0);
      if (user == null) {
        SW.start("username");
        l = userRepository.findByDomainNameAndCellphoneNumber(domainName.toLowerCase(), search.toLowerCase());
        SW.stop();
        if (l.size() == 1) user = l.get(0);
      }
    }
    log.debug("Found : "+((user==null)?" no local user.":user.toString()));
    return user;
  }

  public User findOne(Long id) {
    return userRepository.findOne(id);
  }
  public User findByDomainNameAndUsername(String domainName, String username) {
    return userRepository.findByDomainNameAndUsername(domainName.toLowerCase(), username.toLowerCase());
  }

  public List<User> findByDomainNameAndUsernameEcosystemAware(String domainName, String username)
      throws
      Status469InvalidInputException,
      Status550ServiceDomainClientException {
    List<String> domainNameListInEcosystem = new ArrayList<>(
        cachingDomainClientService.listDomainNamesInEcosystemByDomainName(domainName.toLowerCase()));
    return userRepository.findByDomainNameInAndUsername(domainNameListInEcosystem, username.toLowerCase());
  }

  public List<User> findByDomainNameAndEmail(String domainName, String email) {
    if (email == null) return Collections.emptyList();
    return userRepository.findByDomainNameAndEmail(domainName.toLowerCase(), email.toLowerCase());
  }
  public List<User> findByDomainNameAndLabelAndLabelValue(String domainName, String label, String labelValue) {
    if (ObjectUtils.isEmpty(label) || ObjectUtils.isEmpty(labelValue)) {
      return Collections.emptyList();
    }
    Specification<User> spec = Specification
        .where(UserSpecifications.userByDomainWithLabelAndOptionalValue(Collections.singletonList(domainName), label, labelValue));
    return userRepository.findAll(spec);
  }

  public Optional<User> findByDomainNameInAndEmail(List<String> domainNames, String email) {
    return userRepository
        .findByDomainNameInAndEmail(domainNames.stream()
                .map(domainName -> domainName.toLowerCase())
                .collect(Collectors.toList()),
            email.toLowerCase()).stream().findAny();
  }

  //FIXME: REMOVE: This is not ayoba, the only unique ecosystem identifier within an ecosystem is the email; this should be fixed ASAP, along with its
  //       internal methods. UserService#findByDomainNameInAndEmail is the correct way in finding a user. It is expected to have resolved the
  //       domain names belonging to an ecosystem before calling this method; hence UserProfileService#getLinkedEcosystemUserGuid was built. It might
  //       actually make more sence to move UserProfileService#getLinkedEcosystemUserGuid into UserLinkService instead of UserProfileService ;)
  @Deprecated(since = "3.15")
  public Optional<User> findMutualExclusiveUserByDomainNamesAndEmailThenCellphoneNumberThenUsername(List<String> domainNames, String uniqueIdentifier) {
    if (uniqueIdentifier == null) return Optional.empty();
    Optional<User> user = findByMutualExclusiveDomainAndEmail(domainNames, uniqueIdentifier);

    if (!user.isPresent()) {
      user = findByMutualExclusiveDomainAndCellphone(domainNames, uniqueIdentifier);

      if (!user.isPresent()) {
        user = findByMutualExclusiveDomainAndUsername(domainNames, uniqueIdentifier);
      }
    }
    return user;
  }

  //FIXME: REMOVE: No need in calling it Mutual Exclusive Domain In, if it is not aware of ecosystem domain relationships
  @Deprecated(since = "3.15")
  public Optional<User> findByMutualExclusiveDomainAndEmail(List<String> domainNames, String email) {
    return userRepository
        .findByDomainNameInAndEmail(domainNames.stream()
                .map(domainName -> domainName.toLowerCase())
                .collect(Collectors.toList()),
            email.toLowerCase()).stream().findAny();
  }

  //FIXME: REMOVE: Username is not an ecosystem unique identifier
  @Deprecated(since = "3.15")
  public Optional<User> findByMutualExclusiveDomainAndUsername(List<String> domainNames, String username) {
    return userRepository
        .findByDomainNameInAndUsername(domainNames.stream()
                .map(domainName -> domainName.toLowerCase())
                .collect(Collectors.toList()),
            username.toLowerCase()).stream().findAny();
  }

  //FIXME: REMOVE: CellphoneNumber is not an ecosystem unique identifier
  @Deprecated(since = "3.15")
  public Optional<User> findByMutualExclusiveDomainAndCellphone(List<String> domainNames, String cellphoneNumber) {
    return userRepository
        .findByDomainNameInAndCellphoneNumber(domainNames.stream()
                .map(domainName -> domainName.toLowerCase())
                .collect(Collectors.toList()),
            cellphoneNumber.toLowerCase()).stream().findAny();
  }

  public List<User> findByDomainNameAndEmailEcosystemAware(String domainName, String email)
      throws
      Status469InvalidInputException,
      Status550ServiceDomainClientException {
    if (email == null) return Collections.emptyList();
    List<String> domainNameListInEcosystem = cachingDomainClientService.listDomainNamesInEcosystemByDomainName(domainName.toLowerCase()).stream().collect(
        Collectors.toList());
    return userRepository.findByDomainNameInAndEmail(domainNameListInEcosystem, email.toLowerCase());
  }

  public List<User> findByDomainNameAndLabelEcosystemAware(String domainName, String label, String value)
      throws
      Status469InvalidInputException,
      Status550ServiceDomainClientException {

    if (ObjectUtils.isEmpty(label) || ObjectUtils.isEmpty(value)|| ObjectUtils.isEmpty(domainName)) {
      return Collections.emptyList();
    }

    List<String> domainNameListInEcosystem = new ArrayList<>(
        cachingDomainClientService.listDomainNamesInEcosystemByDomainName(domainName.toLowerCase()));

    if(ObjectUtils.isEmpty(domainNameListInEcosystem)){
      return Collections.emptyList();
    }

    Specification<User> spec = Specification.where(UserSpecifications.userByDomainWithLabelAndOptionalValue(domainNameListInEcosystem, label, value));
    return userRepository.findAll(spec);
  }

  //TODO: When africa needs to be done we need to cater for cellphone lookups as well
  public List<User> findByDomainNameAndMobile(String domainName, String mobile) {
    if (mobile == null) return Collections.emptyList();
    return userRepository.findByDomainNameAndCellphoneNumber(domainName.toLowerCase(), mobile.toLowerCase());
  }

  public List<User> findByDomainNameAndEmailOrCellphoneNumber(String domainName, String email, String cellphoneNumber) {
    List<User> users = findByDomainNameAndEmail(domainName, email);
    if (users.isEmpty()) {
      users = findByDomainNameAndMobile(domainName, cellphoneNumber);
      if (users.isEmpty()) return Collections.emptyList();
    }
    return users;
  }

  public List<User> findByCreatedRange(List<String> domains, DateTime startDate, DateTime endDate) {
    return userRepository.findByDomainNameInAndCreatedDateBetween(domains, startDate.toDate(), endDate.toDate());
  }

  public User findFromGuid(String guid) {

    User user = userRepository.findByGuid(guid);
    log.trace("Retrieved User from DB #### " + user);
    if (user != null) return user;

    String[] split = guid.split("/", 2);

    if (split.length != 2) {
      return null;
    }
    return get(split[0], split[1]);
  }

  public lithium.service.user.client.objects.User findAndConvert(String domainName, String userNameOrId) {
    return convert(get(domainName, userNameOrId));
  }
  public lithium.service.user.client.objects.User findAndConvert(String guid) {
    return convert(findFromGuid(guid));
  }
  public lithium.service.user.client.objects.User convert(User user) {

    lithium.service.user.client.objects.User userClient = modelMapper.map(user, lithium.service.user.client.objects.User.class);
    if (user.getUserCategories() != null) {
      userClient.setUserCategories(
          user.getUserCategories()
              .stream()
              .map(entity -> lithium.service.user.client.objects.UserCategory.builder().name(entity.getName()).id(entity.getId()).description(entity.getDescription()).build())
              .toList());
    }
    return userClient;
  }

  public boolean isUniqueUsername(String domainName, String username) {
    return isUniqueUsername(domainName, username, true);
  }

  public Boolean isUniqueUsername(String domainName, String username, User user) {
    return isUniqueUsername(domainName, username, !cachingDomainClientService.isDomainInAnyEcosystem(user.domainName()));
  }
  public boolean isUniqueUsername(String domainName, String username, boolean domainOnly) {
    try {
      if (!domainOnly && cachingDomainClientService.isDomainInAnyEcosystem(domainName)) {
        //Domain is in an ecosystem so we need to check all domains in that ecosystem for uniqueness
        return isUniqueUsernameEcosystemAware(domainName, username);
      }
    } catch (Status550ServiceDomainClientException e) {
      log.warn("Unable to resolve domain existence in ecosystem. domain: " + domainName + " username: " + username, e);
    }

    return (findByDomainNameAndUsername(domainName, username) == null);
  }
  public boolean isUniqueEmail(String domainName, String email) {
    return isUniqueEmail(domainName, email, true);
  }
  public boolean isUniqueEmail(String domainName, String email, boolean domainOnly) {
    try {
      if (!domainOnly && cachingDomainClientService.isDomainInAnyEcosystem(domainName)) {
        //Domain is in an ecosystem so we need to check all domains in that ecosystem for uniqueness
        return isUniqueEmailEcosystemAware(domainName, email);
      }
    } catch (Status550ServiceDomainClientException e) {
      log.warn("Unable to resolve domain existence in ecosystem. domain: " + domainName + " email: " + email, e);
    }
    return (findByDomainNameAndEmail(domainName, email)).isEmpty()
        && checkEmailUniqueByLabel(domainName, email, false);
  }

  public boolean isUniqueUsernameEcosystemAware(String domainName, String username) {
    try {
      return (findByDomainNameAndUsernameEcosystemAware(domainName, username).isEmpty());
    } catch (
        Status469InvalidInputException e) {
      log.info("Problem looking up uniqueness of username in " + domainName + " for username " + username + " " + e.getMessage());
      return (findByDomainNameAndUsername(domainName, username) == null);
    } catch (Status550ServiceDomainClientException e) {
      log.warn("Problem looking up uniqueness of username in " + domainName + " for username " + username, e);
    }
    return false;
  }

  public boolean isUniqueEmailEcosystemAware(String domainName, String email) {
    try {
      return findByDomainNameAndEmailEcosystemAware(domainName, email).isEmpty()
          && checkEmailUniqueByLabel(domainName, email, true);
    } catch (
        Status469InvalidInputException e) {
      log.debug("Problem looking up uniqueness of email in " + domainName + " for email " + email + " " + e.getMessage());
      return (findByDomainNameAndEmail(domainName, email)).isEmpty();
    } catch (Status550ServiceDomainClientException e) {
      log.warn("Problem looking up uniqueness of email in " + domainName + " for email " + email, e);
    }
    return false;
  }

  private boolean checkEmailUniqueByLabel(String domainName, String email, boolean ecosystemCheck) {

    if (ecosystemCheck) {
      try {
        return findByDomainNameAndLabelEcosystemAware(domainName, Label.PENDING_EMAIL, email).isEmpty();
      } catch (Status469InvalidInputException e) {
        log.debug("Problem looking up uniqueness of email via label in " + domainName + " for email " + email + " " + e.getMessage());
        return (findByDomainNameAndLabelAndLabelValue(domainName, Label.PENDING_EMAIL, email)).isEmpty();
      } catch (Status550ServiceDomainClientException e) {
        log.warn("Problem looking up uniqueness of email via label in " + domainName + " for email " + email, e);
      }
      return false;
    }

    return findByDomainNameAndLabelAndLabelValue(domainName, Label.PENDING_EMAIL, email).isEmpty();
  }

  public boolean isUniqueMobile(String domainName, String mobile) {
    return (findByDomainNameAndMobile(domainName, mobile).isEmpty());
  }

  /**
   *
   * @param password
   * @return
   */
  public boolean isPasswordOk(String password) {
    PasswordPolicy policy = new PasswordPolicy(BreachDatabase.haveIBeenPwned(5), 1, 64);
    //Check the password against the password list
    switch(policy.check(password)) {
      case OK:
        return true;

      case BREACHED:
      case TOO_SHORT:
      case TOO_LONG:
      default:
        return false;
    }
  }

  public Page<User> findAllByGroups(PageRequest pageRequest, Group group) {
    return userRepository.findAllByGroups(pageRequest, group);
  }
  public Iterable<User> findAllByGroups(Group group) {
    return userRepository.findAllByGroups(group);
  }
  public List<User> findAllByGroupsContains(Group group) {
    return userRepository.findAllByGroupsContains(group);
  }

  public Page<User> findAll(Specification<User> userSpecifications, PageRequest pageRequest) {
    return userRepository.findAll(userSpecifications, pageRequest);
  }

  public List<User> findAll(Specification<User> userSpecifications) {
    return userRepository.findAll(userSpecifications);
  }

  public User changeDateOfBirth(String authorGuid, String guid, DateTime dateOfBirth, String comments, LithiumTokenUtil util) {

    Integer dobDay = dateOfBirth.getDayOfMonth();
    Integer dobMonth = dateOfBirth.getMonthOfYear();
    Integer dobYear = dateOfBirth.getYear();

    User user = findFromGuid(guid);
    if(DateUtil.isAfterNow(dateOfBirth)){
      throw new Status422InvalidDateOfBirthException(RegistrationError.INVALID_DOB.getResponseMessageLocal(messageSource, user.domainName()));
    }

    String oldDobDay =  DateUtil.getFullStringDate(user.getDobDay(), user.getDobMonth(), user.getDobYear());
    String newDobDay = DateUtil.getFullStringDate(dobDay, dobMonth, dobYear);

    List<ChangeLogFieldChange> changes = addChangeLogFieldChanges(oldDobDay, newDobDay, "dateOfBirth");

    //As per requirements, the date of birth needs to be included in the comments
    StringBuilder additionalComments = additionalComments(oldDobDay, newDobDay, Module.SERVICE_USER.getResponseMessageLocal(messageSource, user.domainName(), "SERVICE_USER.GLOBAL.DATE_OF_BIRTH_EDIT", "The Date Of Birth was changed from "));

    if(comments != null && ! comments.isEmpty()) {
      additionalComments.append(" ")
          .append(comments);
    }

    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), authorGuid, util, additionalComments.toString(), null,
        changes, Category.ACCOUNT, SubCategory.DOB_CHANGE, 0, user.domainName());

    user.setDobDay(dobDay);
    user.setDobMonth(dobMonth);
    user.setDobYear(dobYear);

    user = save(user);

    checkAndUpdateUnderage(LocalDate.parse(dobDay + "-" + dobMonth + "-" + dobYear, DateTimeFormatter.ofPattern("d-M-yyyy")), user);

    try {
      pubSubUserService.buildAndSendPubSubAccountChange(user, util.getAuthentication(), PubSubEventType.ACCOUNT_UPDATE);
    } catch (Exception e) {
      log.warn(Module.SERVICE_USER.getResponseMessageLocal(messageSource, user.domainName(), "SERVICE_USER.GLOBAL.DATE_OF_BIRTH_ERROR", "can't sent pub-sub message for DOB update") + e.getMessage());
    }
    return user;
  }

  private List<ChangeLogFieldChange> addChangeLogFieldChanges(String oldValue,
      String newDobDay,
      String field) {
    List<ChangeLogFieldChange> changes = new ArrayList<>();

    changes.add(
        ChangeLogFieldChange.builder()
            .field(field)
            .fromValue(oldValue)
            .toValue(newDobDay)
            .build()
    );
    return changes;
  }

  private StringBuilder additionalComments(String oldValue,
      String newValue, String comment) {
    StringBuilder additionalComments = new StringBuilder();
    additionalComments.append(comment)
        .append(oldValue)
        .append(" to ")
        .append(newValue)
        .append(".");
    return additionalComments;
  }

  public User changePlaceOfBirth(LithiumTokenUtil util, User user, UserChanges userChanges) throws Exception {
    User newSavedUser;
    try {
      lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(util.getJwtUser().getDomainName());
      Locale locale = LocaleContextHolder.getLocale();

      if(user == null || userChanges == null)
        throw new Status404UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_NOT_FOUND",
            new Object[]{new lithium.service.translate.client.objects.Domain(util.getJwtUser().getDomainName())}, "", locale));

      String oldPlaceOfBirth = user.getPlaceOfBirth();
      user.setPlaceOfBirth(userChanges.getPlaceOfBirth().isEmpty() ? null : userChanges.getPlaceOfBirth());
      newSavedUser = save(user);

      List<ChangeLogFieldChange> placeOfBirthList = addChangeLogFieldChanges(oldPlaceOfBirth, user.getPlaceOfBirth(), "placeOfBirth");
      StringBuilder additionalCommentsBuilder = additionalComments(oldPlaceOfBirth, user.getPlaceOfBirth(), Module.SERVICE_USER.getResponseMessageLocal(messageSource, user.domainName(), "SERVICE_USER.GLOBAL.PLACE_OF_BIRTH_EDIT", "Place of birth was changed from "));

      if(user.getComments() != null && !user.getComments().isEmpty()) {
        additionalCommentsBuilder.append(user.getComments());
      }

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), util.getJwtUser().getGuid(), util,
          additionalCommentsBuilder.toString(), null, placeOfBirthList, Category.ACCOUNT, SubCategory.PLACE_OF_BIRTH_CHANGE, 0, user.domainName());

    } catch(Exception ex) {
      log.error(ex.getMessage(), ex);
      throw new Exception(ex);
    }

    try {
      pubSubUserService.buildAndSendPubSubAccountChange(user, util.getAuthentication(), PubSubEventType.ACCOUNT_UPDATE);
    } catch (Exception e) {
      log.warn(Module.SERVICE_USER.getResponseMessageLocal(messageSource, user.domainName(), "SERVICE_USER.GLOBAL.PLACE_OF_BIRTH_ERROR", "Can't sent pub-sub message for placeOfBirth update.") + e.getMessage());
    }
    return newSavedUser;
  }

  @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
  public User changePassword(String guid, PasswordBasic passwordBasic, LithiumTokenUtil util)
      throws Status404UserNotFoundException, Status456NewPasswordsMismatchException,
      Status423InvalidPasswordException, Status457CurrentAndNewPasswordMatchException,
      Status500InternalServerErrorException, Status500LimitInternalSystemClientException,
      Status491PermanentSelfExclusionException, Status490SoftSelfExclusionException,
      Status496PlayerCoolingOffException {
    User user = findFromGuid(guid);
    Locale locale = Locale.US;  //TODO: This needs to be updated to retrieve from user/domain.
    log.debug("Password Update ("+user.guid()+") - User from token : "+user);
    if (user == null) throw new Status404UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.PASSWORD.NOT_FOUND", new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "", locale));
    if (passwordBasic.confirmNewPasswordsMatch()) {
      limitService.checkPlayerRestrictions(guid, locale.toLanguageTag());
      log.debug("Password Update (" + user.guid() + ") - new passwords match (" + passwordBasic.getNewPassword() + ")");

      String currentHash;
      String localSaltValue = user.getPasswordHash().startsWith("st:") ? passwordSalt : null;
      try {
        currentHash = PasswordHashing.hashPassword(passwordBasic.getCurrentPassword(), localSaltValue);
      } catch (lithium.exceptions.Status500InternalServerErrorException e) {
        log.error("Password Hash Exception " + ExceptionMessageUtil.allMessages(e), e);
        throw new Status500InternalServerErrorException(messageSource.getMessage("ERROR_DICTIONARY.PASSWORD.PASSWORD_HASHING", new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "Password hashing failed.", LocaleContextHolder
            .getLocale()), e.getStackTrace());
      }

      if (!currentHash.equals(user.getPasswordHash())) {
        log.debug("Password Update ("+user.guid()+") - current password mismatch ("+currentHash+" != "+user.getPasswordHash()+")");
        throw new Status423InvalidPasswordException(messageSource.getMessage("ERROR_DICTIONARY.PASSWORD.INVALID_PASSWORD",  new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "Password entered incorrectly.", locale));
      }
      if (passwordBasic.newPasswordSameAsOld()) {
        log.debug("Password Update ("+user.guid()+") - current password same as new");
        throw new Status457CurrentAndNewPasswordMatchException(messageSource.getMessage("ERROR_DICTIONARY.PASSWORD.CURRENT_MATCH_NEW", new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "New password cannot be the same as current password.", locale));
      }
      log.debug("Password Update ("+user.guid()+") - updating password");
      return changePassword(guid, guid, passwordBasic.getNewPassword(), util);
    }
    log.warn("Password Update ("+user.guid()+") - new passwords do not match");
    throw new Status456NewPasswordsMismatchException(messageSource.getMessage("ERROR_DICTIONARY.PASSWORD.PASSWORD_MISMATCH", new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "New passwords do not match.", locale));
  }

  @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User changePassword(String updatedByGuid, String guid, String password, LithiumTokenUtil util) throws Status500InternalServerErrorException {
    try {
      User user = findFromGuid(guid);
      User updatedBy = findFromGuid(updatedByGuid);
      String updatedByFullName = null;

      if(updatedBy != null) {
        updatedByFullName = String.format("%s %s", updatedBy.getFirstName(), updatedBy.getLastName());
      }

      List<ChangeLogFieldChange> changes = new ArrayList<>();
      if (!updatedByGuid.equalsIgnoreCase(user.getPasswordUpdatedBy())) {
        changes.add(
            ChangeLogFieldChange.builder()
                .field("passwordUpdatedBy")
                .fromValue(user.getPasswordUpdatedBy())
                .toValue(updatedByFullName)
                .build()
        );
      }
      changes.add(
          ChangeLogFieldChange.builder()
              .field("excessiveFailedLoginBlock")
              .fromValue(user.getExcessiveFailedLoginBlock() != null ? user.getExcessiveFailedLoginBlock().toString() : null)
              .toValue("false")
              .build()
      );
      user.setExcessiveFailedLoginBlock(false);
      user.setPasswordHash(PasswordHashing.hashPassword(password, passwordSalt));
      user.setPasswordUpdated(new Date());
      user.setPasswordUpdatedBy(updatedByFullName);

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), updatedByGuid, util, null, null,
          changes, Category.ACCOUNT, SubCategory.PASSWORD_RESET, 0, user.guid().substring(0, user.guid().indexOf('/'))); // password test case does not cater for domain yay :')

      user = save(user);

      // UserPasswordHashAlgorithm added for VB migrated users where password hashes used algorithms set by DK.
      // When password is changed, the algorithm defaults back to lithium's default hashing algorithm, so if an entry exists,
      // it needs to be removed. Failure to remove will mean users are unable to authenticate after a password reset.
      userPasswordHashAlgorithmService.delete(user);

      passwordChangeNotificationService.sendSmsAndEmailNotification(user);

      return user;
    } catch (Exception e) {
      throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
    }
  }

  @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
  public User changePassword(String domain, User user, String password, Principal principal) throws Exception {
    LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
    String passwordUpdatedBy = util.domainName()+"/"+util.username();
    List<ChangeLogFieldChange> changes = new ArrayList<>();
    if (!passwordUpdatedBy.equalsIgnoreCase(user.getPasswordUpdatedBy())) {
      changes.add(
          ChangeLogFieldChange.builder()
              .field("passwordUpdatedBy")
              .fromValue(user.getPasswordUpdatedBy())
              .toValue(passwordUpdatedBy)
              .build()
      );
    }
    user.setPasswordHash(PasswordHashing.hashPassword(password, passwordSalt));
    user = save(user);

    // UserPasswordHashAlgorithm added for VB migrated users where password hashes used algorithms set by DK.
    // When password is changed, the algorithm defaults back to lithium's default hashing algorithm, so if an entry exists,
    // it needs to be removed. Failure to remove will mean users are unable to authenticate after a password reset.
    userPasswordHashAlgorithmService.delete(user);

    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
        null, null, changes, Category.ACCOUNT, SubCategory.PASSWORD_RESET, 0, domain);
    return user;
  }

  public List<User> nullGuidTop100() {
    return userRepository.findTop100ByGuidIsNull();
  }

  public List<UserProjection> findByDomainNameAndUsernameOrFirstNameOrLastNameOrEmail(String domainName, String search) {
    return userRepository.findByDomainNameAndUsernameOrFirstNameOrLastNameOrEmail(domainName, search);
  }

  public List<User> findByDomainNameInAndUsernameOrFirstNameOrLastNameOrEmail(List<String> domains, String search) {
    return userRepository.findByDomainNameInAndUsernameOrFirstNameOrLastNameOrEmail(domains, search + "%");
  }

  public UserProjection findByGuid(String domain, String guid) {
    return userRepository.findByDomainNameAndGuid(domain, guid);
  }

  public List<Domain> filterRequestedDomainsForToken(String[] domainNames, LithiumTokenUtil tokenUtil) {
    List<Domain> domains = new ArrayList<>();
    List<Domain> allowedDomainsForUser = new ArrayList<>();

    tokenUtil.domainsWithRole("PLAYER_VIEW").forEach(jwtDomain -> {
      if ((jwtDomain.getPlayerDomain()!=null) && (jwtDomain.getPlayerDomain())) {
        Domain domain = domainService.findOrCreate(jwtDomain.getName());
        if (domain != null) {
          allowedDomainsForUser.add(domain);
        }
      }
    });

    if (domainNames != null) {
      for (String domainName:domainNames) {
        if ((domainName == null) || (domainName.isEmpty())) continue;
        Domain d = domainService.findOrCreate(domainName);
        if (tokenUtil.hasRole(domainName, "PLAYER_VIEW")) {
          if (allowedDomainsForUser.contains(d)) {
            domains.add(d);
          }
        }
      }
    }

    return domains;
  }

  public Page<User> find(
      List<Domain> domains,
      List<UserCategory> userCategories,
      String username,
      String firstName,
      String lastName,
      Date signupDateRangeStart,
      Date signupDateRangeEnd,
      List<String> statuses,
      List<String> statusReasons,
      String id,

      Date dateofbirthStartDate,
      Date dateofbirthEndDate,
      String email,
      String mobilenumber,
      List<Long> verificationstatuses,
      String includeexcludetestaccount,
//		String haspendingwithdrawals,
      String accountmanagementstatus,
      Date lastloginStartDate,
      Date lastloginEndDate,
      List<String> clienttypeList,
//		String assignedaccountmanager,
//		String tags,
      Date lastdepositStartDate,
      Date lastdepositEndDate,
      String isTestAccount,

      String searchValue,
      Pageable pageable
  ) {
    Specification<User> spec = Specification.where(UserSpecifications.domainIn(domains));
    if (!userCategories.isEmpty()) spec = spec.and(UserSpecifications.userCategoriesIn(userCategories));

    // spec.and(UserSpecifications.fetchUserEvents());

    spec = addToSpec(username, spec, UserSpecifications::usernameStartsWith);
    spec = addToSpec(firstName, spec, UserSpecifications::firstNameStartsWith);
    spec = addToSpec(lastName, spec, UserSpecifications::lastNameStartsWith);
    spec = addToSpec(searchValue, spec, UserSpecifications::any);
    spec = addToSpec(signupDateRangeStart, false, spec, UserSpecifications::signupDateRangeStart);
    spec = addToSpec(signupDateRangeEnd, true, spec, UserSpecifications::signupDateRangeEnd);
    spec = addToSpec(statuses, spec, UserSpecifications::statusesIn);
    spec = addToSpec(statusReasons, spec, UserSpecifications::statusReasonsIn);
    spec = addToSpec(id, spec, UserSpecifications::idStartsWith);
    spec = addToSpec(dateofbirthStartDate, dateofbirthEndDate, spec, UserSpecifications::dateOfBirth);
    spec = addToSpec(email, spec, UserSpecifications::email);
    spec = addToSpec(mobilenumber, spec, UserSpecifications::mobilenumber);
    spec = addToSpecLongList(verificationstatuses, spec, UserSpecifications::verificationstatusesIn);
    spec = addToSpec(lastloginStartDate, lastloginEndDate, spec, UserSpecifications::lastlogin);
    spec = addToSpec(lastdepositStartDate, lastdepositEndDate, spec, UserSpecifications::lastdeposit);
    spec = addToSpec(clienttypeList, spec, UserSpecifications::clienttype);
    spec = addToSpec(isTestAccount, spec, UserSpecifications::isTestAccount);

    return userRepository.findAll(spec, pageable);
  }

  private Specification<User> addToSpec(final List<String> aString, Specification<User> spec, Function<List<String>, Specification<User>> predicateMethod) {
    if (aString != null && !aString.isEmpty()) {
      Specification<User> localSpec = Specification.where(predicateMethod.apply(aString));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<User> addToSpecLongList(final List<Long> aLongList, Specification<User> spec, Function<List<Long>, Specification<User>> predicateMethod) {
    if (aLongList != null && !aLongList.isEmpty()) {
      Specification<User> localSpec = Specification.where(predicateMethod.apply(aLongList));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<User> addToSpec(final String aString, Specification<User> spec, Function<String, Specification<User>> predicateMethod) {
    if (aString != null && !aString.isEmpty()) {
      Specification<User> localSpec = Specification.where(predicateMethod.apply(aString));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<User> addToSpec(final Date aDate, Specification<User> spec, Function<Date, Specification<User>> predicateMethod) {
    if (aDate != null) {
      Specification<User> localSpec = Specification.where(predicateMethod.apply(aDate));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<User> addToSpec(final Date aDate, boolean addDay, Specification<User> spec, Function<Date, Specification<User>> predicateMethod) {
    if (aDate != null) {
      DateTime someDate = new DateTime(aDate);
      if (addDay) {
        someDate = someDate.plusDays(1).withTimeAtStartOfDay();
      } else {
        someDate = someDate.withTimeAtStartOfDay();
      }
      Specification<User> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }
    return spec;
  }

  private Specification<User> addToSpec(
      final Date dateRangeStart,
      final Date dateRangeEnd,
      Specification<User> spec,
      BiFunction<Date, Date, Specification<User>> predicateMethod) {

    if (dateRangeStart != null || dateRangeEnd != null) {

      LocalDateTime startDate = null;
      LocalDateTime endDate = null;

      if (dateRangeStart != null) {
        startDate = LocalDateTime.fromDateFields(dateRangeStart);
      }
      if (dateRangeEnd != null) {
        endDate = LocalDateTime.fromDateFields(dateRangeEnd);
      } else {
        endDate = LocalDateTime.now().plusYears(20);
      }

      if (startDate != null && startDate.isEqual(endDate)) {
        endDate = startDate.plusDays(1).withTime(0,0,0,0);
      }

      final Specification<User> localSpec = Specification.where(predicateMethod.apply(
          startDate != null ? startDate.toDate() : null,
          endDate != null ? endDate.toDate() : null
      ));

      spec = (spec == null) ? localSpec : spec.and(localSpec);
      return spec;
    }

    return spec;
  }

  public List<User> findByDomainNameAndFirstNameAndLastNameAndBirthDay
      (String domainName, String firstName, String lastName, int dobDay, int dobMonth, int dobYear) {
    return userRepository.findByDomainNameAndFirstNameAndLastNameAndDobDayAndDobMonthAndDobYear
        (domainName, firstName, lastName, dobDay, dobMonth, dobYear);
  }

  public List<User> findByDomainNameAndFirstNameAndLastNameAndBirthDayAndIdNot(DuplicateCheckRequestData requestData) {
    return userRepository.findByDomainNameAndFirstNameAndLastNameAndDobDayAndDobMonthAndDobYearAndIdNot(
        requestData.getDomainName(),
        requestData.getFirstName(),
        requestData.getLastName(),
        requestData.getDobDay(),
        requestData.getDobMonth(),
        requestData.getDobYear(),
        requestData.getUserOwnerId());
  }

  public List<User> findByDomainNameAndLastNameAndBirthDayAndPostcodeAndIdNot(DuplicateCheckRequestData requestData) {
    return userRepository.findByDomainNameAndLastNameAndDobDayAndDobMonthAndDobYearAndResidentialAddressPostalCodeAndIdNot(
        requestData.getDomainName(),
        requestData.getLastName(),
        requestData.getDobDay(),
        requestData.getDobMonth(),
        requestData.getDobYear(),
        requestData.getPostcode(),
        requestData.getUserOwnerId());
  }

  public List<User> findByDomainNameAndFirstNameAndLastNameAndBirthDayEcosystemAware
      (String domainName, String firstName, String lastName, int dobDay, int dobMonth, int dobYear)
      throws
      Status469InvalidInputException,
      Status550ServiceDomainClientException {
    ArrayList<String> domainNameListInEcosystem = cachingDomainClientService.listDomainNamesInEcosystemByDomainName(domainName.toLowerCase());
    return userRepository.findByDomainNameInAndFirstNameAndLastNameAndDobDayAndDobMonthAndDobYear
        (domainNameListInEcosystem, firstName, lastName, dobDay, dobMonth, dobYear);
  }

  public boolean isUniqueFullNameEcosystemAware(String domainName, String firstName, String lastName, int dobDay, int dobMonth, int dobYear) {
    try {
      return (findByDomainNameAndFirstNameAndLastNameAndBirthDayEcosystemAware
          (domainName, firstName, lastName, dobDay, dobMonth, dobYear).isEmpty());
    } catch (
        Status469InvalidInputException e) {
      log.info("Problem looking up uniqueness of username in " +
          domainName + " for " + domainName + " : " + firstName +" " + e.getMessage());
      log.debug("Problem looking up uniqueness of username in " +
          domainName + " for " + domainName + " : " + firstName +" " + lastName + " " + dobDay + "-" + dobMonth + "-" + dobYear + " "
          + e.getMessage());
      return (findByDomainNameAndFirstNameAndLastNameAndBirthDay(domainName, firstName, lastName, dobDay, dobMonth, dobYear) == null);
    } catch (Status550ServiceDomainClientException e) {
      log.warn("Problem looking up uniqueness of username in " +
          domainName + " for " + domainName + " : " + firstName +" " + lastName + " " + dobDay + "-" + dobMonth + "-" + dobYear + " ", e);
    }
    return false;
  }

  public boolean isFullNameUnique
      (final String domainName, final String firstName, final String lastName, int dobDay, int dobMonth, int dobYear, boolean domainOnly) {
    try {
      if (!domainOnly && cachingDomainClientService.isDomainInAnyEcosystem(domainName)) {
        //Domain is in an ecosystem so we need to check all domains in that ecosystem for uniqueness
        return isUniqueFullNameEcosystemAware
            (domainName, firstName, lastName, dobDay, dobMonth, dobYear);
      }
    } catch (Status550ServiceDomainClientException e) {
      log.warn("Unable to resolve domain existence in ecosystem. domain: " +
          domainName + " : " + firstName +" " + lastName + " " + dobDay + "-" + dobMonth + "-" + dobYear, e);
    }
    List<User> players = findByDomainNameAndFirstNameAndLastNameAndBirthDay(domainName, firstName,
        lastName, dobDay, dobMonth, dobYear);
    return players.isEmpty();
  }

  public boolean isFullNameUnique(final String domainName, final String firstName, final String lastName, int dobDay, int dobMonth, int dobYear) {
    return isFullNameUnique(domainName, firstName, lastName, dobDay, dobMonth, dobYear, true);
  }

  public TermsAndConditionsVersion userTermsAndConditionsVersion(String userGuid)
      throws Status550ServiceDomainClientException {
    User user = userRepository.findByGuid(userGuid);
    String currentDomainVersion = cachingDomainClientService.getCurrentDomainTermsAndConditionsVersion(user.domainName());

    return TermsAndConditionsVersion.builder()
        .acceptedUserVersion(user.getTermsAndConditionsVersion())
        .currentDomainVersion(currentDomainVersion)
        .build();
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = {ObjectOptimisticLockingFailureException.class}, exclude = {
      Exception.class})
  public void optInToCommunications(Boolean optIn, LithiumTokenUtil tokenUtil) {
    PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder builder = PubSubMarketingPreferences.builder();
    User user = userRepository.findByGuid(tokenUtil.guid());
    user.setCommsOptInComplete(true);
    if (optIn != null) {
      Boolean optOut = !optIn;
      user.setCallOptOut(optOut);
      user.setEmailOptOut(optOut);
      user.setLeaderboardOptOut(optOut);
      user.setPushOptOut(optOut);
      user.setSmsOptOut(optOut);
      user.setPostOptOut(optOut);

      try {
        List<ChangeLogFieldChange> changeLogList = new ArrayList<>();
        changeLogList.add(getChangeLogFieldChange(optOut, "emailOptOut",  String.valueOf(!user.getEmailOptOut())));
        changeLogList.add(getChangeLogFieldChange(optOut, "leaderboardOptOut", String.valueOf(!user.getLeaderboardOptOut())));
        changeLogList.add(getChangeLogFieldChange(optOut, "postOptOut", String.valueOf(!user.getPostOptOut())));
        changeLogList.add(getChangeLogFieldChange(optOut, "smsOptOut", String.valueOf(!user.getSmsOptOut())));
        changeLogList.add(getChangeLogFieldChange(optOut, "callOptOut", String.valueOf(!user.getCallOptOut())));
        changeLogList.add(getChangeLogFieldChange(optOut, "pushOptOut", String.valueOf(!user.getPushOptOut())));
        changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), tokenUtil.getJwtUser().guid(), tokenUtil,
            null, null, changeLogList, Category.SUPPORT, SubCategory.EDIT_DETAILS, 0, user.domainName());
      } catch (Exception ex) {
        log.error("Failed to log changelog fields for :  " + user.guid() + " :: " + ex.getMessage(), ex);
      }

      try {
        builder.callOptOut(!optIn);
        builder.emailOptOut(!optIn);
        builder.leaderBoardOptOut(!optIn);
        builder.pushOptOut(!optIn);
        builder.smsOptOut(!optIn);
        builder.postOptOut(!optIn);
        pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.USER, builder , tokenUtil, PubSubEventType.MARKETING_PREFERENCES);
      } catch (Exception ex) {
        log.error("optInToCommunications failed for:  " + user.getGuid() + " :: " + ex.getMessage(), ex);
      }
    }
    user = save(user);
  }

  private ChangeLogFieldChange getChangeLogFieldChange(Boolean optOut, String field, String currentValue) {
    return ChangeLogFieldChange.builder().field(field).fromValue(currentValue).toValue(optOut.toString()).build();
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public TermsAndConditionsVersion acceptTermsAndConditions(String domainName, String userGuid, String authorGuid,
      String comments, Locale locale)
      throws Status550ServiceDomainClientException, Status500InternalServerErrorException {
    log.debug("UserService.acceptTermsAndConditions [domainName="+domainName+", userGuid="+userGuid
        + ", authorGuid="+authorGuid+", comments="+comments+", locale="+locale+"]");

    String currentDomainVersion = cachingDomainClientService.getCurrentDomainTermsAndConditionsVersion(domainName);

    User user = userRepository.findByGuid(userGuid);
    String userOldTcVersion = user.getTermsAndConditionsVersion();
    user.setTermsAndConditionsVersion(currentDomainVersion);
    user = save(user);

    String tcAcceptanceMsg = messageSource.getMessage("SERVICE_USER.ACCOUNT.TC_ACCEPTANCE.MSG",
        new Object[] { currentDomainVersion }, locale);

    if (comments == null) {
      comments = tcAcceptanceMsg;
    } else {
      comments += "\r\n\r\n" + tcAcceptanceMsg;
    }

    try {
      List<ChangeLogFieldChange> clfc = addChangeLogFieldChanges(userOldTcVersion, currentDomainVersion, "termsAndConditionsVersion");

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), authorGuid, null, comments,
          null, clfc, Category.ACCOUNT, SubCategory.TC_ACCEPTANCE, 0, user.domainName());

    } catch (Exception e) {
      String msg = "Note registration for user terms and conditions acceptance failed";
      log.error(msg + " [domainName="+domainName+", userGuid="+userGuid+", comments="+comments+"] "
          + e.getMessage(), e);
      throw new Status500InternalServerErrorException(msg);
    }

    return TermsAndConditionsVersion.builder()
        .currentDomainVersion(currentDomainVersion)
        .acceptedUserVersion(user.getTermsAndConditionsVersion())
        .build();
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public void blockDuplicatedUser(long userId, String comment) {
    User user = userRepository.findOne(userId);
    Status oldValue = user.getStatus();
    StatusReason oldStatusReason = user.getStatusReason();

    Status status = findStatus(lithium.service.user.client.enums.Status.BLOCKED.statusName());
    StatusReason reason = findStatusReason(
        lithium.service.user.client.enums.StatusReason.DUPLICATED_ACCOUNT.statusReasonName());

    user.setUpdatedDate(new Date());
    user.setComments("Account is closed due to suspected duplicated accounts ");
    user.setStatus(status);
    user.setStatusReason(reason);
    save(user);
    try {
      List<ChangeLogFieldChange> clfc = addChangeLogFieldChanges((oldValue == null) ? "" : oldValue.getName(), status.getName(), "status");
      clfc.add(ChangeLogFieldChange.builder()
          .field("statusReason")
          .fromValue((oldStatusReason == null)? "" : oldStatusReason.getName())
          .toValue(reason.getName())
          .build());

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "disable",
          user.getId(), user.guid(), null,
          comment, null, clfc, Category.ACCOUNT,
          SubCategory.CLOSURE, 80, user.domainName());

    } catch (Exception ex) {
      log.error("Unable to perform changelog change for block ununique user: " + user, ex);
    }
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public void blockIBANMismatchUser(String userGuid,
      String accoutNoteMessage,
      String financeNoteMessage
  ) {
    User user = userRepository.findByGuid(userGuid);
    Status oldValue = user.getStatus();
    StatusReason oldStatusReason = user.getStatusReason();

    Status status = findStatus(lithium.service.user.client.enums.Status.BLOCKED.statusName());
    StatusReason reason = findStatusReason(
        lithium.service.user.client.enums.StatusReason.DEPOSIT_MISMATCH.statusReasonName());

    user.setUpdatedDate(new Date());
    user.setComments(accoutNoteMessage);
    user.setStatus(status);
    user.setStatusReason(reason);
    save(user);

    try {
      // note account category
      List<ChangeLogFieldChange> clfc = addChangeLogFieldChanges((oldValue == null) ? "" : oldValue.getName(), status.getName(), "status");
      clfc.add(ChangeLogFieldChange.builder()
          .field("statusReason")
          .fromValue((oldStatusReason == null)? "" : oldStatusReason.getName())
          .toValue(reason.getName())
          .build());

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "disable",
          user.getId(), user.guid(), null,
          accoutNoteMessage, null, clfc, Category.ACCOUNT,
          SubCategory.STATUS_CHANGE, 80, user.domainName());

      // note finance category
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "disable",
          user.getId(), user.guid(), null,
          financeNoteMessage, null, clfc, Category.FINANCE,
          SubCategory.IBAN_MISMATCH, 80, user.domainName());

    } catch (Exception ex) {
      log.error("Unable to perform changelog change for block unverified user: " + user, ex);
    }
  }

  public Status findStatus(String name) {
    return statusRepository.findByName(name.toUpperCase());
  }

  public StatusReason findStatusReason(String name) {
    return statusReasonRepository.findByName(name);
  }

  public User changeUserStatus(UserAccountStatusUpdate statusUpdate) {
    User user = changeUserStatus(statusUpdate, null);

    // Triggering auto-restrictions on user status changes
    autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder().userGuid(user.guid()).build());

    return user;
  }

  /**
   * The initial purpose of this method is for the VB migration. Use with care. This will not do all the necessary
   * things for the normal workflow.
   */
//  @Retryable(backoff = @Backoff(delay = 10, maxDelay = 50), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public void changeUserStatusBasic(UserAccountStatusUpdateBasic statusUpdate) {
    User user = findFromGuid(statusUpdate.getUserGuid());

    Status newStatus = findStatus(statusUpdate.getStatusName());
    StatusReason newStatusReason = findStatusReason(statusUpdate.getStatusReasonName());

    user.setStatus(newStatus);
    user.setStatusReason(newStatusReason);

    if (statusUpdate.isMarkSelfExcluded()) {
      user.setHasSelfExcluded(true);
    }

    if (statusUpdate.isOptOutComms()) {
      user.setEmailOptOut(true);
      user.setPostOptOut(true);
      user.setSmsOptOut(true);
      user.setCallOptOut(true);
      user.setPushOptOut(true);
      user.setLeaderboardOptOut(true);
    }

    user = userRepository.save(user);
  }

  @Transactional
  public User findByUserGuidAlwaysRefresh(String userGuid) {
    User user = findFromGuid(userGuid);
    entityManager.refresh(user);
    return user;
  }


  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User changeUserStatus(UserAccountStatusUpdate statusUpdate, LithiumTokenUtil util) {
    String userGuid = statusUpdate.getUserGuid();
    String statusName = statusUpdate.getStatusName();
    String reasonName = statusUpdate.getStatusReasonName();
    String authorGuid = statusUpdate.getAuthorGuid();
    String comment = statusUpdate.getComment();
    String categoryName = statusUpdate.getNoteCategoryName();
    String subCategoryName = statusUpdate.getNoteSubCategoryName();
    int notePriority = statusUpdate.getNotePriority();
    String details = "[userGuid="+userGuid+", statusName="+statusName+", reasonName="+reasonName+"]";

    log.debug("UserService.changeUserStatus " + details);
    User user = findFromGuid(userGuid);
    Status newStatus = findStatus(statusName);
    StatusReason newStatusReason = findStatusReason(reasonName);

    User oldUser = new User();
    oldUser.setStatus(user.getStatus());
    oldUser.setStatusReason(user.getStatusReason());

    user.setStatus(newStatus);
    user.setStatusReason(newStatusReason);
    user = save(user);

    addToSyncUserAttributesQueue(user);

    try {
      String changeType = (newStatus.getUserEnabled())? "enable": "disable";
      List<ChangeLogFieldChange> clfc = new ArrayList<>();

      ChangeLogFieldChange status = changeLogService.compareById(user.getStatus(), oldUser.getStatus(),"status", "name");
      if(!ObjectUtils.isEmpty(status.getField())) clfc.add(status);

      ChangeLogFieldChange statusReason = null;
      if(!ObjectUtils.isEmpty(user.getStatusReason()) || !ObjectUtils.isEmpty(oldUser.getStatusReason())) {
        statusReason = changeLogService.compareById(user.getStatusReason(), oldUser.getStatusReason(), "statusReason", "name");
      }
      if(!ObjectUtils.isEmpty(statusReason)){
        clfc.add(statusReason);
      }

      //LSPLAT-3679 PLAT-4395
      //Both authorguid and util are null when passed in from backend change resulting in no changelog being written,
      //hence the following author check added
      String author = lithium.service.user.client.objects.User.SYSTEM_FULL_NAME;
      if(authorGuid != null){
        author = authorGuid;
      }
      if(util != null){
        author=util.guid();
      }

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", changeType, user.getId(), author, util, comment,
          null, clfc, Category.fromName(categoryName), SubCategory.fromName(subCategoryName), notePriority, user.domainName());
    } catch (Exception e) {
      log.error("Note registration for user account status change failed " + details + " " + e.getMessage(), e);
    }

    if (newStatus.getUserEnabled()) {
      try {
        userStatusService.sendAccountEnabledEmail(user);
      } catch (Exception e) {
        log.error("Account enabled mail for user account status change failed " + details + " " + e.getMessage(), e);
      }
    } else {
      loginEventService.logout(user.guid(), null); // Logout all active sessions
    }

    if (!newStatus.getUserEnabled()) {
      try {
        lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
        if (domain.getPlayers()) {
          userEventService.registerEvent(
              user.guid(),
              UserEventBasic.builder()
                  .type("PLAYER_STATUS_DISABLED")
                  .data(JsonStringify.objectToString(newStatus))
                  .message("Player status changed to " + newStatus.getName())
                  .build()
          );
        }
      } catch (Exception e) {
        log.error("User event (PLAYER_STATUS_DISABLED) for user account status change failed " + details + " " + e.getMessage(), e);
      }
    }

    try {
      pubSubUserService.publishAccountChange(user, statusUpdate, null);
    } catch (Exception e) {
      log.error("Pub-sub event for user account status change failed " + details + " " + e.getMessage(), e);
    }

    String newUserStatusName = newStatus.getName();
    if (newUserStatusName.equals(lithium.service.user.client.enums.Status.OPEN.statusName())) {
      try {
        Access access = limitInternalSystemService.checkAccess(user.guid());
        if(!access.isCompsAllowed()) {
          promotionRestrictionTriggerStream.trigger(PromotionRestrictionTriggerData.builder().userGuid(userGuid).restrict(true).build());
        }
      }
      catch( Exception e) {
        log.error("Failed to set the restriction for user: " +user.guid() + " "+ e.getMessage(), e );
      }
    }

    return user;
  }

  private SportsBookClient sportsBookClient() throws Exception {
    return lithiumServiceClientFactory.target(SportsBookClient.class);
  }

  public AccountStatusErrorCodeAndMessage getAccountStatusErrorCodeAndMessage(User user) {
    String langTag = "en_US";
    try {
      lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
      langTag = domain.getDefaultLocale();
    } catch (Exception e) {
      // Unlikely. It's fine, just using english as default.
    }
    Locale locale = Locale.forLanguageTag(langTag);

    lithium.service.user.client.enums.Status status = lithium.service.user.client.enums.Status.fromName(user.getStatus().getName());
    lithium.service.user.client.enums.StatusReason statusReason = lithium.service.user.client.enums.StatusReason
        .fromName(user.getStatusReason().getName());

    int errorCode = Status405UserDisabledException.ERROR_CODE;
    String errorMsgKey = "ERROR_DICTIONARY.LOGIN.USER_DISABLED";

    if (status != null) {
      if (status.equals(lithium.service.user.client.enums.Status.FROZEN.statusName())) {
        errorCode = Status447AccountFrozenException.ERROR_CODE;
        errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_FROZEN";
      } else if (status.equals(lithium.service.user.client.enums.Status.BLOCKED.statusName())) {
        errorCode = Status448AccountBlockedException.ERROR_CODE;
        errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_BLOCKED";
      }

      if (statusReason != null) {
        switch (statusReason) {
          case COOLING_OFF:
            errorCode = Status496PlayerCoolingOffException.ERROR_CODE;
            errorMsgKey = "ERROR_DICTIONARY.LOGIN.FLAGGED_AS_COOLING_OFF";
            break;
          case SELF_EXCLUSION:
            if (limitService.isPermanentSelfExcluded(user.getGuid())) {
              errorCode = Status491PermanentSelfExclusionException.ERROR_CODE;
              errorMsgKey = "ERROR_DICTIONARY.LOGIN.PERMANENT_SELF_EXCLUSION";
            } else {
              errorCode = Status450AccountFrozenSelfExcludedException.ERROR_CODE;
              errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_FROZEN_SELF_EXCLUDED";
            }
            break;
          case CRUKS_SELF_EXCLUSION:
            errorCode = Status446AccountFrozenCRUKSSelfExcludedException.ERROR_CODE;
            errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_FROZEN_CRUKS_SELF_EXCLUSION";
            break;
          case GAMSTOP_SELF_EXCLUSION:
            errorCode = Status428AccountFrozenGamstopSelfExcludedException.ERROR_CODE;
            errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_FROZEN_GAMESTOP_SELF_EXCLUDED";
            break;
          case PLAYER_REQUEST:
            errorCode = Status433AccountBlockedPlayerRequestException.ERROR_CODE;
            errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_BLOCKED_PLAYER_REQUEST";
            break;
          case RESPONSIBLE_GAMING:
            errorCode = Status434AccountBlockedResponsibleGamingException.ERROR_CODE;
            errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_BLOCKED_RESPONSIBLE_GAMING";
            break;
          case AML:
            errorCode = Status435AccountBlockedAMLException.ERROR_CODE;
            errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_BLOCKED_AML";
            break;
          case FRAUD:
            errorCode = Status455AccountBlockedFraudException.ERROR_CODE;
            errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_BLOCKED_FRAUD";
            break;
          case DUPLICATED_ACCOUNT:
            errorCode = Status436AccountBlockedDuplicatedAccountException.ERROR_CODE;
            errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_BLOCKED_DUPLICATE_ACCOUNT";
            break;
          case OTHER:
            errorCode = Status437AccountBlockedOtherException.ERROR_CODE;
            errorMsgKey = "ERROR_DICTIONARY.LOGIN.ACCOUNT_BLOCKED_OTHER";
            break;
        }
      }
    }

    return AccountStatusErrorCodeAndMessage.builder()
        .errorCode(errorCode)
        .errorMsg(messageSource.getMessage(errorMsgKey, null, locale))
        .build();
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User updateProtectionOfCustomerFundsVersion(String userGuid) throws Status500InternalServerErrorException {
    User user = userRepository.findByGuid(userGuid);
    String currentDomainVersion = null;
    try {
      currentDomainVersion = cachingDomainClientService.getCurrentDomainProtectionOfCustomerFundsVersion(
          user.domainName());
    } catch (Status550ServiceDomainClientException e) {
      String msg = "Failed to retrieve current domain protection of customer funds version";
      log.error(msg + " [user.guid="+user.guid()+"]");
      throw new Status500InternalServerErrorException(msg);
    }
    String userAcceptedVersion = user.getProtectionOfCustomerFundsVersion();
    if ((userAcceptedVersion == null) ||
        (userAcceptedVersion != null && !userAcceptedVersion.contentEquals(currentDomainVersion))) {
      user.setProtectionOfCustomerFundsVersion(currentDomainVersion);
      user = save(user);
      List<ChangeLogFieldChange> clfc = addChangeLogFieldChanges(userAcceptedVersion, currentDomainVersion, "protectionOfCustomerFundsVersion");
      try {
        changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), lithium.service.user.client.objects.User.SYSTEM_GUID,
            null, null, null, clfc, Category.ACCOUNT, SubCategory.CUSTOMER_FUNDS, 0, user.domainName());

      } catch (Exception e) {
        String msg = "Note registration for user account status change failed";
        log.error(msg + " [user.guid="+user.guid()+"]");
        throw new Status500InternalServerErrorException(msg);
      }
    }
    return user;
  }

  @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 10, maxDelay = 50, random = true))
  @TimeThisMethod
  public User createUserStub(String domainName, String userName) throws Exception {
    SW.start("retrieveDomainFromDomainService");
    lithium.service.domain.client.objects.Domain externalDomain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    SW.stop();
    if (externalDomain == null) {
      throw new Exception("No such domain");
    }
    if (!externalDomain.getEnabled()) {
      throw new Exception("Domain disabled");
    }
    if (externalDomain.getDeleted()) {
      throw new Exception("Domain does not exist");
    }

    SW.start("user.find");
    User existingUser = userRepository.findByDomainNameAndUsername(domainName.toLowerCase(), userName.toLowerCase());
    SW.stop();

    if (existingUser == null) {
      SW.start("domain.find");
      Domain domain = domainService.findOrCreate(domainName);
      SW.stop();

      SW.start("uniqueness");
      if (!this.isUniqueUsername(domainName, userName)) {
        throw new Exception("The username is not unique");
      }
      SW.stop();

      SW.start("status.find");
      Status status = statusRepository.findByName("OPEN");
      SW.stop();
      SW.start("user.save");
      User userToSave = User.builder()
          .domain(domain)
          .username(userName)
          .passwordHash(PasswordHashing.hashPassword(UUID.randomUUID()
              .toString(), passwordSalt))
          .createdDate(new Date())
          .updatedDate(new Date())
          .status(status)
          .build();
      User savedUser = this.save(userToSave);
      SW.stop();

      SW.start("changelog");
      List<ChangeLogFieldChange> changeLogFieldChanges = changeLogService.copy(savedUser, new User(),
          new String[]{"domain", "username", "createdDate", "updatedDate"});
      changeLogService.registerChangesWithDomain("user", "create", savedUser.getId(), savedUser.getUsername(), "", null, changeLogFieldChanges,
          Category.ACCOUNT, SubCategory.ACCOUNT, 80,
          savedUser.domainName());
      SW.stop();

      return savedUser;
    } else {
      return existingUser;
    }

  }

  public User findById(Long userId) {
    return userRepository.findOne(userId);
  }

  @Transactional
  public User buildUser(PlayerBasic pb, Domain domain, Address residentialAddress, Address postalAddress, Status status, StatusReason statusReason, String passwordHash)
      throws Status426InvalidParameterProvidedException, IllegalAccessException {
    if (residentialAddress != null ) {
      residentialAddress = addressRepository.findOne(residentialAddress.getId());
    }
    if (postalAddress != null ) {
      postalAddress = addressRepository.findOne(postalAddress.getId());
    }
    long verficationStatusId = VerificationStatus.UNVERIFIED.getId();
    if(pb.isUnderAged()) {
      verficationStatusId = VerificationStatus.UNDERAGED.getId();
    }

    User user = User.builder()
        .domain(domain)
        .username(pb.getUsername() != null ? pb.getUsername() : System.nanoTime()+"")
        .passwordHash(passwordHash)
        .email(pb.getEmail())
        .emailValidated(false)
        .firstName(pb.getFirstName())
        .lastName(pb.getLastName())
        .lastNamePrefix(pb.getLastNamePrefix())
        .countryCode(pb.getCountryCode())
        .placeOfBirth(pb.getPlaceOfBirth())
        .telephoneNumber(pb.getTelephoneNumber())
        .cellphoneNumber(pb.getCellphoneNumber())
        .residentialAddress(residentialAddress)
        .postalAddress(postalAddress)
        .comments(pb.getComments())
        .verificationStatus(verficationStatusId)
        .status(status)
        .statusReason(statusReason)
        .bonusCode(pb.getBonusCode())
        .dobYear(pb.getDobYear())
        .dobMonth(pb.getDobMonth())
        .dobDay(pb.getDobDay())
        .deleted(false)
        .createdDate(new Date())
        .updatedDate(new Date())
        .referrerGuid(pb.getReferrerGuid())
        .timezone(pb.getTimezone())
        .gender(pb.getGender())
        .callOptOut(pb.isCallOptOut())
        .smsOptOut(pb.isSmsOptOut())
        .emailOptOut(pb.isEmailOptOut())
        .postOptOut(pb.isPostOptOut())
        .leaderboardOptOut(pb.isLeaderboardOptOut())
        .promotionsOptOut(pb.isPromotionsOptOut())
        .pushOptOut(pb.isPushOptOut())
        .build();
    validateUser(user);
    user = save(user);
    return user;
  }

  public User addOrUpdateDomainSpecificUserLabelValues(User user, Map<String, String> additionalData) {
    return additionalData.size() > 0 ? userLabelValueService.updateOrAddUserLabelValues(user.getId(), additionalData) : user;
  }

  public void validateUser(User user) throws Status426InvalidParameterProvidedException {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<User>> violations = validator.validate(user);
    if (!violations.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (ConstraintViolation<User> violation : violations) {
        sb.append(" , " + violation.getMessage());
      }
    }
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User saveEmailWelcomeStatus(Long userId, boolean welcomeEmailSent) {
    User user = userRepository.findOne(userId);
    user.setWelcomeEmailSent(welcomeEmailSent);
    return save(user);
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User saveEmailValidated(Long userId, boolean emailValidated, String validatedEmail, String requestEmail, boolean pendingEmailValidationActivate) {
    User user = userRepository.findOne(userId);
    user.setEmailValidated(emailValidated);
    //LSPLAT-5389_During the initial validation there is no email that is being deleted
    user.setDeletedEmail(user.getEmail().equals(validatedEmail) ? "" : user.getEmail());
    user.setEmail(validatedEmail);
    if (pendingEmailValidationActivate) {
      removePendingEmail(user, requestEmail);
    }
    return save(user);
  }

  private User removePendingEmail(User user, String email) {
    if (!ObjectUtils.isEmpty(user.getCurrent())) {
      List<UserRevisionLabelValue> lvList = user.getCurrent().getLabelValueList();
      if (!ObjectUtils.isEmpty(lvList)) {
        for (UserRevisionLabelValue urlv : lvList) {
          if (urlv.getLabelValue().getLabel().getName().equals(Label.PENDING_EMAIL) && Objects.equals(email, urlv.getLabelValue().getValue())) {
            user = addOrUpdateDomainSpecificUserLabelValues(user, Collections.singletonMap(Label.PENDING_EMAIL, ""));
          }
        }
      }
    }
    return user;
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User saveCellphoneValidated(Long userId, boolean cellphoneValidated) {
    User user = userRepository.findOne(userId);
    user.setCellphoneValidated(cellphoneValidated);
    return save(user);
  }

  public User setTest(User user, boolean isTestAccount, Principal principal) throws Exception {
    if (user.getTestAccount() == null || isTestAccount != user.getTestAccount()) {
      user.setTestAccount(isTestAccount);
      addToSyncUserAttributesQueue(user);

      ChangeLogFieldChange c = ChangeLogFieldChange.builder()
          .field("testAccount")
          .fromValue(String.valueOf(!isTestAccount))
          .toValue(String.valueOf(isTestAccount))
          .build();
      List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
      clfc.add(c);

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
          null, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 1, user.domainName());

      return save(user);
    } else {
      return user;
    }
  }

  public void addToSyncUserAttributesQueue(User user) {
    try {
      userAttributesTriggerStream.trigger(UserAttributesData.builder()
          .guid(user.guid())
          .testAccount(user.getTestAccount())
          .build());
    } catch (Exception ex) {
      log.error("Add TestAccount sign to user stream failed: " + ex.getMessage(), ex);
    }
  }

  public User deriveRootEcosystemUser(Long userId)
      throws Status550ServiceDomainClientException, Status469InvalidInputException {
    User user = findOne(userId);
    User rootUser = null;

    if (cachingDomainClientService.isDomainInAnyEcosystem(user.domainName())) {
      //Check if the domain is in a mutually exclusive ecosystem and also a betting domain
      if (cachingDomainClientService.isDomainNameOfEcosystemMutuallyExclusiveType(user.domainName())) {

        Optional<String> rootDomain = cachingDomainClientService.findEcosystemRootByDomainName(user.domainName());
        if (rootDomain.isPresent()) {

          String rootDomainName = rootDomain.get();
          Optional<User> parentUser = findByDomainNameAndEmail(rootDomainName, user.getEmail()).stream().findFirst();

          return parentUser.orElse(rootUser);
        }
      }
      rootUser = user;
    }

    return rootUser;
  }

  /**
   * If the user from token is a root ecosystem domain, then we do not need to do anything
   * else if the user token is not a root ecosystem domain, then we need to return a root user player basic which is later used
   * to update the root users profiles marketing preferences as if this endpoint was being called with the root users token
   * @param frontendPlayerBasic
   * @param user
   * @return
   */
  public PlayerBasic mapParentPromotionOptOuts(FrontendPlayerBasic frontendPlayerBasic, User user) {
    PlayerBasic rootPlayerBasic = new PlayerBasic();
    lithium.service.user.client.objects.User rootUser = userProfileService.getLinkedEcosystemUserGuid(user.guid(), EcosystemRelationshipTypes.ECOSYSTEM_ROOT);

    if (!ObjectUtils.isEmpty(rootUser)) {
      if (!ObjectUtils.isEmpty(frontendPlayerBasic.getParentEmailOptOut())) {
        rootPlayerBasic.setEmailOptOut(frontendPlayerBasic.getParentEmailOptOut());
      } else {
        rootPlayerBasic.setEmailOptOut(rootUser.getEmailOptOut());
      }

      if (!ObjectUtils.isEmpty(frontendPlayerBasic.getParentCallOptOut())) {
        rootPlayerBasic.setCallOptOut(frontendPlayerBasic.getParentCallOptOut());
      } else {
        rootPlayerBasic.setCallOptOut(rootUser.getCallOptOut());
      }

      if (!ObjectUtils.isEmpty(frontendPlayerBasic.getParentPostOptOut())) {
        rootPlayerBasic.setPostOptOut(frontendPlayerBasic.getParentPostOptOut());
      } else {
        rootPlayerBasic.setPostOptOut(rootUser.getPostOptOut());
      }

      if (!ObjectUtils.isEmpty(frontendPlayerBasic.getParentSmsOptOut())) {
        rootPlayerBasic.setSmsOptOut(frontendPlayerBasic.getParentSmsOptOut());
      } else {
        rootPlayerBasic.setSmsOptOut(rootUser.getSmsOptOut());
      }

      if (!ObjectUtils.isEmpty(frontendPlayerBasic.getParentPushOptOut())) {
        rootPlayerBasic.setPushOptOut(frontendPlayerBasic.getParentPushOptOut());
      } else {
        rootPlayerBasic.setPushOptOut(rootUser.getPushOptOut());
      }

      if (!ObjectUtils.isEmpty(frontendPlayerBasic.getParentLeaderboardOptOut())) {
        rootPlayerBasic.setLeaderboardOptOut(frontendPlayerBasic.getParentLeaderboardOptOut());
      } else {
        rootPlayerBasic.setLeaderboardOptOut(rootUser.getLeaderboardOptOut());
      }
    }
    return rootPlayerBasic;
  }

  public PlayerBasic declarePlayerBasicAndOverrideNonNullableFieldsWithCurrentData(FrontendPlayerBasic frontendPlayerBasic, User user) {
    PlayerBasic userUpdate = new PlayerBasic();
    if (ObjectUtils.isEmpty(frontendPlayerBasic.getEmailOptOut())) {
      userUpdate.setEmailOptOut(user.getEmailOptOut());
    }
    if (ObjectUtils.isEmpty(frontendPlayerBasic.getSmsOptOut())) {
      userUpdate.setSmsOptOut(user.getSmsOptOut());
    }
    if (ObjectUtils.isEmpty(frontendPlayerBasic.getPostOptOut())) {
      userUpdate.setPostOptOut(user.getPostOptOut());
    }
    if (ObjectUtils.isEmpty(frontendPlayerBasic.getCallOptOut())) {
      userUpdate.setCallOptOut(user.getCallOptOut());
    }
    if (ObjectUtils.isEmpty(frontendPlayerBasic.getPushOptOut())) {
      userUpdate.setPushOptOut(user.getPushOptOut());
    }
    if (ObjectUtils.isEmpty(frontendPlayerBasic.getLeaderboardOptOut())) {
      userUpdate.setLeaderboardOptOut(user.getLeaderboardOptOut());
    }

    return userUpdate;
  }

  public User setPromotionsOptOut(Long userId, boolean optOut, Principal principal) {
    User user = null;
    try {
      user = findById(userId);
      boolean fromValue = user.getPromotionsOptOut();
      LithiumTokenUtil tokenUtil = tokenService.getUtil(principal);

      user.setPromotionsOptOut(optOut);
      save(user); //No need to sync after promotionOptOut change

      ChangeLogFieldChange c = ChangeLogFieldChange.builder()
          .field("promotionsOptOut")
          .fromValue(String.valueOf(fromValue))
          .toValue(String.valueOf(optOut))
          .build();
      List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
      clfc.add(c);

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenUtil,
          null, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 1, user.domainName());

      PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder builder = PubSubMarketingPreferences.builder();
      builder.emailOptOut(user.getEmailOptOut());
      builder.postOptOut(user.getPostOptOut());
      builder.smsOptOut(user.getSmsOptOut());
      builder.callOptOut(user.getCallOptOut());
      builder.pushOptOut(user.getPushOptOut());
      builder.leaderBoardOptOut(user.getLeaderboardOptOut());
      builder.promotionsOptOut(optOut);

      pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.USER, builder, tokenUtil, PubSubEventType.MARKETING_PREFERENCES);

    }
    catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return user;
  }

  public Page<String> getUserGuidsWithBirthdayOn(List<String> guids, PageRequest pageRequest, DateTime dateTime) {
    Specification<User> specs = Specification.where(UserSpecifications.withBirthdayOn(dateTime))
        .and(UserSpecifications.guidIn(guids));

    Page<String> results =  userRepository.findAll(specs, pageRequest)
        .map(User::getGuid);
    return  results;
  }
//  /**
//   * User this if you have a method that reads a user, then have calls to external services that can possibly update the user.
//   * Reading the user without the context refresh will return an invalid user version and will cause execution failures.
//   * @param userId
//   * @return
//   */

  @CacheEvict(cacheNames = {"lithium.service.user.client.service.user-full-name-by-guid"}, key = "#root.args[0].guid()")
  public Response<User> updateUser(User user, PlayerBasic userUpdate, BindingResult bindingResult, Principal principal) throws Exception {
    if(!ObjectUtils.isEmpty(userUpdate.getUsername()) && !userUpdate.getUsername().equals(user.getUsername())){
      //LSPLAT-5562 Only when we attempt to update/edit the username do we check if they have the role to do the update
      checkUserPermission(user.domainName(), principal, "PLAYER_USERNAME_EDIT");

      if (LithiumTokenUtil.getUserGuidStrategy() == UserGuidStrategy.USERNAME) {
        throw new lithium.exceptions.Status500InternalServerErrorException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.PROFILE_UPDATE_INVALID_GUID_STRATEGY",
            new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName()), UserGuidStrategy.USERNAME.name()},
            LocaleContextHolder.getLocale()));
      }
    }
    boolean pendingEmailValidationActivate = Boolean.parseBoolean(domainClientService.getDomainSetting(user.domainName(), DomainSettings.PENDING_EMAIL_VALIDATION_ACTIVATE));
    if (!cachingDomainClientService.isDomainNameOfEcosystemRootType(user.domainName())) {
      //LSPLAT-5743 Checks are not neccessary for the root domain player
      validateNames(user, userUpdate);
      LocalDate dob = validateAndFormatDob(user, userUpdate);
      checkAndUpdateUnderage(dob, user);
    }

    User oldUser = new User();
    BeanUtils.copyProperties(user, oldUser);
    userUpdate.setId(user.getId());

    String[] fieldsToIgnore = excludePropertyNames(getNullPropertyNames(userUpdate));
    List<String> fieldsToIgnoreList = Stream.of(fieldsToIgnore).collect(Collectors.toList());

    //channelsOptOut does not need to be persisted, adding it here for now to fix (LSPLAT-3441).
    fieldsToIgnoreList.add("channelsOptOut");
    fieldsToIgnoreList.add("promotionsOptOut"); //The promotionsOptOut can only be updated via /profile/opt/{method}/{optOut} for now (Fix for LSPLAT-5556)
    if (pendingEmailValidationActivate) {
      fieldsToIgnoreList.add("email");
    }
    BeanUtils.copyProperties(userUpdate, user, fieldsToIgnoreList.toArray(new String[0]));

    //LSPLAT-3349_PLAT-4073_Unable_to_update_the_players_personal_information
    if (!ObjectUtils.isEmpty(userUpdate.getEmail()) && !userUpdate.getEmail().equalsIgnoreCase(oldUser.getEmail())) {
      //LSPLAT-2925 / PLAT-3660 Confirm email is unique in ecosystem
      //Checkif in ecosystem(
      if (domainClientService.isDomainInAnyEcosystem(user.getDomain().getName())) {
        if (!isUniqueEmail(user.domainName(), userUpdate.getEmail(), false)) {
          throw new Status431UserExistsInEcosystemException(
              messageSource.getMessage("UI_NETWORK_ADMIN.DOMAIN.EMAIL.EXISTS_IN_ECOSYSTEM", null, LocaleContextHolder.getLocale()));
        }
      } else {
        //LSPLAT-2925 / PLAT-3660 Confirm email is unique in domain, or that duplicate emails are allowed within the domain
        if (!isUniqueEmail(user.domainName(), userUpdate.getEmail(), true)
            && !Boolean.parseBoolean(domainClientService.getDomainSetting(user.getDomain().getName(), DomainSettings.ALLOW_DUPLICATE_EMAIL))) {
          throw new Status453EmailNotUniqueException(
              messageSource.getMessage("UI_NETWORK_ADMIN.DOMAIN.EMAIL.EXISTS_IN_DOMAIN", null, LocaleContextHolder.getLocale()));
        }
      }
    }

    boolean emailAddressChanged = false;
    if (userUpdate.getEmail() != null) {
      if (!isValidIEmailAddress(userUpdate.getEmail())) {
        log.warn("User profile update failed, the email(" + userUpdate.getEmail() + ") is incorrect. Received UserUpdate is:" + userUpdate);
        throw new Status420InvalidEmailException(RegistrationError.INVALID_EMAIL.getResponseMessageLocal(messageSource, user.domainName()));
      }
      emailAddressChanged = (!userUpdate.getEmail().equalsIgnoreCase(oldUser.getEmail()));
      if (emailAddressChanged && !isUniqueEmail(user.domainName(), userUpdate.getEmail(), !cachingDomainClientService.isDomainInAnyEcosystem(user.domainName())) && !Boolean.parseBoolean(
          domainClientService.getDomainSetting(user.domainName(), DomainSettings.ALLOW_DUPLICATE_EMAIL))) {
        throw new Status453EmailNotUniqueException(
            RegistrationError.EMAIL_NOT_UNIQUE.getResponseMessageLocal(messageSource, user.domainName()));
      }
    }

    if (!pendingEmailValidationActivate) {
      userUpdate.setEmailValidated(!emailAddressChanged && user.isEmailValidated());
      user.setEmailValidated(!emailAddressChanged && user.isEmailValidated());
    } else {
      if (!userUpdate.getEmail().equalsIgnoreCase(oldUser.getEmail())) {
        addOrUpdateDomainSpecificUserLabelValues(user, ImmutableMap.of(Label.PENDING_EMAIL, userUpdate.getEmail()));
      }
    }

    boolean cellphoneChanged = false;
    if (userUpdate.getCellphoneNumber() != null) {
      try {
        Long.parseLong(userUpdate.getCellphoneNumber());
      } catch (NumberFormatException e) {
        log.warn("User profile update failed, cellphone number(" + userUpdate.getCellphoneNumber() + ") may only contain numbers. Received UserUpdate is:" + user
            .toString());
        throw new Status421InvalidCellphoneException(RegistrationError.INVALID_CELLPHONE.getResponseMessageLocal(messageSource, user.getDomain().getName()), e.getStackTrace());
      }

      cellphoneChanged = (!userUpdate.getCellphoneNumber().equalsIgnoreCase(oldUser.getCellphoneNumber()));
      if (cellphoneChanged) {
        boolean allowNotUniqueCellPhoneNumber = false;
        Optional<String> cellPhoneUniqueSetting = domainClientService.retrieveDomainFromDomainService(user.getDomain().getName())
            .findDomainSettingByName(DomainSettings.ALLOW_DUPLICATE_CELLNUMBER.key());
        if (cellPhoneUniqueSetting.isPresent() && cellPhoneUniqueSetting.get().equalsIgnoreCase("true")) {
          allowNotUniqueCellPhoneNumber = true;
        }
        if (!allowNotUniqueCellPhoneNumber) {
          if (!isUniqueMobile(user.getDomain().getName(), userUpdate.getCellphoneNumber())) {
            throw new Status454CellphoneNotUniqueException(RegistrationError.CELLPHONE_NOT_UNIQUE.getResponseMessageLocal(messageSource, user.getDomain().getName()));
          }
        }
      }
    }

    userUpdate.setCellphoneValidated(cellphoneChanged ? false : user.isCellphoneValidated());
    user.setCellphoneValidated(cellphoneChanged ? false : user.isCellphoneValidated());

    user.setUpdatedDate(new Date());
    user = save(user);

    // Finished saving user data. Call service-access if domain config contains an access rule for update of user details.
    // We don't care about the outcome at this point.
    try {
      accessRuleService.userDetailsUpdateAccessRule(user);
    } catch (Exception e) {
      log.error("Unable to call access provider on user details update for " + user.guid() + ". " + e.getMessage(), e);
    }

    //LSPLAT-3441 fix
    String[] fieldsToCopy = Stream.of(excludePropertyNames(getNotNullPropertyNames(userUpdate)))
        .filter(f -> !f.equalsIgnoreCase("channelsOptOut"))
        .toArray(String[]::new);

    List<ChangeLogFieldChange> clfc = changeLogService.compare(
        user, oldUser, fieldsToCopy
    );

    if(clfc.size() > 0 || pendingEmailValidationActivate) {

      if(emailAddressChanged){
        emailValidationService.sendEmailValidationTokenEmail(user.getDomain().getName(),
            userUpdate.getEmail(), true, false, false);
      }

      //Performing this here requires the calls to the subsequent validation methods to not need to also do it
      preValidationNotificationService.sendEmailOrCellphoneChangeNotification(oldUser.getEmail(), oldUser.getCellphoneNumber(), user);

      //Check for user Full name changes
      if (isUserFullNameChanged(clfc)) {
        userSynchronizeStream.announceUserChanges(convertUser(user));
      }

      //New change log register to handle the addition of a category and sub-category
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(),
          tokenService.getUtil(principal),
          userUpdate.getComments(), null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());

      try {
        pubSubUserService.buildAndSendPubSubAccountChange(user, principal, PubSubEventType.ACCOUNT_UPDATE);
      } catch (Exception e) {
        log.warn("can't sent pub-sub message" + e.getMessage());
      }
    }
    return Response.<User>builder().data(user).build();
  }

  private LocalDate validateAndFormatDob(User user, PlayerBasic userUpdate) throws Status426InvalidParameterProvidedException, Status422InvalidDateOfBirthException {
    if (userUpdate.getDobYear() == null && user.getDobYear() != null) {
      userUpdate.setDobYear(user.getDobYear());
    }

    if ((userUpdate.getDobYear() == null && user.getDobYear() == null) || userUpdate.getDobYear() < 1900 || userUpdate.getDobYear() > 9999) {
      throw new Status422InvalidDateOfBirthException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_DOB_YEAR",
          new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "Invalid dob year.",
          LocaleContextHolder.getLocale()));
    }

    if (userUpdate.getDobMonth() == null && user.getDobMonth() != null) {
      userUpdate.setDobMonth(user.getDobMonth());
    }

    if ((userUpdate.getDobMonth() == null && user.getDobMonth() == null) || userUpdate.getDobMonth() < 1 || userUpdate.getDobMonth() > 12) {
      throw new Status422InvalidDateOfBirthException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_DOB_MONTH",
          new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "Invalid dob month.",
          LocaleContextHolder.getLocale()));
    }

    if (userUpdate.getDobDay() == null && user.getDobDay() != null) {
      userUpdate.setDobDay(user.getDobDay());
    }

    if ((userUpdate.getDobDay() == null && user.getDobDay() == null) || userUpdate.getDobDay() < 1 || userUpdate.getDobDay() > 31) {
      throw new Status422InvalidDateOfBirthException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_DOB_DAY",
          new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "Invalid dob day.",
          LocaleContextHolder.getLocale()));
    }

    String dateOfBirth = userUpdate.getDobDay() + "-" + userUpdate.getDobMonth() + "-" + userUpdate.getDobYear();
    if (!isDateValid(dateOfBirth)) {
      throw new Status422InvalidDateOfBirthException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_DOB_DAY",
          new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())}, "Invalid dob day.",
          LocaleContextHolder.getLocale()));
    }

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");
    LocalDate formattedDateOfBirth = LocalDate.parse(dateOfBirth, dateTimeFormatter);
    if(formattedDateOfBirth.isAfter(LocalDate.now())) {
      throw new Status422InvalidDateOfBirthException(RegistrationError.INVALID_DOB.getResponseMessageLocal(messageSource, user.domainName()));
    }
    return formattedDateOfBirth;
  }

  public void validateNames(User user, PlayerBasic userUpdate) throws Status426InvalidParameterProvidedException {
    if(userUpdate.getFirstName() != null) {
      if (!userUpdate.getFirstName().trim().isEmpty()) {
        if(!validateNamesSymbols(userUpdate.getFirstName().trim())) {
          throw new Status426InvalidParameterProvidedException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_FIRST_NAME", new Object[] {new lithium.service.translate.client.objects.Domain(
              user.domainName())}, "Invalid first name.", LocaleContextHolder.getLocale()));
        }
      } else {
        if (!user.getFirstName().isEmpty()) {
          userUpdate.setFirstName(user.getFirstName());
        } else {
          throw new Status426InvalidParameterProvidedException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_FIRST_NAME", new Object[] {new lithium.service.translate.client.objects.Domain(
              user.domainName())}, "Invalid first name.", LocaleContextHolder.getLocale()));
        }
      }
    } else {
      if (!user.getFirstName().isEmpty()) {
        userUpdate.setFirstName(user.getFirstName());
      } else {
        throw new Status426InvalidParameterProvidedException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_FIRST_NAME", new Object[] {new lithium.service.translate.client.objects.Domain(
            user.domainName())}, "Invalid first name.", LocaleContextHolder.getLocale()));
      }
    }

    if(userUpdate.getLastName() != null) {
      if (!userUpdate.getLastName().trim().isEmpty()) {
        if(!validateNamesSymbols(userUpdate.getLastName().trim())) {
          throw new Status426InvalidParameterProvidedException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_LAST_NAME", new Object[] {new lithium.service.translate.client.objects.Domain(
              user.domainName())}, "Invalid last name.", LocaleContextHolder.getLocale()));
        }
      } else {
        if (!user.getLastName().isEmpty()) {
          userUpdate.setLastName(user.getLastName());
        } else {
          throw new Status426InvalidParameterProvidedException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_LAST_NAME", new Object[] {new lithium.service.translate.client.objects.Domain(
              user.domainName())}, "Invalid last name.", LocaleContextHolder.getLocale()));
        }
      }
    } else {
      if (!user.getLastName().isEmpty()) {
        userUpdate.setLastName(user.getLastName());
      } else {
        throw new Status426InvalidParameterProvidedException(messageSource.getMessage("ERROR_DICTIONARY.REGISTRATION.INVALID_LAST_NAME", new Object[] {new lithium.service.translate.client.objects.Domain(
            user.domainName())}, "Invalid last name.", LocaleContextHolder.getLocale()));
      }
    }
  }

  private void checkAndUpdateUnderage(LocalDate dob, User user) throws Status550ServiceDomainClientException {
    lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
    String minUserAgeSetting = domain.findDomainSettingByName(DomainSettings.MIN_USER_AGE.key()).orElse(DomainSettings.MIN_USER_AGE.defaultValue());
    int minAge = Integer.parseInt(minUserAgeSetting);
    int userAgeYears = Period.between(dob, LocalDate.now()).getYears();
    if (minAge > userAgeYears) {
      self.saveStatus(user.getId(),lithium.service.user.client.enums.Status.BLOCKED.statusName(),lithium.service.user.client.enums.StatusReason.OTHER.statusReasonName());
      self.saveVerificationStatus(user.getId(), VerificationStatus.UNDERAGED.getId());

      // Triggering auto-restrictions on user status changes
      autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder().userGuid(user.guid()).build());
    }
  }

  private boolean isDateValid(String date) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    sdf.setLenient(false);
    return sdf.parse(date, new ParsePosition(0)) != null;
  }

  private boolean validateNamesSymbols(String text) {
    if (text == null) {
      return false;
    }
    int length = text.length();
    if (length < UserValidatorProperties.MIN_NAME_LENGTH || UserValidatorProperties.MAX_NAME_LENGTH < length) {
      return false;
    }
    Pattern pattern = Pattern.compile(UserValidatorProperties.CHECK_NAME_PATTERN);
    Matcher matcher = pattern.matcher(text);
    if (!matcher.find()) {
      return false;
    }
    {
      return true;
    }
  }

  public String[] excludePropertyNames(String[] notNullPropertyNames) {
    List<String> userData = Arrays.stream(notNullPropertyNames)
        .filter(s -> !s.equals("additionalData")
            && !s.equals("underAged"))
        .filter(s -> !s.equals("domainName"))
        .collect(Collectors.toList());
    String[] filtered = new String[userData.size()];
    return userData.toArray(filtered);
  }

  public String[] getNullPropertyNames(Object source) {
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

  public String[] getNotNullPropertyNames(Object source) {
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

  public boolean isValidIEmailAddress(String email) {
    Matcher matcher = UserValidatorProperties.EMAIL_PATTERN.matcher(email);
    return matcher.matches();
  }
//  public User findByIdAndRefresh(Long userId){
//    User userEntity = userRepository.findById(userId);
//    //entityManager.refresh(userEntity);
//    return userEntity;
//  }

  public User updateFailedLoginBlock(User user,  LithiumTokenUtil util, Boolean blockStatus) throws Status500InternalServerErrorException {
    try {
      User updatedBy = findFromGuid(util.guid());
      List<ChangeLogFieldChange> changes = new ArrayList<>();

      changes.add(
          ChangeLogFieldChange.builder()
              .field("excessiveFailedLoginBlock")
              .fromValue(user.getExcessiveFailedLoginBlock() != null ? user.getExcessiveFailedLoginBlock().toString() : null)
              .toValue(String.valueOf(blockStatus))
              .build()
      );
      user.setExcessiveFailedLoginBlock(blockStatus);
      user = save(user);

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "excessiveLoginBlock", user.getId(), updatedBy.guid(), util, null, null,
          changes, Category.ACCOUNT, SubCategory.FAILED_LOGINS, 0,
          user.guid().substring(0, user.guid().indexOf('/')));

      return user;
    } catch (Exception e) {
      throw new Status500InternalServerErrorException(ExceptionMessageUtil.allMessages(e));
    }
  }

  public lithium.service.user.client.objects.User convertUser(User user) {
    mapper.getConfiguration().setPropertyCondition(context -> {
      //if the object is a PersistentCollection could be not initialized
      //in case of lazy strategy, in this case the object will not be mapped:
      return (!(context.getSource() instanceof PersistentCollection)
          || ((PersistentCollection)context.getSource()).wasInitialized());
    });
    lithium.service.user.client.objects.User resultUser =
        mapper.map(user, lithium.service.user.client.objects.User.class);

    if (user.getCurrent() != null) {
      List<UserRevisionLabelValue> lvList = user.getCurrent().getLabelValueList();
      if (lvList != null) {
        Map<String, String> lvMap = new HashMap<>();
        for (UserRevisionLabelValue lv : lvList) {
          lvMap.put(lv.getLabelValue().getLabel().getName(), lv.getLabelValue().getValue());
        }
        resultUser.setLabelAndValue(lvMap);
      }
    }

    return resultUser;
  }

  @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED)
  public User findForUpdate(Long userId) {
    // StaleObjectStateException when we try to pessimistically lock user.
    // This behaviour has been observed in accounting some time ago: https://gitlab.com/playsafe/lithium/app-lithium-full/-/blob/develop/service-accounting/service-accounting-provider-internal/src/main/java/lithium/service/accounting/provider/internal/services/TransactionServiceWrapper.java#L944
    // This exception caused lithium.service.user.data.entities.UserActiveSessionsMetadata tracking active sessions, to go out of sync.
    // Causing calculations of play time limit time used to be incorrect after a reset had been done.
    entityManager.flush();
    entityManager.clear();
    return userRepository.findForUpdate(userId);
  }

  public void checkUserPermission(String domainName, Principal principal, String role) {
    if (!LithiumTokenUtil.builder(tokenStore, principal).build().hasRole(domainName, role))
      throw new AccessDeniedException("User does not have access to update username for this domain");
  }
  public boolean isUserFullNameChanged(List<ChangeLogFieldChange> clfc) {
    return clfc.stream()
        .anyMatch(changeLogFieldChange ->
            "firstName".equalsIgnoreCase(changeLogFieldChange.getField()) ||
                "lastName".equalsIgnoreCase(changeLogFieldChange.getField()) ||
                "lastNamePrefix".equalsIgnoreCase(changeLogFieldChange.getField())
        );
  }
  public boolean isDomainNameOfEcosystemRootType(String domainName) {
    return cachingDomainClientService.isDomainNameOfEcosystemRootType(domainName);
  }

  @Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
  public User clearPlayerPersonalInfo(User user, LithiumTokenUtil tokenUtil) {
    Status deletedStatus = statusRepository.findByName(lithium.service.user.client.enums.Status.DELETED.statusName());
    user = changeUserStatus(
        UserAccountStatusUpdate.builder()
            .userGuid(user.guid())
            .authorGuid(tokenUtil.guid())
            .statusName(deletedStatus.getName())
            .statusReasonName("")
            .noteCategoryName(Category.ACCOUNT.getName())
            .noteSubCategoryName(SubCategory.CLOSURE.getName())
            .notePriority(70)
            .build(),
        tokenUtil
    );
    User target = new User();
    user.setUsername(String.valueOf(Instant.now().toEpochMilli()));
    user.setDeleted(true);
    user.setBiometricsStatus(BiometricsStatus.NOT_REQUIRED);

    final BeanWrapper source = new BeanWrapperImpl(user);
    final BeanWrapper destination = new BeanWrapperImpl(target);

    ArrayList<String> fieldsToInclude = new ArrayList<>();
    fieldsToInclude.add("id");
    fieldsToInclude.add("guid");
    fieldsToInclude.add("domain");
    fieldsToInclude.add("status");
    fieldsToInclude.add("deleted");
    fieldsToInclude.add("username");
    fieldsToInclude.add("version");
    fieldsToInclude.add("biometricsStatus");

    Field[] fields = user.getClass().getDeclaredFields();

    for (Field field : fields) {
      try {
        if (field.getName().equals("log") ||field.getName().equals("serialVersionUID")) {
          continue;
        }
        if (fieldsToInclude.contains(field.getName())) {
          log.debug("Copying over field in object copy: " + field.getName());
          Object providedObject = source.getPropertyValue(field.getName());
          destination.setPropertyValue(field.getName(), providedObject);
          continue;
        }
        if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
          destination.setPropertyValue(field.getName(), false);
          continue;
        }
        if (field.getType().equals(Date.class)) {
          destination.setPropertyValue(field.getName(), new Date());
          continue;
        }
        destination.setPropertyValue(field.getName(), null);
        log.debug("Adding property to destination object: " + field.getName());
      } catch (NotReadablePropertyException nrpe) {
        log.debug("Non readable property when synchronising user information: " + field.getName());
      }
    }

    return save(target, true);
  }

  public User updatePlayer(String domain, User user, PlayerBasic userUpdate, Principal principal) throws Exception {

    boolean emailAddressChanged = false;
    String oldEmail = user.getEmail();
    boolean pendingEmailValidationActivate = Boolean.parseBoolean(domainClientService.getDomainSetting(domain, DomainSettings.PENDING_EMAIL_VALIDATION_ACTIVATE));

    if (!ObjectUtils.nullSafeEquals(userUpdate.getEmail(), user.getEmail())) {
      boolean uniqueEmail = isUniqueEmail(user.getDomain().getName(), userUpdate.getEmail(), !cachingDomainClientService.isDomainInAnyEcosystem(domain));
      if (uniqueEmail) {
        emailAddressChanged = true;
        //LSPLAT-5389 emailValidated remains true whilst pendingEmail not validated
        user.setEmailValidated(pendingEmailValidationActivate && user.isEmailValidated());
        if (pendingEmailValidationActivate) {
          addOrUpdateDomainSpecificUserLabelValues(user, Map.of(Label.PENDING_EMAIL, userUpdate.getEmail()));
        }
      } else {
        userUpdate.setEmail(user.getEmail());
      }
    } else {
      userUpdate.setEmailValidated(user.isEmailValidated());
    }

    List<ChangeLogFieldChange> clfc = changeLogService.copy(userUpdate, user, new String[]{"firstName", "lastName",
        "countryCode", "email", "telephoneNumber", "cellphoneNumber", "dobDay", "dobMonth", "dobYear", "emailValidated"});
    if(clfc.size() > 0){
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
          userUpdate.getComments(), null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, domain);
    }

    user.setUpdatedDate(new Date());
    user = save(user);

    if (emailAddressChanged) {
      try {
        emailValidationService.sendEmailValidationTokenEmail(user.getDomain().getName(),
            pendingEmailValidationActivate ? userUpdate.getEmail() : user.getEmail(), emailAddressChanged, false, false);
        preValidationNotificationService.sendEmailOrCellphoneChangeNotification(oldEmail, userUpdate.getCellphoneNumber(), user);
      } catch (Exception e) {
        log.error("Problem sending email validation mail for: " + user.getGuid() +" reason: (" + e.getMessage() + ")", e);
      }
    }

    // Finished saving user data. Call service-access if domain config contains an access rule for update of user details.
    // We don't care about the outcome at this point.
    try {
      accessRuleService.userDetailsUpdateAccessRule(user);
    } catch (Exception e) {
      log.error("Unable to call access provider on user details update for " + user.guid() + ". " + e.getMessage(), e);
    }
    try {
      pubSubUserService.publishAccountChange(user, principal);
    } catch (Exception e){
      log.warn("can't sent pub-sub message for: " + user.getGuid() +". " + e.getMessage());
    }

    return user;
  }

  public Response<User> redoEmailValidation(
      String domain,
      User user,
      String email,
      boolean isNumeric,
      LithiumTokenUtil util
  ) throws Exception {
    if (user == null) return Response.<User>builder().status(Response.Status.NOT_FOUND).build();
    if (!user.getDomain().getName().equals(domain)) return Response.<User>builder().status(Response.Status.NOT_FOUND).build();

    boolean emailAddressChanged = false;
    String oldEmailAddress = user.getEmail();

    boolean pendingEmailValidationActivate = Boolean.parseBoolean(domainClientService.getDomainSetting(domain, DomainSettings.PENDING_EMAIL_VALIDATION_ACTIVATE));

    if (email != null && (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(email))) {
      emailAddressChanged = true;
      if (pendingEmailValidationActivate) {
        addOrUpdateDomainSpecificUserLabelValues(user, ImmutableMap.of(Label.PENDING_EMAIL, email));
      } else {
        user.setEmail(email);
        user = save(user);
        List<ChangeLogFieldChange> clfc = new ArrayList<>();
        ChangeLogFieldChange c = ChangeLogFieldChange.builder()
            .field("email")
            .fromValue(oldEmailAddress)
            .toValue(user.getEmail())
            .build();
        clfc.add(c);
        changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), util.guid(),
            util,null, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, domain);
      }
    }

    user.setEmailValidated(pendingEmailValidationActivate && user.isEmailValidated());
    user = save(user);

    try {
      if (isNumeric) {
        emailValidationService.sendEmailValidationTokenEmailNumericToken(domain, email, emailAddressChanged, true, false);
      } else {
        emailValidationService.sendEmailValidationTokenEmail(domain, email, emailAddressChanged, true, false);
      }
    } catch (Exception e) {
      log.error("Problem sending email validation mail (" + e.getMessage() + ")", e);
    }
    preValidationNotificationService.sendEmailOrCellphoneChangeNotification(oldEmailAddress, user.getCellphoneNumber(), user);
    pubSubUserService.publishAccountChange(user, util.getAuthentication());

    return Response.<User>builder().data(user).status(Response.Status.OK).build();
  }

  public User toggleEmailValidation(User user, Principal principal)
      throws Status550ServiceDomainClientException, Status500LimitInternalSystemClientException {
    boolean validated = user.isEmailValidated();

    user.setEmailValidated(!validated);

//    user = emailValidationService.pendingEmailCheck(user);

    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field("emailValidated")
        .fromValue(String.valueOf(validated))
        .toValue(String.valueOf(!validated))
        .build();
    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    clfc.add(c);
    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(),  tokenService.getUtil(principal),
        null, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());
    User savedUser = save(user);
    pubSubUserService.publishAccountChange(savedUser, principal);
    return user;

  }

  public User toggleSowValidation(User user, Principal principal) {
    boolean validated = user.isRequireSowDocument();
    user.setRequireSowDocument(validated? false : true);
    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field("requireSowDocument")
        .fromValue(String.valueOf(validated))
        .toValue(String.valueOf(validated? false : true))
        .build();
    List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
    clfc.add(c);
    changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(),  tokenService.getUtil(principal),
        null, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());
    User savedUser = save(user);
    pubSubUserService.publishAccountChange(savedUser, principal);
    return user;
  }

  public Response<User> redoMobilePhoneValidation(String domain, User user, String mobilePhone, Principal principal) {
    if (user == null) return Response.<User>builder().status(Response.Status.NOT_FOUND).build();
    if (!user.getDomain().getName().equals(domain)) return Response.<User>builder().status(Response.Status.NOT_FOUND).build();

    Boolean mobilePhoneChanged = false;
    String oldMobilePhone = user.getCellphoneNumber();
    if (mobilePhone != null && (user.getCellphoneNumber() == null || !user.getCellphoneNumber().equalsIgnoreCase(mobilePhone))) {
      mobilePhoneChanged = true;
      user.setCellphoneNumber(mobilePhone);
      user = save(user);
      List<ChangeLogFieldChange> clfc = new ArrayList<>();
      ChangeLogFieldChange c = ChangeLogFieldChange.builder()
          .field("cellphoneNumber")
          .fromValue(oldMobilePhone)
          .toValue(user.getCellphoneNumber())
          .build();
      clfc.add(c);

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
          null, null, clfc, Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, domain);
    }

    user.setCellphoneValidated(false);
    user = save(user);

    try {
      smsValidationService.sendCellphoneValidationTokenSms(domain, user.getUsername(), mobilePhoneChanged, true);
    } catch (Exception e) {
      log.error("Problem sending cellphone validation sms (" + e.getMessage() + ")", e);
    }

    preValidationNotificationService.sendEmailOrCellphoneChangeNotification(user.getEmail(), oldMobilePhone, user);

    return Response.<User>builder().data(user).status(Response.Status.OK).build();
  }

  public boolean isEmailAddressChanged(User user, User oldUser, PlayerBasic userUpdate) {

    if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(oldUser.getEmail())) {
      //LSPLAT-2925 / PLAT-3660 Confirm email is unique in ecosystem
      //Checkif in ecosystem(
      if (domainClientService.isDomainInAnyEcosystem(user.getDomain().getName())) {
        if (user.getEmail() != null && !isUniqueEmail(user.getDomain().getName(), user.getEmail(), false)) {
          throw new Status431UserExistsInEcosystemException(
              messageSource.getMessage("UI_NETWORK_ADMIN.DOMAIN.EMAIL.EXISTS_IN_ECOSYSTEM", null, LocaleContextHolder.getLocale()));
        }
      } else {
        //LSPLAT-2925 / PLAT-3660 Confirm email is unique in domain, or that duplicate emails are allowed within the domain
        if (user.getEmail() != null && !isUniqueEmail(user.getDomain().getName(), user.getEmail(), true)
            && !Boolean.parseBoolean(domainClientService.getDomainSetting(user.getDomain().getName(), DomainSettings.ALLOW_DUPLICATE_EMAIL))) {
          throw new Status453EmailNotUniqueException(
              messageSource.getMessage("UI_NETWORK_ADMIN.DOMAIN.EMAIL.EXISTS_IN_DOMAIN", null, LocaleContextHolder.getLocale()));
        }
      }
    }

    boolean emailAddressChanged = false;
    if (userUpdate.getEmail() != null) {
      if (!isValidIEmailAddress(userUpdate.getEmail())) {
        log.warn("User profile update failed, the email(" + userUpdate.getEmail() + ") is incorrect. Received UserUpdate is:" + userUpdate.toString());
        throw new Status420InvalidEmailException(RegistrationError.INVALID_EMAIL.getResponseMessageLocal(messageSource, user.getDomain().getName()));
      }
      emailAddressChanged = (!userUpdate.getEmail().equalsIgnoreCase(oldUser.getEmail()));
      if (emailAddressChanged && !isUniqueEmail(user.getDomain().getName(), userUpdate.getEmail()) && !Boolean.parseBoolean(
          domainClientService.getDomainSetting(user.getDomain().getName(), DomainSettings.ALLOW_DUPLICATE_EMAIL))) {
        throw new Status453EmailNotUniqueException(
            RegistrationError.EMAIL_NOT_UNIQUE.getResponseMessageLocal(messageSource, user.getDomain().getName()));
      }
    }
    return emailAddressChanged;
  }

  public Long countByUserCategoriesId(Long userCategoryId) {
    return userRepository.countByUserCategories_Id(userCategoryId);
  }
  public List<User> findAllByDeletedAndPendingDeleteStatus() {
    Status deletedStatus = statusRepository.findByName(lithium.service.user.client.enums.Status.DELETED.statusName());
    return userRepository.findAllByDeletedIsTrueAndStatusIsNot(deletedStatus);
  }

  public Response<AdjustmentTransaction> adjustBalance(AdjustMultiRequest request, LithiumTokenUtil token)
      throws Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException, lithium.exceptions.Status500InternalServerErrorException {
    validateAccountCode(request.getContraAccountCode(), request.getAmountCents());
    Response<AdjustmentTransaction> adjustMultiResponse = accountingClientService.adjustMultiV2(request);
    if (adjustMultiResponse.isSuccessful()) {
      logBalanceAdjustChanges(request, token);
    }
    return adjustMultiResponse;
  }

  private void logBalanceAdjustChanges(AdjustMultiRequest request, LithiumTokenUtil token) {
    try {
      User user = findFromGuid(request.getOwnerGuid());
      List<ChangeLogFieldChange> clfc = new ArrayList<>();

      BigDecimal amount = CurrencyAmount.fromCents(request.getAmountCents()).toAmount().setScale(2);

      String comment = messageSource.getMessage("UI_NETWORK_ADMIN.USER.BALANCE.ADJUST",
          new Object[] {
              request.getCurrencyCode(),
              amount.toString(),
              request.getContraAccountCode(),
              request.getLabels()[0]
          },
          Locale.US
      );
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), request.getAuthorGuid(), token, comment, null, clfc,
          Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, request.getDomainName());
    } catch (Exception e) {
      log.error("ChangeLogService could not register changes  ", e);
      throw new Status500InternalServerErrorException("changeLogService error");
    }
  }

  private void validateAccountCode(String accountCode, long amount) {
    if(!AccountCode.isValid(accountCode, amount)) {
      log.error("Account code is not valid for this balance adjustment");
      throw new IllegalArgumentException("Account code is not valid");
    }
  }

}
