package lithium.service.user.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status407IpBlockedException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status432UserExistsInAnotherExclusiveDomainException;
import lithium.exceptions.Status447AccountFrozenException;
import lithium.exceptions.Status450AccountFrozenSelfExcludedException;
import lithium.exceptions.Status453EmailNotUniqueException;
import lithium.exceptions.Status460LoginRestrictedException;
import lithium.exceptions.Status463IncompleteUserRegistrationException;
import lithium.exceptions.Status465DomainUnknownCountryException;
import lithium.exceptions.Status469InvalidInputException;
import lithium.exceptions.Status492ExcessiveFailedLoginBlockException;
import lithium.math.CurrencyAmount;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.access.client.AccessService;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.AuthResultContainer;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.access.client.objects.CheckAuthorizationResult;
import lithium.service.casino.client.CasinoBonusClient;
import lithium.service.casino.client.data.CasinoBonus;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.EcosystemRelationshipTypes;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.ecosystem.EcosystemDomainRelationship;
import lithium.service.geo.client.GeoClient;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status479DomainAgeLimitException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.stats.client.enums.Event;
import lithium.service.stats.client.enums.Type;
import lithium.service.stats.client.objects.StatEntry;
import lithium.service.stats.client.stream.QueueStatEntry;
import lithium.service.stats.client.stream.StatsStream;
import lithium.service.translate.client.objects.LoginError;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.UserPubSubClient;
import lithium.service.user.client.enums.StatusReason;
import lithium.service.user.client.objects.AddressBasic;
import lithium.service.user.client.objects.AutoRegistration;
import lithium.service.user.client.objects.DuplicateCheckRequestData;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.client.objects.TermsAndConditionsVersion;
import lithium.service.user.client.objects.ValidatePreRegistrationResponse;
import lithium.service.user.data.dto.PlayerPlaytimeLimitConfigRequest;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.IncompleteUser;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserApiToken;
import lithium.service.user.data.entities.UserRevisionLabelValue;
import lithium.service.user.data.repositories.AddressRepository;
import lithium.service.user.data.repositories.IncompleteUserRepository;
import lithium.service.user.data.repositories.StatusReasonRepository;
import lithium.service.user.data.repositories.StatusRepository;
import lithium.service.user.exceptions.Status420InvalidEmailException;
import lithium.service.user.exceptions.Status421InvalidCellphoneException;
import lithium.service.user.exceptions.Status422InvalidDateOfBirthException;
import lithium.service.user.exceptions.Status423InvalidUsernameException;
import lithium.service.user.exceptions.Status424PasswordNotComplexException;
import lithium.service.user.exceptions.Status425InvalidLastNamePrefixException;
import lithium.service.user.exceptions.Status431UserExistsInEcosystemException;
import lithium.service.user.exceptions.Status451UnderageException;
import lithium.service.user.exceptions.Status452UsernameNotUniqueException;
import lithium.service.user.exceptions.Status454CellphoneNotUniqueException;
import lithium.service.user.exceptions.Status461UserNotUniqueException;
import lithium.service.user.exceptions.Status471BalanceLimitNotProvidedException;
import lithium.service.user.exceptions.Status472DepositLimitNotProvidedException;
import lithium.service.user.exceptions.Status473TimeLimitNotProvidedException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.provider.sphonic.idin.objects.IncompleteUserStatus;
import lithium.service.user.services.oauthClient.OauthApiInternalClientService;
import lithium.service.user.validators.UserValidatorProperties;
import lithium.util.ChangeLogType;
import lithium.util.ExceptionMessageUtil;
import lithium.util.PasswordHashing;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lithium.service.access.client.AccessService.ListType.DUPLICATE_CHECK;

@Service
@Slf4j
public class SignupService {
  /**
   * If updating this, remember to update User entity. (firstname + lastname) lithium.service.user.data.entities.User#firstName
   */
  final MessageSource messageSource;
  final AccessService accessService;
  final AddressRepository addressRepository;
  final AffiliateService affiliateService;
  final AutoRestrictionTriggerStream autoRestrictionTriggerStream;
  final ChangeLogService changeLogService;
  final CachingDomainClientService cachingDomainClientService;
  final DomainService domainService;
  final EmailValidationService emailValidationService;
  final LithiumServiceClientFactory factory;
  final IncompleteUserRepository incompleteUserRepository;
  final LimitService limitService;
  final ModelMapper modelMapper;
  final OauthApiInternalClientService oauthApiInternalClientService;
  final PubSubUserService pubSubUserService;
  final ReferralService referralService;
  final SignupEventService signupEventService;
  final SMSValidationService smsValidationService;
  final StatsStream statsStream;
  final StatusRepository statusRepository;
  final StatusReasonRepository statusReasonRepository;
  final UserApiTokenService userApiTokenService;
  final UserLinkService userLinkService;
  final UserService userService;
  final IncompleteUserService incompleteUserService;
  final PlayTimeLimitsService playTimeLimitsService;
  final PlaytimeLimitsV2Service playtimeLimitsV2Service;
  final UserEventService userEventService;
  final RegistrationSynchronizeService synchronizeService;
  @Value("${lithium.password.salt}")
  private String passwordSalt;
  final LithiumServiceClientFactory lithiumServiceClientFactory;
  final CollectionDataService collectionDataService;

  @Autowired
  public SignupService(
      AccessService accessService,
      AddressRepository addressRepository,
      AffiliateService affiliateService,
      AutoRestrictionTriggerStream autoRestrictionTriggerStream,
      UserService userService, ModelMapper modelMapper,
      SignupEventService signupEventService,
      ChangeLogService changeLogService,
      CachingDomainClientService cachingDomainClientService,
      DomainService domainService,
      EmailValidationService emailValidationService,
      UserApiTokenService userApiTokenService,
      OauthApiInternalClientService oauthApiInternalClientService,
      UserLinkService userLinkService,
      SMSValidationService smsValidationService,
      LithiumServiceClientFactory factory,
      IncompleteUserRepository incompleteUserRepository,
      LimitService limitService,
      ReferralService referralService,
      StatusRepository statusRepository,
      StatusReasonRepository statusReasonRepository,
      StatsStream statsStream,
      PubSubUserService pubSubUserService,
      MessageSource messageSource,
      IncompleteUserService incompleteUserService,
      PlayTimeLimitsService playTimeLimitsService,
      PlaytimeLimitsV2Service playtimeLimitsV2Service,
      UserEventService userEventService, RegistrationSynchronizeService synchronizeService,
      LithiumServiceClientFactory lithiumServiceClientFactory, CollectionDataService collectionDataService) {
    this.accessService = accessService;
    this.addressRepository = addressRepository;
    this.affiliateService = affiliateService;
    this.autoRestrictionTriggerStream = autoRestrictionTriggerStream;
    this.userService = userService;
    this.modelMapper = modelMapper;
    this.signupEventService = signupEventService;
    this.changeLogService = changeLogService;
    this.cachingDomainClientService = cachingDomainClientService;
    this.domainService = domainService;
    this.emailValidationService = emailValidationService;
    this.userApiTokenService = userApiTokenService;
    this.oauthApiInternalClientService = oauthApiInternalClientService;
    this.userLinkService = userLinkService;
    this.smsValidationService = smsValidationService;
    this.factory = factory;
    this.incompleteUserRepository = incompleteUserRepository;
    this.limitService = limitService;
    this.referralService = referralService;
    this.statusRepository = statusRepository;
    this.statusReasonRepository = statusReasonRepository;
    this.statsStream = statsStream;
    this.pubSubUserService = pubSubUserService;
    this.messageSource = messageSource;
    this.incompleteUserService = incompleteUserService;
    this.playTimeLimitsService = playTimeLimitsService;
    this.playtimeLimitsV2Service = playtimeLimitsV2Service;
    this.userEventService = userEventService;
    this.synchronizeService = synchronizeService;
    this.lithiumServiceClientFactory = lithiumServiceClientFactory;
    this.collectionDataService = collectionDataService;
  }

  private void validateTimeSlotLimitFormatReceived(PlayerBasic pb) throws Status478TimeSlotLimitException, Status426InvalidParameterProvidedException {
    if (!Stream.of(pb.getTimeSlotLimitStart(), pb.getTimeSlotLimitEnd()).anyMatch(Objects::nonNull)) {
      throw new Status478TimeSlotLimitException(RegistrationError.TIME_SLOT_LIMIT_NOT_PROVIDED.getResponseMessageLocal(messageSource, pb.getDomainName()));
    }
    else if (!playTimeLimitsService.isTimeFormatValid(pb.getTimeSlotLimitStart()) || !playTimeLimitsService.isTimeFormatValid(pb.getTimeSlotLimitEnd())) {
      String parameter = "timeSlotLimitStart = " + pb.getTimeSlotLimitStart() + " timeSlotLimitEnd = " + pb.getTimeSlotLimitEnd();
      throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, pb.getDomainName(), new Object[] { parameter }));
    }
    else {
      Long utcTimeFrom = playTimeLimitsService.getTimestamp(pb.getTimeSlotLimitStart());
      Long utcTimeTo = playTimeLimitsService.getTimestamp(pb.getTimeSlotLimitEnd());
      Date fromDate = new Date(utcTimeFrom);                         // 01 Jan 1700 11:00 || 17 June 2022 11:00
      Date toDate = new Date(utcTimeTo);
      if (!playTimeLimitsService.isTimeSlotValid(fromDate, toDate))
        throw new Status478TimeSlotLimitException(RegistrationError.TIME_SLOT_LIMIT_INVALID_RANGE.getResponseMessageLocal(messageSource, pb.getDomainName()));
    }
  }

  public lithium.service.domain.client.objects.Domain getExternalDomain(String domainName)
      throws Status550ServiceDomainClientException {
    try {
      lithium.service.domain.client.objects.Domain externalDomain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);

      if (!externalDomain.getEnabled()) {
        throw new Status550ServiceDomainClientException(RegistrationError.DOMAIN_IS_DISABLED.getResponseMessageLocal(messageSource, domainName));
      }
      if (externalDomain.getDeleted()) {
        throw new Status550ServiceDomainClientException(RegistrationError.DOMAIN_NON_EXISTING.getResponseMessageLocal(messageSource, domainName));
      }
      if (!externalDomain.getPlayers()) {
        throw new Status550ServiceDomainClientException(RegistrationError.NOT_PLAYER_DOMAIN.getResponseMessageLocal(messageSource, domainName));
      }
      return externalDomain;
    } catch (Status550ServiceDomainClientException exception) {
      throw new Status550ServiceDomainClientException(RegistrationError.NO_SUCH_DOMAIN.getResponseMessageLocal(messageSource, domainName), exception.getStackTrace());
    }

  }

  public AuthResultContainer checkUserAgentValidityPreSignup(final String ipAddress, final String userAgent,
      final lithium.service.domain.client.objects.Domain domain,
      final AddressBasic address, String userName, String deviceId, PlayerBasic playerBasic)
      throws Status551ServiceAccessClientException {
    Map<String, String> ipAndUserAgentData = accessService.parseIpAndUserAgent(ipAddress, userAgent);
    AuthResultContainer art = AuthResultContainer.builder().ipAndUserAgentData(ipAndUserAgentData).build();

    String preSignupAccessRule = domain.getPreSignupAccessRule();
    if (preSignupAccessRule != null && !preSignupAccessRule.isEmpty()) {
      Map<String, String> claimedGeoData = new LinkedHashMap<>();

      claimedGeoData.put(AccessService.MAP_CLAIMED_COUNTRY, (address != null) ? address.getCountry() : null);
      claimedGeoData.put(AccessService.MAP_CLAIMED_STATE, (address != null) ? address.getAdminLevel1() : null);
      claimedGeoData.put(AccessService.MAP_CLAIMED_CITY, (address != null) ? address.getCity() : null);
      claimedGeoData.put(AccessService.MAP_POST, (address != null) ? address.getPostalCode() : null);
      AuthorizationResult authorizationResult = accessService.checkAuthorization(domain.getName(),
          preSignupAccessRule, claimedGeoData, ipAndUserAgentData, deviceId, domain.getName() + "/" + userName, false,
          playerBasic);
      log.info("authorizationResult " + authorizationResult);
      art.setAuthorizationResult(authorizationResult);
    }
    return art;
  }

  public AuthResultContainer checkUserAgentValidityPostSignup(final String ipAddress, final String userAgent,
      final lithium.service.domain.client.objects.Domain domain,
      final AddressBasic address, String userName, String deviceId)
      throws Status551ServiceAccessClientException {
    Map<String, String> ipAndUserAgentData = accessService.parseIpAndUserAgent(ipAddress, userAgent);
    AuthResultContainer art = AuthResultContainer.builder().ipAndUserAgentData(ipAndUserAgentData).build();

    String signupAccessRule = domain.getSignupAccessRule();
    if (signupAccessRule != null && !signupAccessRule.isEmpty()) {
      Map<String, String> claimedGeoData = new LinkedHashMap<>();

      claimedGeoData.put(AccessService.MAP_CLAIMED_COUNTRY, (address != null) ? address.getCountry() : null);
      claimedGeoData.put(AccessService.MAP_CLAIMED_STATE, (address != null) ? address.getAdminLevel1() : null);
      claimedGeoData.put(AccessService.MAP_CLAIMED_CITY, (address != null) ? address.getCity() : null);
      claimedGeoData.put(AccessService.MAP_POST, (address != null) ? address.getPostalCode() : null);
      AuthorizationResult authorizationResult = accessService.checkAuthorization(domain.getName(),
          signupAccessRule, claimedGeoData, ipAndUserAgentData, deviceId, domain.getName() + "/" + userName, false);
      log.info("authorizationResult " + authorizationResult);
      art.setAuthorizationResult(authorizationResult);
    }
    return art;
  }

  private AuthResultContainer checkUserAgentValidityPreSignup(HttpServletRequest request,
      final lithium.service.domain.client.objects.Domain domain,
      final AddressBasic address, String userName, String deviceId, PlayerBasic playerBasic)
      throws Status551ServiceAccessClientException {
    String ipAddress = ipAddress(request);
    String userAgent = request.getHeader("User-Agent");
    if (request.getHeader("User-Agent-Forwarded") != null) {
      userAgent = request.getHeader("User-Agent-Forwarded");
    }
    return checkUserAgentValidityPreSignup(ipAddress, userAgent, domain, address, userName, deviceId, playerBasic);
  }

  public String getUserAgent(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    if (request.getHeader("User-Agent-Forwarded") != null) {
      userAgent = request.getHeader("User-Agent-Forwarded");
    }
    return userAgent;
  }

  public void addCorrelationID(HttpServletRequest request, PlayerBasic playerBasic) throws Status426InvalidParameterProvidedException {
    String correlationId = request.getHeader("XXX-Correlation-ID");
    boolean required = Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(playerBasic.getDomainName(), DomainSettings.CORRELATION_ID));
    if (correlationId == null || correlationId.isEmpty()) {
      if (required) {
        String parameter = RegistrationError.CORRELATION_ID_NOT_PROVIDED.getResponseMessageLocal(messageSource, playerBasic.getDomainName());
        throw new Status426InvalidParameterProvidedException(
            RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, playerBasic.getDomainName(), new Object[]{parameter}));
      }
    } else {
      playerBasic.setDeviceId(correlationId);
      addAdditionalData(playerBasic, Label.CORRELATION_ID, correlationId);
    }
  }

  private void addAdditionalData(PlayerBasic playerBasic, String label, String value) {
    Map<String, String> additionalData = playerBasic.getAdditionalData() != null ? playerBasic.getAdditionalData() : new HashMap<>();
    additionalData.put(label, value);
    playerBasic.setAdditionalData(additionalData);
  }

  private AuthResultContainer checkUserAgentValidityPostSignup(HttpServletRequest request,
      final lithium.service.domain.client.objects.Domain domain,
      final AddressBasic address, String userName, String deviceId)
      throws Status551ServiceAccessClientException {
    String ipAddress = ipAddress(request);
    String userAgent = request.getHeader("User-Agent");
    if (request.getHeader("User-Agent-Forwarded") != null) {
      userAgent = request.getHeader("User-Agent-Forwarded");
    }
    return checkUserAgentValidityPostSignup(ipAddress, userAgent, domain, address, userName, deviceId);
  }

  private String ipAddress(HttpServletRequest request) {
    String ipAddress = request.getRemoteAddr();
    if (request.getHeader("X-Forwarded-For") != null) {
      ipAddress = request.getHeader("X-Forwarded-For");
    }
    return ipAddress;
  }

  private String userAgent(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    if (request.getHeader("User-Agent-Forwarded") != null) {
      userAgent = request.getHeader("User-Agent-Forwarded");
    }
    return userAgent;
  }

  private void addIPOnBlockList(String domainName, String ipblockList, String ipAddress) {
//		if ((signupAccessRule == null) || (signupAccessRule.isEmpty())) return;
    if ((ipblockList == null) || (ipblockList.isEmpty())) {
      return;
    }
    if ((ipAddress != null) && (ipAddress.contains(","))) {
      ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
    }
    log.debug("Adding : '" + ipAddress + "' to : '" + ipblockList + "' for domain : " + domainName);
    accessService.addValueToList(domainName, ipblockList, ipAddress);
  }

  private boolean dobValid(Integer year, Integer month, Integer day, String domainName) throws Status422InvalidDateOfBirthException {
    if ((year == null) && (month == null) && (day == null)) {
      return true;
    }
    try {
      String dateFormat = "yyyy.M.d";
      DateFormat sdf = new SimpleDateFormat(dateFormat);
      ZoneId defaultZoneId = ZoneId.systemDefault();
      sdf.setLenient(false);
      Date dob = sdf.parse(year + "." + month + "." + day);

      Date today = Date.from(LocalDate.now().atStartOfDay(defaultZoneId).toInstant());
      if(dob.after(today)){
        throw new Status422InvalidDateOfBirthException(RegistrationError.INVALID_DOB.getResponseMessageLocal(messageSource, domainName));
      }
    } catch (java.text.ParseException parseException) {
      log.error("RegisterPlayer :: error evaluating dob : " + parseException.getMessage());
      return false;
    }
    return true;
  }

  public User registerV2Player(
      PlayerBasic pb,
      HttpServletRequest request
  ) throws
      Status403AccessDeniedException,
      Status421InvalidCellphoneException,
      Status420InvalidEmailException,
      Status422InvalidDateOfBirthException,
      Status426InvalidParameterProvidedException,
      Status451UnderageException,
      Status452UsernameNotUniqueException,
      Status453EmailNotUniqueException,
      Status454CellphoneNotUniqueException,
      Status500InternalServerErrorException,
      Status550ServiceDomainClientException,
      Status551ServiceAccessClientException{

    lithium.service.domain.client.objects.Domain externalDomain = getExternalDomain(pb.getDomainName());

   // pb.setTestUser(externalDomain.getIsTestDomain());

    AuthResultContainer arc = checkUserAgentValidityPreSignup(request, externalDomain, pb.getResidentialAddress(), pb.getUsername(), pb.getDeviceId(),
        pb);
    if (!Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(pb.getDomainName(), DomainSettings.ALLOW_REGISTER_V2))) {
      throw new Status403AccessDeniedException(RegistrationError.ACCESS_DENIED.getResponseMessageLocal(messageSource, pb.getDomainName(), new Object[]{ DomainSettings.ALLOW_REGISTER_V2.key()}));
    }

    try {
      return registerPlayer(pb, arc); //From register V2 flow
    } catch (Status461UserNotUniqueException e) {
      throw new Status500InternalServerErrorException(RegistrationError.USER_NOT_UNIQUE.getResponseMessageLocal(messageSource, pb.getDomainName()));
    } catch (Status478TimeSlotLimitException e) {
      // Status478TimeSlotLimitException not supported on V2, rethrowing as a 500
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  @TimeThisMethod
  @Transactional(propagation = Propagation.NEVER)
  public User registerPlayer(
      PlayerBasic pb,
      HttpServletRequest request
  ) throws
      Status403AccessDeniedException,
      Status420InvalidEmailException,
      Status421InvalidCellphoneException,
      Status422InvalidDateOfBirthException,
      Status426InvalidParameterProvidedException,
      Status451UnderageException,
      Status452UsernameNotUniqueException,
      Status453EmailNotUniqueException,
      Status454CellphoneNotUniqueException,
      Status461UserNotUniqueException,
      Status478TimeSlotLimitException,
      Status500InternalServerErrorException,
      Status550ServiceDomainClientException,
      Status551ServiceAccessClientException {

    lithium.service.domain.client.objects.Domain externalDomain = getExternalDomain(pb.getDomainName());
    addCorrelationID(request, pb);
    AuthResultContainer arc = checkUserAgentValidityPreSignup(request, externalDomain, pb.getResidentialAddress(), pb.getUsername(), pb.getDeviceId(),
        pb);
    return registerPlayer(pb, arc); //From register V1, V3 & V4 flows
  }

  @TimeThisMethod
  @Transactional(propagation = Propagation.NEVER)
  public User registerPlayer(
    PlayerBasic pb,
    AuthResultContainer arc
  ) throws
      Status403AccessDeniedException,
      Status420InvalidEmailException,
      Status421InvalidCellphoneException,
      Status422InvalidDateOfBirthException,
      Status426InvalidParameterProvidedException,
      Status451UnderageException,
      Status452UsernameNotUniqueException,
      Status453EmailNotUniqueException,
      Status454CellphoneNotUniqueException,
      Status461UserNotUniqueException,
      Status478TimeSlotLimitException,
      Status500InternalServerErrorException,
      Status550ServiceDomainClientException,
      Status551ServiceAccessClientException {

    User user = preRegisterPlayer(pb, arc); // From registration V1 - V4 flows

    try {
      user = postRegisterPlayer(pb, arc, user); // From registration V1 - V4 flows
    } catch (ErrorCodeException e) {
      log.debug("Exception occured whilst trying to register a user, still going to trigger the event to register root ecosystem account playerBasic= {}", pb);
      throw e;
    } finally {
      //Trigger media account registration and cross domain linking
      String ipAddress = (arc.getIpAndUserAgentData().get(AccessService.MAP_IP) != null)? arc.getIpAndUserAgentData().get(AccessService.MAP_IP): null;
      String userAgent = (arc.getIpAndUserAgentData().get(AccessService.MAP_USERAGENT) != null)? arc.getIpAndUserAgentData().get(AccessService.MAP_USERAGENT): null;
      userEventService.streamUserRegistrationSuccessEvent(user.getId(), ipAddress, userAgent , pb.getDeviceId(), pb.getPassword(), pb.isChannelsOptOut());
    }
    // LSPLAT-5330 - NO MORE COMMITS ON USER FROM THIS POINT TO AVOID DEADLOCKS ON MEDIA ACCOUNT REGISTRATION !!!
    return user;
  }

  @TimeThisMethod
  @Transactional(propagation = Propagation.NEVER)
  public User preRegisterPlayer(
    PlayerBasic pb,
    AuthResultContainer arc
  ) throws
      Status403AccessDeniedException,
      Status420InvalidEmailException,
      Status421InvalidCellphoneException,
      Status422InvalidDateOfBirthException,
      Status426InvalidParameterProvidedException,
      Status452UsernameNotUniqueException,
      Status453EmailNotUniqueException,
      Status454CellphoneNotUniqueException,
      Status478TimeSlotLimitException,
      Status500InternalServerErrorException,
      Status550ServiceDomainClientException {

    //Pre domain validation checks included
    getExternalDomain(pb.getDomainName());

    log.debug("PlayerBasic :: " + pb);
    Domain domain = domainService.findOrCreate(pb.getDomainName());
    log.debug("Domain :: " + domain);

    /**
     * Incomplete user pre-registration checks
     */
    incompleteUserService.incompleteUserPreRegChecks(pb);

    if (!arc.isSuccesful()) {
      signupEventService.saveSignupEvent(arc.getIpAndUserAgentData(), domain, arc.getAuthorizationResult().getMessage(), 0, false);
      log.debug("Status403AccessDeniedException(" + arc.getAuthorizationResult().getErrorMessage() + ")");
      throw new Status403AccessDeniedException(RegistrationError.ACCESS_DENIED.getResponseMessageLocal(messageSource, pb.getDomainName(), arc.getAuthorizationResult().getErrorMessage(), arc.getAuthorizationResult().getErrorMessage()));
    }

    /**
     * If the playerbasic contains collectionData it needs to be validated
     */
    collectionDataService.validateCollectionDataInput(pb);

    if (playTimeLimitsService.isPlayerTimeFrameLimitsActivatedForDomain(domain.getName())) {
      // If time slot limits are provided, then we need to validate before saving as part of pre-register steps. saving happens in post register steps
      if (Stream.of(pb.getTimeSlotLimitStart(), pb.getTimeSlotLimitEnd()).anyMatch(Objects::nonNull)) {
          validateTimeSlotLimitFormatReceived(pb);
      }
    }

    //When the domain setting is set to true then we throw 426 InvalidParameters to prevent Nigeria from registering without providing ResidentialAddress
    //& When the DomainSetting is not set, we ignore & allow registration
    // (Nigeria FE already doesnt allow registering without providing ResidentialAddress - So this will also only allign & disallow Registering without Providing ResidentialAddress when directly using endpoint as-well)
    if (Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(pb.getDomainName(), DomainSettings.RESIDENTIAL_ADDRESS_REQUIRED)) && !isResidentialAddressProvided(pb)) {
      String parameter = "residentialAddress = null";
      throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, pb.getDomainName(), new Object[]{ parameter }));
    }

    // Note the || in the condition below: When the first name or last name is provided on the request while registering a root ecosystem domain user, we still need to validate the first name and last name for invalid characters.
    if (!cachingDomainClientService.isDomainNameOfEcosystemRootType(pb.getDomainName()) || pb.getFirstName() != null || pb.getLastName() != null) {
      if (!validateNamesSymbols(pb.getFirstName()) || !validateNamesSymbols(pb.getLastName())) {
        String parameter = "firstName = " + pb.getFirstName() + " lastName = " + pb.getLastName();
        throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, pb.getDomainName(), new Object[] {parameter}));
      }
    }

    // Only perform the uniqueness checks here if it is not an ecosystem-linked domain (v3 reg takes care of this)
    if (!cachingDomainClientService.isDomainInAnyEcosystem(pb.getDomainName())) {
      if (pb.getUsername() != null && !userService.isUniqueUsername(pb.getDomainName(), pb.getUsername())) {
        throw new Status452UsernameNotUniqueException(RegistrationError.USERNAME_NOT_UNIQUE.getResponseMessageLocal(messageSource, pb.getDomainName()));
      }

      if (!userService.isUniqueEmail(pb.getDomainName(), pb.getEmail()) && !Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(pb.getDomainName(), DomainSettings.ALLOW_DUPLICATE_EMAIL))) {
        throw new Status453EmailNotUniqueException(RegistrationError.EMAIL_NOT_UNIQUE.getResponseMessageLocal(messageSource, pb.getDomainName()));
      }

    }

    boolean allowNotUniqueCellPhoneNumber = false;
    Optional<String> cellPhoneUniqueSetting = cachingDomainClientService.retrieveDomainFromDomainService(domain.getName())
        .findDomainSettingByName(DomainSettings.ALLOW_DUPLICATE_CELLNUMBER.key());
    if (cellPhoneUniqueSetting.isPresent() && cellPhoneUniqueSetting.get().equalsIgnoreCase("true")) {
      allowNotUniqueCellPhoneNumber = true;
    }
    if (!allowNotUniqueCellPhoneNumber) {
      if (!userService.isUniqueMobile(pb.getDomainName(), pb.getCellphoneNumber())) {
        throw new Status454CellphoneNotUniqueException(RegistrationError.CELLPHONE_NOT_UNIQUE.getResponseMessageLocal(messageSource, pb.getDomainName()));
      }
    }

    Address residentialAddress = null;
    if (pb.getResidentialAddress() != null) {
      residentialAddress = validateAndOverrideGeoData(pb.getResidentialAddress(), "residentialAddress", pb.getDomainName());
    }
    Address postalAddress = null;
    if (pb.getPostalAddress() != null) {
      postalAddress = validateAndOverrideGeoData(pb.getPostalAddress(), "postalAddress", pb.getDomainName());
    }

    if (!cachingDomainClientService.isDomainNameOfEcosystemRootType(pb.getDomainName()) && Stream.of(pb.getDobYear(), pb.getDobMonth(), pb.getDobDay()).anyMatch(Objects::isNull)) {
      String parameter = "dobYear = " + pb.getDobYear() + " dobMonth = " + pb.getDobMonth() + " dobDay = " + pb.getDobDay();
      throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, pb.getDomainName(), new Object[] {parameter}));
    }
    if (!dobValid(pb.getDobYear(), pb.getDobMonth(), pb.getDobDay(), pb.getDomainName())) {
      log.debug("Invalid dob supplied in registration");
      throw new Status422InvalidDateOfBirthException(RegistrationError.INVALID_DOB.getResponseMessageLocal(messageSource, pb.getDomainName()));
    }

    Status status = statusRepository.findByName(lithium.service.user.client.enums.Status.OPEN.statusName());
    lithium.service.user.data.entities.StatusReason statusReason = null;
    if (pb.getStatus() != null && pb.getStatus().equals(lithium.service.user.client.enums.Status.BLOCKED)
        && pb.getStatusReason() != null && pb.getStatusReason().equals(lithium.service.user.client.enums.StatusReason.IBAN_MISMATCH)) {
      status = statusRepository.findByName(lithium.service.user.client.enums.Status.BLOCKED.statusName());
      statusReason = statusReasonRepository.findByName(StatusReason.IBAN_MISMATCH.statusReasonName());
    }

    String passwordHash;
    try {
      passwordHash = PasswordHashing.hashPassword(pb.getPassword(), passwordSalt);
    } catch (lithium.exceptions.Status500InternalServerErrorException e) {
      log.error("Password Hash Exception " + ExceptionMessageUtil.allMessages(e), e);
      throw new Status500InternalServerErrorException(RegistrationError.PASSWORD_HASHING.getResponseMessageLocal(messageSource, pb.getDomainName()), e.getStackTrace());
    }

    if (pb.getEmail() != null && !isValidIEmailAddress(pb.getEmail())) {
      log.debug("User register failed, the email(" + pb.getEmail() + ") is incorrect. Received PlayerBasic is:" + pb.toString());
      throw new Status420InvalidEmailException(RegistrationError.INVALID_EMAIL.getResponseMessageLocal(messageSource, pb.getDomainName()));
    }

    if (pb.getCellphoneNumber() != null) {
      try {
        Long.parseLong(pb.getCellphoneNumber());
      } catch (NumberFormatException e) {
        log.debug("User register failed, cellphone number(" + pb.getCellphoneNumber() + ") may only contain numbers. Received PlayerBasic is:" + pb
            .toString());
        throw new Status421InvalidCellphoneException(RegistrationError.INVALID_CELLPHONE.getResponseMessageLocal(messageSource, pb.getDomainName()), e.getStackTrace());
      }
    }

    User user = null;
    try {
      user = userService.buildUser(pb, domain, residentialAddress, postalAddress, status, statusReason, passwordHash);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    try {
      if (playTimeLimitsService.isPlayerTimeFrameLimitsActivatedForDomain(user.getDomain().getName())) {
        if (pb.getTimeSlotLimitStart() != null && pb.getTimeSlotLimitEnd() != null) {
          playTimeLimitsService.createPlayerTimeSlotLimitForUser(user.guid(), user.getId(), pb.getDomainName(), pb.getTimeSlotLimitStart(),
              pb.getTimeSlotLimitEnd());
        }
      } else {
        if (pb.getTimeSlotLimitStart() != null && pb.getTimeSlotLimitEnd() != null) {
          String parameter = "timeSlotLimitStart = " + pb.getTimeSlotLimitStart() + ", timeSlotLimitEnd = " + pb.getTimeSlotLimitEnd();
          log.debug("TimeSlotLimits is not activated on domain but values where supplied on registration as: " + parameter + ", for user: " + user.guid());
        }
      }
    } catch (Exception e) {
      log.error("User registered successfully, but could not save/create timeSlotLimit/timeFrameLimit. for user: " + user.guid());
    }

    if (!playTimeLimitsService.isDepositLimitServiceActivatedForDomain(user.getDomain().getName())) {
      if (pb.getDepositLimitDaily() != null && pb.getDepositLimitWeekly() != null && pb.getDepositLimitMonthly() != null) {
        String parameter = "depositLimitDaily = " + pb.getDepositLimitDaily() + ", depositLimitWeekly = " + pb.getDepositLimitWeekly() + ", depositLimitMonthly = " + pb.getDepositLimitMonthly();
        log.debug("PlayerDepositLimit is not activated on domain but values where supplied on registration as: " + parameter + ", for user: " + user.guid());
      }
    }
    try {
      if (!ObjectUtils.isEmpty(pb.getDepositLimitDaily()) && !pb.getDepositLimitDaily().equals("false")) {
        CurrencyAmount.fromAmountString(pb.getDepositLimitDaily());
        limitService.saveDepositLimit(user.guid(), Granularity.GRANULARITY_DAY.granularity(), pb.getDepositLimitDaily());
      }
    } catch (Exception e) {
      log.error("Could not save daily deposit limit.", e);
    }
    try {
      if (!ObjectUtils.isEmpty(pb.getDepositLimitWeekly()) && !pb.getDepositLimitWeekly().equals("false")) {
        CurrencyAmount.fromAmountString(pb.getDepositLimitWeekly());
        limitService.saveDepositLimit(user.guid(), Granularity.GRANULARITY_WEEK.granularity(), pb.getDepositLimitWeekly());
      }
    } catch (Exception e) {
      log.error("Could not save weekly deposit limit.", e);
    }
    try {
      if (!ObjectUtils.isEmpty(pb.getDepositLimitMonthly()) && !pb.getDepositLimitMonthly().equals("false")) {
        CurrencyAmount.fromAmountString(pb.getDepositLimitMonthly());
        limitService.saveDepositLimit(user.guid(), Granularity.GRANULARITY_MONTH.granularity(), pb.getDepositLimitMonthly());
      }
    } catch (Exception e) {
      log.error("Could not save monthly deposit limits.", e);
    }

    if (!playTimeLimitsService.isBalanceLimitServiceActivatedForDomain(user.getDomain().getName()) && pb.getBalanceLimit() != null) {
      String parameter = "balanceLimit = " + pb.getBalanceLimit();
      log.debug("PlayerBalanceLimit is not activated on domain but value was supplied on registration as: " + parameter + ", for user: " + user.guid());
    }
    try {
      if (pb.getBalanceLimit() != null) {
        limitService.saveBalanceLimit(user.guid(), pb.getBalanceLimit());
      }
    } catch (Exception e) {
      log.error("Could not save balance limits.", e);
    }

    // Perform checks for available age and global loss limits
    try {
      limitService.setPlayerAgeLimit(user);
    } catch (Status479DomainAgeLimitException e) {
      log.error("Could not save player age limit.", e);
    }

    // This will create a UserApiToken and save it to the user account. guid/shortGuid/token in one.
    userApiTokenService.findOrGenerateShortGuid(user.guid());

    if (residentialAddress != null) {
      residentialAddress.setUserId(user.getId());
      residentialAddress = addressRepository.save(residentialAddress);
    }
    if (postalAddress != null) {
      postalAddress.setUserId(user.getId());
      postalAddress = addressRepository.save(postalAddress);
    }

    addCollectionData(pb, user);
    return user;
  }

  private void addCollectionData(PlayerBasic pb, User user) {
    /**
     * Create or update user collection data
     */
    if(!ObjectUtils.isEmpty(pb.getCollectionData()) && pb.getCollectionData().size() > 0) {
      long count = pb.getCollectionData().stream().filter(s -> s.getCollectionName() != null && !ObjectUtils.isEmpty(s.getData()) && s.getData().size() > 0).count();
      if(count > 0L) {
        collectionDataService.createOrUpdateCollectionData(pb, user.getId());
      }
    }
  }

  public Address validateAndOverrideGeoData(AddressBasic addressBasic, String addressType, String domainName)
      throws Status426InvalidParameterProvidedException {
    Address address = modelMapper.map(addressBasic, Address.class);

    if(!ObjectUtils.isEmpty(address.getCountry())){
      return synchronizeAndStoreAddress(addressType, address);
    }

    if(ObjectUtils.isEmpty(address.getCountry()) && !ObjectUtils.isEmpty(address.getCountryCode())){
      try {
        GeoClient geoClient = lithiumServiceClientFactory.target(GeoClient.class, "service-geo", true);
        address.setCountry(geoClient.countries().getData().stream().filter(country->country.getCode().equalsIgnoreCase(address.getCountryCode()))
            .findFirst()
            .orElseThrow(() -> new Status426InvalidParameterProvidedException(RegistrationError.COUNTRY_NOT_PROVIDED.getResponseMessageLocal(messageSource, domainName)))
            .getName());
      } catch (LithiumServiceClientFactoryException e) {
        log.error("Problem getting geo data. " + e.getMessage(), e);
      }
      return synchronizeAndStoreAddress(addressType, address);
    }

    if(ObjectUtils.isEmpty(address.getCountry()) && ObjectUtils.isEmpty(address.getCountryCode())){
      try {
        String countryIso = cachingDomainClientService.getDomainClient().findByName(domainName).getData().getDefaultCountry();
        GeoClient geoClient = lithiumServiceClientFactory.target(GeoClient.class, "service-geo", true);
        address.setCountry(geoClient.countries().getData().stream().filter(country->country.getIso3().equalsIgnoreCase(countryIso))
            .findFirst()
            .orElseThrow(() -> new Status426InvalidParameterProvidedException(RegistrationError.COUNTRY_NOT_PROVIDED.getResponseMessageLocal(messageSource, domainName)))
            .getName());
      } catch (LithiumServiceClientFactoryException e) {
        log.error("Failed to retrieve geoClient" + e.getMessage(), e);
      } catch (Status550ServiceDomainClientException ex){
        log.error("Failed to retrieve domainClient. " + ex.getMessage(), ex);
      }

      return synchronizeAndStoreAddress(addressType, address);
    }

    throw new Status426InvalidParameterProvidedException(RegistrationError.COUNTRY_NOT_PROVIDED.getResponseMessageLocal(messageSource, domainName));
  }

  private Address synchronizeAndStoreAddress(String addressType, Address address) {
    Address synchronizedAddress = synchronizeService.overrideGeoData(address);
    log.debug(addressType + " : " + address);
    return addressRepository.save(synchronizedAddress);
  }

  @TimeThisMethod
  @Transactional(propagation = Propagation.NEVER)
  public User postRegisterPlayer(
    PlayerBasic pb,
    AuthResultContainer arc,
    User user
  ) throws
      Status403AccessDeniedException,
      Status422InvalidDateOfBirthException,
      Status451UnderageException,
      Status461UserNotUniqueException,
      Status550ServiceDomainClientException,
      Status551ServiceAccessClientException {

    lithium.service.domain.client.objects.Domain externalDomain = getExternalDomain(pb.getDomainName());

    // Add affiliate data to playerBasic.additionalData; since it is stored as additional data
    affiliateService.registerAffiliatePlayer(pb, user);
    // Store all additional data provided on playerBasic
    userService.addOrUpdateDomainSpecificUserLabelValues(user, pb.getAdditionalData());

    //Triggering the auto restrictions synchronously so that register bonuses will not be granted before the under 24 restriction
    // is applied on the player's profile (PLAT-5692)
    boolean autoRestrictionCheckSuccessful = limitService.triggerAutoRestrictions(user.guid());

    if (autoRestrictionCheckSuccessful) {
      registerForSignupBonus(
          (pb.getBonusCode() != null) ? pb.getBonusCode() : "",
          user.guid(),
          pb.getBonusId()
      );
    } else if (!autoRestrictionCheckSuccessful && pb.getBonusCode() != null) {
      String comment = String.format("Skipping the granting of signup bonus %s because of a failure while checking restrictions for user %s", pb.getBonusCode(), user.guid());
      changeLogService.registerChangesWithDomainAndFullName("user.bonus", ChangeLogType.COMMENT.name(), user.getId(), lithium.service.user.client.objects.User.SYSTEM_GUID, comment, null, new ArrayList<>(), Category.BONUSES, SubCategory.BONUS_REGISTER, 10,
          user.domainName(), lithium.service.user.client.objects.User.SYSTEM_FULL_NAME);
      log.error(comment);
    }


    if (pb.getReferrerGuid() != null && !pb.getReferrerGuid().isEmpty()) {
      try {
        referralService.addReferral(pb.getReferrerGuid(), user.guid());
      } catch (Exception e) {
        log.error("User registered, but referral service failed: " + e.getMessage(), e);
      }
    }

    addIPOnBlockList(user.getDomain().getName(), externalDomain.getIpblockList(), arc.getIpAndUserAgentData().get(AccessService.MAP_IP));
    String leaderboardPushDomainLinkOptOut = cachingDomainClientService.getDomainSetting(user.getDomain().getName(), DomainSettings.LEADERBOARD_PUSH_DOMAIN_LINK_OPT_OUT);

    try {
      // On auto root ecosystem registration, the status change was also being applied to the root ecosystem
      // domain (LSM), so we are excluding the check that already happend on the mutually exclusive (LSB) domain
      if (!cachingDomainClientService.isDomainNameOfEcosystemRootType(user.domainName())) {
        validateDobAndAge(user, pb);
      }
    } catch (Status451UnderageException uae) {
      // TODO: Add note with comment in the call below. The comment should indicate that the reason is because
      //       the player is underage. I don't want to add another status reason, keeping them to spec.
      user = userService.saveStatus(user.getId(), lithium.service.user.client.enums.Status.BLOCKED.statusName(),
          StatusReason.OTHER.statusReasonName());
      user = userService.saveVerificationStatus(user.getId(), VerificationStatus.UNDERAGED.getId());
      throw uae;
    } finally {
      try {
        String[] chanels = new String[]{};
          if(leaderboardPushDomainLinkOptOut.equalsIgnoreCase("hide")) { // doing this check for LSPLAT-5032, we only want display LeaderboardOptOut and pushOptOut only if is set to show
            chanels = new String[]{
                "guid", "domain", "username", "email", "firstName", "lastNamePrefix", "lastName", "countryCode", "placeOfBirth",
                "telephoneNumber", "cellphoneNumber", "residentialAddress", "postalAddress", "statusReason",
                "createdDate", "updatedDate", "dobYear", "dobMonth", "dobDay", "timezone",
                "callOptOut", "smsOptOut", "emailOptOut", "postOptOut"
            };
          } else {
            chanels = new String[]{
                "guid", "domain", "username", "email", "firstName", "lastNamePrefix", "lastName", "countryCode", "placeOfBirth",
                "telephoneNumber", "cellphoneNumber", "residentialAddress", "postalAddress", "statusReason",
                "createdDate", "updatedDate", "dobYear", "dobMonth", "dobDay", "timezone",
                "callOptOut", "smsOptOut", "emailOptOut", "postOptOut", "leaderboardOptOut", "pushOptOut"
            };
          }
        List<ChangeLogFieldChange> clfc = changeLogService.copy(
            user,
            new User(),
            chanels
        );

        String logStr = "New user created: " + user.getGuid() + " on " + user.getDomain().getName() + " at " + user.getCreatedDate();
        log.info(logStr);
        ChangeLogFieldChange status_clfc = ChangeLogFieldChange.builder().field("Status").fromValue("").toValue(user.getStatus().getName()).build();
        clfc.add(status_clfc);
        changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "create", user.getId(), user.guid(), null, pb.getComments(),
            null, clfc, Category.ACCOUNT, SubCategory.ACCOUNT_CREATION, 0, user.getDomain().getName());

      } catch (Exception e) {
        log.error("User registered, but changelog failed. (" + user + ")", e);
      }
    }

    userService.addToSyncUserAttributesQueue(user);

    /**
     * Perform incomplete user checks for the incomplete user
     */
    Response<ValidatePreRegistrationResponse> response = incompleteUserService.postRegistrationStepsIncompleteUser(pb, user);

    if(!ObjectUtils.isEmpty(response) && !ObjectUtils.isEmpty(response.getData()) && !StringUtil.isEmpty(response.getData().getUserGuid())) {
      user = userService.findByUserGuidAlwaysRefresh(response.getData().getUserGuid());
    }

    try {
      // PlayerBasic will only have an id field with a value if an incomplete user
      // entry was used, thus the version 2 signup process. Sms is default.
      if (pb.getId() != null && pb.getId() > 0 && user.getCellphoneNumber() != null) {
        smsValidationService.sendCellphoneValidationTokenSms(user.getDomain().getName(),
                user.getCellphoneNumber(), false, false);
      } else if (!ObjectUtils.isEmpty(response) && (!ObjectUtils.isEmpty(response.getData()))) {
        emailValidationService.sendEmailValidationTokenEmail(user.getDomain().getName(), user.getEmail(),
                false, false, user.isEmailValidated());
      } else {
        emailValidationService.sendEmailValidationTokenEmail(user.getDomain().getName(), user.getEmail(),
                false, false, false);
      }
    } catch (Exception e) {
      log.error("User registered, sms/emailValidation service failed. (" + user + ")", e);
    }

    try {
      String ipAddress = (arc.getIpAndUserAgentData().get(AccessService.MAP_IP) != null)? arc.getIpAndUserAgentData().get(AccessService.MAP_IP): null;
      String userAgent = (arc.getIpAndUserAgentData().get(AccessService.MAP_USERAGENT) != null)? arc.getIpAndUserAgentData().get(AccessService.MAP_USERAGENT): null;
      streamSuccessfulRegistrationStat(user, ipAddress, userAgent, pb);
    } catch (Exception e) {
      log.error("User registered, but successful registration stat entry failed. (" + user + ")", e);
    }

    try {
      // If something goes wrong here, f.eg svc-changelog is unavailable, we throw an error and rollback in the
      // method below, but the user is still created. When he tries to login, he would still need to accept
      // t's and c's based on the flow.
      TermsAndConditionsVersion termsAndConditionsVersion = userService.acceptTermsAndConditions(
          user.getDomain().getName(), user.guid(), user.guid(), null,
          Locale.forLanguageTag(externalDomain.getDefaultLocale()));

      user.setTermsAndConditionsVersion(termsAndConditionsVersion.getAcceptedUserVersion());
    } catch (Exception e) {
      log.error("User registered. But TC acceptance failed. (" + user + ")", e);
    }

    if (playTimeLimitsService.isPlayTimeLimitServiceActivatedForDomain(user.getDomain().getName())) {
      if (pb.getTimeCap() != null && pb.getTimeCapAmount() != null) {
        playtimeLimitsV2Service.setPlayerConfiguration(
            PlayerPlaytimeLimitConfigRequest.builder()
                .secondsAllocated(pb.getTimeCapAmount() * 60)
                .granularity(playtimeLimitsV2Service.getGranularityType(pb.getTimeCap()))
                .userId(user.getId())
                .build(),
            null
        );
      } else {
        log.debug("Can't register new PlayTimeLimit for user:" + user.getGuid()+ "timeCap or timeCapAmount Not specified in registration request. set a Monthly limit with a limit of 31 days" );
        playtimeLimitsV2Service.setPlayerConfiguration(
            PlayerPlaytimeLimitConfigRequest.builder()
                .secondsAllocated(2628288)
                .granularity(Granularity.GRANULARITY_MONTH.granularity())
                .userId(user.getId())
                .build(),
            null
        );
      }
    } else {
      if (pb.getTimeCap() != null && pb.getTimeCapAmount() != null) {
        String parameter = "TimeCap = " + pb.getTimeCap() + ", TimeCapAmount = " + pb.getTimeCapAmount();
        log.debug("PlayTimeLimit is not activated on domain but values where supplied on registration as: " + parameter + ", for user: " + user.guid());
      }
    }

    try {
      pubSubUserService.buildAndSendPubSubAccountCreate(user, PubSubEventType.ACCOUNT_CREATE);
      PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder pubSubAccountChangeBuilder = PubSubMarketingPreferences.builder();
      pubSubAccountChangeBuilder.callOptOut(user.getCallOptOut());
      if(leaderboardPushDomainLinkOptOut.equalsIgnoreCase("show")) {
        pubSubAccountChangeBuilder.leaderBoardOptOut(user.getLeaderboardOptOut());
        pubSubAccountChangeBuilder.pushOptOut(user.getPushOptOut());
      }
      pubSubAccountChangeBuilder.emailOptOut(user.getEmailOptOut());
      pubSubAccountChangeBuilder.postOptOut(user.getPostOptOut());
      pubSubAccountChangeBuilder.smsOptOut(user.getSmsOptOut());
      pubSubAccountChangeBuilder.accountId(user.getId());
      pubSubAccountChangeBuilder.promotionsOptOut(user.getPromotionsOptOut());
      pubSubUserService.buildAndSendPubSubAccountChangeOpt(user, PubSubEventOrigin.USER, pubSubAccountChangeBuilder, null, PubSubEventType.MARKETING_PREFERENCES);
    } catch (Exception ex) {
      log.error("registerPlayer pubSub message failed for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
    }

    AuthResultContainer arcPostSignup = checkUserAgentValidityPostSignup(
        arc.getIpAndUserAgentData().get(AccessService.MAP_IP),
        arc.getIpAndUserAgentData().get(AccessService.MAP_USERAGENT),
        externalDomain,
        pb.getResidentialAddress(),
        pb.getUsername(),
        pb.getDeviceId());
    if (!arcPostSignup.isSuccesful()) {

      // Check for duplicated user cases
      // if found throw Status461UserNotUniqueException
      final Optional<CheckAuthorizationResult> userDuplicateFound = arcPostSignup.getAuthorizationResult().getRawResults().stream()
          .filter(checkAuthorizationResult ->
              checkAuthorizationResult.getListType().equalsIgnoreCase(DUPLICATE_CHECK.type())
                  && checkAuthorizationResult.isFound())
          .findFirst();
      if (userDuplicateFound.isPresent()) {
        String message = Optional.ofNullable(userDuplicateFound.get().getData())
            .map(data -> data.get("message"))
            .orElse("Account blocked, but reason missed");
        String comment = Optional.ofNullable(userDuplicateFound.get().getData())
            .map(data -> data.get("signupEventComment"))
            .orElse("Account blocked, but reason missed");
        signupEventService
            .saveSignupEvent(arc.getIpAndUserAgentData(), user.getDomain(), comment, user.getId(), false);
        blockDuplicatedUser(user, message);
      }


      // We're checking if the fail response comes from PROVIDER or if it's GENERIC.
      // If the fail comes from PROVIDER we need to show an appropriate message
      // If the fail is GENERIC we need to show the normal 'OTHER' message
      // Start the process with a GENERIC message, and update it to PROVIDER only if a provider has an issue

      String defaultLocale = cachingDomainClientService.domainLocale(user.getDomain().getName());
      String authResultMessage = arcPostSignup.getAuthorizationResult().getMessage();
      String defaultErrorResponse = RegistrationError.POST_SIGNUP_ACCESS_RULE.getResponseMessageLocal(messageSource, pb.getDomainName());
      String statusName = lithium.service.user.client.enums.Status.BLOCKED.statusName();

      StatusReason postArcStatusReason = StatusReason.OTHER;
      String statusReasonName = postArcStatusReason.statusReasonName();
      String statusDescription = postArcStatusReason.description();
      // Get the providerUrl
      // This is the unique property for all External Access Providers, this cannot be modified by the user.
      String accessProviderUrl = arcPostSignup.getAuthorizationResult().getProviderUrl();

      // Technical Debt Task: https://jira.livescore.com/browse/PLAT-1955
      // If the current access rule type providerURL contains "gamstop", update the status
      // this will be refactored once we get a standardised way of checking all Access Rule Providers.
      try {
        if (accessProviderUrl != null && accessProviderUrl.toLowerCase().contains("gamstop")) {
          statusName = lithium.service.user.client.enums.Status.FROZEN.statusName();
          statusReasonName = StatusReason.GAMSTOP_SELF_EXCLUSION.statusReasonName();
          statusDescription = "Gamstop Self-Exclusion";
          defaultErrorResponse = arcPostSignup.getAuthorizationResult().getErrorMessage();

          // Once we change the status reason, we need to mark Has Ever Self-Excluded to true.
          try {
            getUserApiInternalClient().markHasSelfExcludedAndOptOutComms(user.guid());
          } catch (Exception e) {
            log.warn("Player self exclusion set successfully, but failed to mark player has self excluded flag and opt out of comms in " +
                "svc-user | " + e.getMessage() + " [playerGuid="+user.guid()+"]", e);
          }
        }
      } catch (Exception e) {
        String msg = "Failed to get Access Control Rulesest configurations";
        log.debug(msg + " " + e);
      }
      // When other providers start throwing specific issues, we can include them below

      User updatedUser = userService.saveStatus(
          user.getId(),
          statusName,
          statusReasonName);

      try {
        String comment = messageSource.getMessage(authResultMessage, null, Locale.forLanguageTag(defaultLocale));

        List<ChangeLogFieldChange> clfc = changeLogService.compare(
            updatedUser,
            new User(),
            new String[]{
                "guid", "createdDate"
            }
        );
        clfc.addAll(Arrays.asList(ChangeLogFieldChange.builder().field("Status").fromValue(user.getStatus().getName()).toValue(statusName).build(),
            ChangeLogFieldChange.builder().field("statusReason").fromValue("").toValue(statusReasonName).build()));
        changeLogService.registerChangesWithDomain("user.exclusion", "create", user.getId(), user.guid(), null, comment,
            clfc, Category.ACCOUNT, SubCategory.STATUS_CHANGE, 70, user.domainName());
      } catch (Exception e) {
        String msg = "Changelog registration for player status reason failed";
        log.error(msg + " playerGuid="+user.guid() + " " + e.getMessage(), e);
      }
      signupEventService
          .saveSignupEvent(arcPostSignup.getIpAndUserAgentData(), user.getDomain(), arcPostSignup.getAuthorizationResult().getMessage(), user.getId(), false);
      log.error("User registered, but post signup access rules failed. (" + user + ")");
      userService.saveStatusIfUserEnabled(
          user.getId(),
          lithium.service.user.client.enums.Status.BLOCKED.statusName(),
          StatusReason.ACCESS_RULE.statusReasonName());

      // Triggering auto-restrictions on user status changes
      autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder().userGuid(user.guid()).build());

      throw new Status403AccessDeniedException(defaultErrorResponse);
    }

    try {
      signupEventService.saveSignupEvent(arc.getIpAndUserAgentData(), user.getDomain(), null, user.getId(), true);
    } catch (Exception e) {
      log.error("User registered, but signup event failed: " + e.getMessage(), e);
    }

    try {
      overrideMarketingChannelOptOut(user.getGuid(), pb.isChannelsOptOut());
    } catch (Exception e) {
      log.error("Failed to override communication opt out for user " + user.guid(), e);
    }

    if(!cachingDomainClientService.isDomainNameOfEcosystemRootType(user.domainName())) {
      checkForUserDuplicates(user);
    }

    return user;
  }

  private boolean isValidIEmailAddress(String email) {
    Matcher matcher = UserValidatorProperties.EMAIL_PATTERN.matcher(email);
    return matcher.matches();
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

  @TimeThisMethod
  @Transactional(propagation = Propagation.NEVER)
  public OAuth2AccessToken registerV3Player(
      PlayerBasic pb,
      HttpServletRequest request,
      String authorization
  ) throws
      Status403AccessDeniedException,
      Status405UserDisabledException,
      Status407IpBlockedException,
      Status422InvalidDateOfBirthException,
      Status426InvalidParameterProvidedException,
      Status431UserExistsInEcosystemException,
      Status432UserExistsInAnotherExclusiveDomainException,
      Status447AccountFrozenException,
      Status451UnderageException,
      Status452UsernameNotUniqueException,
      Status453EmailNotUniqueException,
      Status454CellphoneNotUniqueException,
      Status460LoginRestrictedException,
      Status461UserNotUniqueException,
      Status490SoftSelfExclusionException,
      Status491PermanentSelfExclusionException,
      Status492ExcessiveFailedLoginBlockException,
      Status496PlayerCoolingOffException,
      Status500InternalServerErrorException,
      Status550ServiceDomainClientException,
      Status500LimitInternalSystemClientException,
      lithium.exceptions.Status500InternalServerErrorException,
      Status551ServiceAccessClientException,
      Status421InvalidCellphoneException,
      Status420InvalidEmailException,
      Status465DomainUnknownCountryException {

    if (!Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(pb.getDomainName(), DomainSettings.ALLOW_REGISTER_V3))) {
      throw new Status403AccessDeniedException(RegistrationError.ACCESS_DENIED.getResponseMessageLocal(messageSource, pb.getDomainName(), new Object[]{DomainSettings.ALLOW_REGISTER_V3.key()}));
    }

    try {
      User user = registerPlayer(pb, request, authorization); // From register V3 flow
      log.debug("Registration successful - Retrieving Authorization Token (" + authorization + "," + user.guid() + ")");
      return getOauth2AccessToken(request, authorization, user, pb);
    } catch (Status401UnAuthorisedException unAuthorisedException) {
      //Swagger documentation for v4 now includes the Status401UnAuthorisedException, but for v3 we are rethrowing the exception as a 500
      throw new Status500InternalServerErrorException(unAuthorisedException.getMessage(), unAuthorisedException.getStackTrace());
    } catch (Status450AccountFrozenSelfExcludedException accountFrozenSelfExcludedException) {
      throw new Status460LoginRestrictedException(accountFrozenSelfExcludedException.getMessage());
    } catch (Status478TimeSlotLimitException e) {
      // Status478TimeSlotLimitException not supported on V3, rethrowing as a 500
      throw new Status500InternalServerErrorException(e.getMessage());
    }
  }

  @TimeThisMethod
  @Transactional(propagation = Propagation.NEVER)
  public User registerPlayer(
      PlayerBasic pb,
      HttpServletRequest request,
      String authorization
  ) throws
      Status401UnAuthorisedException,
      Status403AccessDeniedException,
      Status420InvalidEmailException,
      Status421InvalidCellphoneException,
      Status422InvalidDateOfBirthException,
      Status426InvalidParameterProvidedException,
      Status431UserExistsInEcosystemException,
      Status432UserExistsInAnotherExclusiveDomainException,
      Status451UnderageException,
      Status452UsernameNotUniqueException,
      Status453EmailNotUniqueException,
      Status454CellphoneNotUniqueException,
      Status461UserNotUniqueException,
      Status478TimeSlotLimitException,
      Status500InternalServerErrorException,
      Status550ServiceDomainClientException,
      Status551ServiceAccessClientException {

    try {
      oauthApiInternalClientService.validateClientAuth(authorization);
    } catch (Exception ex) {
      log.debug("Invalidated client auth : " + ex.getMessage());
      throw new Status401UnAuthorisedException(RegistrationError.INVALID_CLIENT_AUTH.getResponseMessageLocal(messageSource, pb.getDomainName()), ex.getStackTrace());
    }

    // Perform checks relevant to the ecosystem requirements
    if (cachingDomainClientService.isDomainInAnyEcosystem(pb.getDomainName())) {
      if(pb.getEmail() != null && !userService.isUniqueEmail(pb.getDomainName(), pb.getEmail())) {
        throw new Status453EmailNotUniqueException(RegistrationError.EMAIL_NOT_UNIQUE.getResponseMessageLocal(messageSource, pb.getDomainName()));
      } else {
        // Perform all the fancy things to determine if a player can be allowed to register based on ecosystem awareness
        ecosystemPreRegistrationValidation(pb);
      }
    }

    return registerPlayer(pb, request); //From V3 & V4 flow
  }

  @Deprecated
  private void checkForUserDuplicates(User user) throws Status550ServiceDomainClientException, Status461UserNotUniqueException {
    boolean isDOBEmpty = user.getDobDay() == null || user.getDobMonth() == null || user.getDobYear() == null;
    if (!isDuplicatedFullNameAllowed(user.domainName()) && !isDOBEmpty) {
      log.debug("Check unique full name & dob enabled for " + user.domainName() + " Start check user=" + user.guid());
      DuplicateCheckRequestData requestData = DuplicateCheckRequestData.builder()
          .domainName(user.domainName())
          .firstName(user.getFirstName())
          .lastName(user.getLastName())
          .dobDay(user.getDobDay())
          .dobMonth(user.getDobMonth())
          .dobYear(user.getDobYear())
          .userOwnerId(user.getId())
          .build();
      List<User> duplicatedUsers = userService.findByDomainNameAndFirstNameAndLastNameAndBirthDayAndIdNot(requestData);
      List<String> duplicatedUsersGuids = duplicatedUsers.stream().map(User::getGuid).collect(Collectors.toList());
//      duplicatedUsers.remove(user);
      if (duplicatedUsers.size() > 0) {
        blockDuplicatedUser(user, "Account is closed due to suspected duplicated accounts:" + duplicatedUsersGuids.stream().collect(
            Collectors.joining(",", "{", "}")));
      }
    }
  }

  private void ecosystemPreRegistrationValidation(PlayerBasic pb)
      throws
      Status431UserExistsInEcosystemException,
      Status432UserExistsInAnotherExclusiveDomainException,
      Status550ServiceDomainClientException,
      Status452UsernameNotUniqueException,
      Status453EmailNotUniqueException, Status426InvalidParameterProvidedException {
    //check un dom only
    //check un eco
    //check if uuid is present and check un match

    // Non ecosystem username email and cellphone checks
    if (pb.getUsername() != null && !userService.isUniqueUsername(pb.getDomainName(), pb.getUsername(), true)) {
      // Username is not unique so we can not continue with registration
      throw new Status452UsernameNotUniqueException(RegistrationError.USERNAME_NOT_UNIQUE.getResponseMessageLocal(messageSource, pb.getDomainName()));
    }

    if (pb.getEmail() != null && !userService.isUniqueEmail(pb.getDomainName(), pb.getEmail(), true)
        && !Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(pb.getDomainName(), DomainSettings.ALLOW_DUPLICATE_EMAIL))) {
      // Email is not unique on the specific domain so we can not continue with registration
      throw new Status453EmailNotUniqueException(RegistrationError.EMAIL_NOT_UNIQUE.getResponseMessageLocal(messageSource, pb.getDomainName()));
    }
    //FIXME: When africa needs to be ecosystem specific
//    if (pb.getCellphoneNumber() != null && !userService.isUniqueCellphone(pb.getDomainName(), pb.getEmail(), true)) {
//      // Email is not unique on the specific domain so we can not continue with registration
//      throw new Status453EmailNotUniqueException();
//    }

    if ((pb.getEmail() != null && !userService.isUniqueEmail(pb.getDomainName(), pb.getEmail(), false)) ||
        (pb.getUsername() != null && !userService.isUniqueUsername(pb.getDomainName(), pb.getUsername(), false)) || pb.getUuid() != null) {
      // Email or username is not unique on ecosystem and not on the specific domain, we need to send a specific error if there is no uuid in pb
      if (!validateUserSignupRequestByUuid(pb)) {
        throw new Status431UserExistsInEcosystemException(RegistrationError.USER_EXIST_IN_ECOSYSTEM_NOT_IN_DOMAIN.getResponseMessageLocal(messageSource, pb.getDomainName()));
      }

      if (!validateUserSignupRequestForMutualExclusivity(
          pb.getDomainName(),
          pb.getEmail() != null ? pb.getEmail() : pb.getCellphoneNumber())) {
        throw new Status432UserExistsInAnotherExclusiveDomainException(RegistrationError.USER_EXIST_IN_ECOSYSTEM_IN_OTHER_DOMAIN.getResponseMessageLocal(messageSource, pb.getDomainName()));
      }
    }
  }

  private boolean isResidentialAddressProvided(PlayerBasic pb) {
    if (!ObjectUtils.isEmpty(pb.getResidentialAddress())) {
      return true;
    }
    return false;
  }

  private boolean isDuplicatedFullNameAllowed(String domainName) throws Status550ServiceDomainClientException {
    lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    Optional<String> domainParameter = domain.findDomainSettingByName(DomainSettings.ALLOW_DUPLICATED_FULL_NAME_AND_DOB.key());
    String bool;
    if (domainParameter.isPresent()) {
      bool = domainParameter.get();
    } else {
      bool = DomainSettings.ALLOW_DUPLICATED_FULL_NAME_AND_DOB.defaultValue();
    }
    return Boolean.valueOf(bool);
  }

  private boolean isDuplicatedEmailAllowed(String domainName) throws Status550ServiceDomainClientException {
    lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    Optional<String> domainParameter = domain.findDomainSettingByName(DomainSettings.ALLOW_DUPLICATE_EMAIL.key());
    String bool;
    if (domainParameter.isPresent()) {
      bool = domainParameter.get();
    } else {
      bool = DomainSettings.ALLOW_DUPLICATE_EMAIL.defaultValue();
    }
    return Boolean.valueOf(bool);
  }

  // returns true if valid
  private boolean validateUserSignupRequestForMutualExclusivity(final String domainName, final String emailOrCell)
      throws
      Status550ServiceDomainClientException {
    ArrayList<EcosystemDomainRelationship> ecosystemDomainRelationships = null;
    try {
      ecosystemDomainRelationships = cachingDomainClientService
          .listEcosystemDomainRelationshipsByDomainName(domainName);
    } catch (Status469InvalidInputException e) {
      log.debug("Fix your code, the domain name input provided is invalid: " + domainName);
    }

    //Get the current domain we want to do signup on, if it is not ME domain, don't bother checking
    Optional<EcosystemDomainRelationship> signupDomain =
        ecosystemDomainRelationships.stream().filter(ecoDomRel -> ecoDomRel.getDomain().getName().equalsIgnoreCase(domainName)).findFirst();
    if (signupDomain.isPresent()) {
      if (!signupDomain.get().getRelationship().getCode().contentEquals(EcosystemRelationshipTypes.ECOSYSTEM_MUTUALLY_EXCLUSIVE.key())) {
        return true;
      }
    }

    // Check for possible mutual exclusivity domain scenario
    Optional<EcosystemDomainRelationship> possibleMutualExclusivityViolation = ecosystemDomainRelationships.stream()
        .filter(ecoDomRel -> {
          if (!ecoDomRel.getDomain().getName().equalsIgnoreCase(domainName)) {
            if (ecoDomRel.getRelationship().getCode().contentEquals(EcosystemRelationshipTypes.ECOSYSTEM_MUTUALLY_EXCLUSIVE.key())) {
              User user = userService.findByUsernameThenEmailThenCell(ecoDomRel.getDomain().getName(), emailOrCell);
              return user != null;
            }
          }
          return false;
        }).findAny();
    return !possibleMutualExclusivityViolation.isPresent();
  }

  /**
   * Evaluating of the UUID passed along with the new registration.<br/> Should any of the fields username, email or cellphone match what is on
   * record, the registration is allowed to proceed<br/> At the point where this is called, checks for uniqueness on the existing domain should
   * already have taken place.
   *
   * @param pb
   * @return true when there was any matching criteria
   */
  private boolean validateUserSignupRequestByUuid(final PlayerBasic pb) throws Status426InvalidParameterProvidedException {
    if (pb.getUuid() != null) {
      UserApiToken userApiToken = userApiTokenService.findByToken(pb.getUuid());
      boolean hadMatchOnUuid = false;

      if(userApiToken == null) throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, pb.getDomainName(), new Object[] {"uuid = " + pb.getUuid()}));
      if(pb.getEmail() == null) throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, pb.getDomainName(), new Object[] {"email = " + pb.getEmail()}));
      if(pb.getPassword() == null) {
        pb.setPassword(PasswordHashing.encodePasswordToSkipHashing(userApiToken.getUser().getPasswordHash()));
      }

      //Email comparison
      if (userApiToken.getUser().getEmail() != null && pb.getEmail() != null) {
        if (userApiToken.getUser().getEmail().equalsIgnoreCase(pb.getEmail())) {
          log.debug("Match on email using uuid: " + userApiToken.getUser().getEmail());
          return true;
        } else {
          log.debug("No match on email using uuid: " + userApiToken.getUser().getEmail() + " incoming: " + pb.getEmail());
        }
      }
      // Cellphone Number & Username is not an ecosystem unique identifier - This will need to be re-looked at a later stage should business need to register a mutual exclusive domain with the UUID using cellphone number or username

      //Cellphone comparison
//      if (userApiToken.getUser().getCellphoneNumber() != null && pb.getCellphoneNumber() != null) {
//        if (userApiToken.getUser().getCellphoneNumber().equalsIgnoreCase(pb.getCellphoneNumber())) {
//          log.debug("Match on cellphone using uuid: " + userApiToken.getUser().getCellphoneNumber());
//          return true;
//        } else {
//          log.debug("No match on cellphone using uuid: " + userApiToken.getUser().getCellphoneNumber() + " incoming: " + pb.getCellphoneNumber());
//        }
//      }

      //Username comparison
//      if (userApiToken.getUser().getUsername() != null && pb.getUsername() != null) {
//        if (userApiToken.getUser().getUsername().equalsIgnoreCase(pb.getUsername())) {
//          log.debug("Match on username using uuid: " + userApiToken.getUser().getUsername());
//          return true;
//        } else {
//          log.debug("No match on username using uuid: " + userApiToken.getUser().getUsername() + " incoming: " + pb.getUsername());
//        }
//      }
      return hadMatchOnUuid;
    } else {
      log.debug("User uuid not present, not validating. " + pb);
    }
    return false;
  }

  private void blockDuplicatedUser(User user, String comment) throws Status461UserNotUniqueException {
    log.warn("Invalid " + user.guid() + " registration. FirstName+LastName+DoB should be unique");
    userService.blockDuplicatedUser(user.getId(), comment);
    log.warn("User " + user.guid() + " was blocked");
    try {
      ResponseEntity<String> response = factory.target(UserPubSubClient.class, true).pushToPubSub(user.guid());
      if (response.getStatusCodeValue() != 200) {
        log.error("Wrong response from pub-sub service for user =[" + user.guid() +"] account change : "+response);
      }
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Cant send pub-sub account change for user =[ " + user.guid() +" ]", e);
    }
    throw new Status461UserNotUniqueException(RegistrationError.USER_NOT_UNIQUE.getResponseMessageLocal(messageSource, user.domainName()));
  }

  private void streamSuccessfulRegistrationStat(User user, String ipAddress, String userAgent, PlayerBasic pb) {
    Map passThroughInfo = new HashMap<>();
    passThroughInfo.put("bonusCode", pb.getBonusCode());
    passThroughInfo.put("referrerGuid", pb.getReferrerGuid());
    statsStream.register(
        QueueStatEntry.builder()
            .type(Type.USER.type())
            .event(Event.REGISTRATION_SUCCESS.event())
            .entry(
                StatEntry.builder()
                    .name(
                        "stats." +
                            "user." +
                            Event.REGISTRATION_SUCCESS.event()
                    )
                    .domain(user.domainName())
                    .ownerGuid(user.guid())
                    .userAgent(userAgent)
                    .ipAddress(ipAddress)
                    .build()
            )
            .passThroughInfo(passThroughInfo)
            .build()
    );
  }

  /**
   * Date of birth is validated if the user date of birth is present and a domain settings attribute is defined in UNA (validateDOB:false) the default
   * is false if no attribute is defined. In addition an attribute can be set containing the minimum age in UNA (minUserAge:18) Enabling this
   * attribute will automatically enforce date of birth validation. The default value is to verify the user age is over 18
   *
   * @param user
   * @param pb
   * @return
   * @throws Status422InvalidDateOfBirthException, Status451UnderageException
   */
  private boolean validateDobAndAge(User user, PlayerBasic pb)
      throws Status422InvalidDateOfBirthException, Status451UnderageException,
      Status550ServiceDomainClientException {
    Integer dobDay = user.getDobDay();
    Integer dobMonth = user.getDobMonth();
    Integer dobYear =  user.getDobYear();
    String domainName = user.getDomain().getName();
    LocalDate dob = null;
    if ((dobYear == null) && (dobMonth == null) && (dobDay == null)) {
      return false;
    }
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.M.d");
    lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    Optional<String> validateDobSetting = domain.findDomainSettingByName("validateDOB");
    Optional<String> minUserAgeSetting = domain.findDomainSettingByName("minUserAge");
    if (!minUserAgeSetting.isPresent()) {
      minUserAgeSetting = Optional.of("18"); // TODO: Would like to not have a default (discuss with Architects)
    }

    if ((validateDobSetting.isPresent() && validateDobSetting.get().equalsIgnoreCase("true"))
        || minUserAgeSetting.isPresent()) {
      try {
        dob = LocalDate.parse(dobYear + "." + dobMonth + "." + dobDay, dateTimeFormatter);
      } catch (DateTimeParseException e) {
        throw new Status422InvalidDateOfBirthException(RegistrationError.INVALID_DOB.getResponseMessageLocal(messageSource, domainName), e.getStackTrace());
      }
    }

    if (minUserAgeSetting.isPresent() && !pb.isUnderAged()) {
      int minAge = Integer.parseInt(minUserAgeSetting.get());
      int userAgeYears = Period.between(dob, LocalDate.now()).getYears();
      boolean underAgeRegistrationEnabled = Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(pb.getDomainName(), DomainSettings.ENABLE_UNDERAGE_REGISTRATION));
      if (minAge > userAgeYears && !underAgeRegistrationEnabled) {
        throw new Status451UnderageException(RegistrationError.USER_UNDERAGE.getResponseMessageLocal(messageSource, domainName));
      }
    }

    if (pb.isUnderAged()) {
      if (!Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(pb.getDomainName(), DomainSettings.ENABLE_UNDERAGE_REGISTRATION))) {
        throw new Status451UnderageException(RegistrationError.USER_UNDERAGE.getResponseMessageLocal(messageSource, domainName));
      }

      userService.saveVerificationStatus(user.getId(), VerificationStatus.UNDERAGED.getId());
      try {
        List<ChangeLogFieldChange> clfc;
        clfc = changeLogService.copy(user, new lithium.service.user.client.objects.User(),
            new String[]{"verificationStatus"});
        changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "create", user.getId(), user.guid(), null, pb.getComments(),
            null, clfc, Category.ACCOUNT, SubCategory.ACCOUNT_CREATION, 0, user.domainName());
      } catch (Exception e) {
        log.error("User registered, but changelog failed. (" + user + ")", e);
      }
    }
    return true;
  }

  public boolean registerForSignupBonus(String bonusCode, String playerGuid, Long bonusId) {
    try {
      CasinoBonusClient casinoBonusClient = factory.target(CasinoBonusClient.class);
      Response<Long> signupBonusResponse;
      if (bonusId != null && bonusId > 0L) {
        signupBonusResponse = casinoBonusClient.registerForSignupBonusById(
            CasinoBonus.builder().bonusId(bonusId).playerGuid(playerGuid).build());
      } else {
        signupBonusResponse = casinoBonusClient.registerForSignupBonus(
            CasinoBonus.builder().bonusCode(bonusCode).playerGuid(playerGuid).build());
      }
      log.debug("signupBonusResponse : " + signupBonusResponse);
      return signupBonusResponse.isSuccessful();
    } catch (Exception e) {
      log.warn("Exception during Bonus registration: '" + e.getMessage() + "' Player Guid: '" + playerGuid + "' ; BonusCode: '" + bonusCode
          + "' ; BonusId: " + bonusId);
      return false;
    }
  }

  public List<lithium.service.user.client.objects.User> getSignupsForDateRange(List<String> domains, DateTime startDate, DateTime endDate) {
    List<User> signupUserList = userService.findByCreatedRange(domains, startDate, endDate);
    List<lithium.service.user.client.objects.User> resultSignupList = new ArrayList<>();

    signupUserList.forEach(user -> {
      lithium.service.user.client.objects.User resultUser = userService.convert(user);

      if (user.getCurrent() != null) {
        List<UserRevisionLabelValue> lvList = user.getCurrent().getLabelValueList();
        if (lvList != null && !lvList.isEmpty()) {
          Map<String, String> lvMap = new HashMap<>();
          for (UserRevisionLabelValue lv : lvList) {
            lvMap.put(lv.getLabelValue().getLabel().getName(), lv.getLabelValue().getValue());
          }
          resultUser.setLabelAndValue(lvMap);
        }
      }

      resultSignupList.add(resultUser);
    });

    return resultSignupList;
  }

  public OAuth2AccessToken registerV4Player(PlayerBasic playerBasic, HttpServletRequest request, String authorization)
      throws Status492ExcessiveFailedLoginBlockException,
      Status432UserExistsInAnotherExclusiveDomainException,
      Status451UnderageException,
      Status461UserNotUniqueException,
      Status500InternalServerErrorException,
      lithium.exceptions.Status500InternalServerErrorException,
      Status491PermanentSelfExclusionException,
      Status421InvalidCellphoneException,
      Status431UserExistsInEcosystemException,
      Status454CellphoneNotUniqueException,
      Status500LimitInternalSystemClientException,
      Status551ServiceAccessClientException,
      Status447AccountFrozenException,
      Status420InvalidEmailException,
      Status460LoginRestrictedException,
      Status453EmailNotUniqueException,
      Status401UnAuthorisedException,
      Status426InvalidParameterProvidedException,
      Status452UsernameNotUniqueException,
      Status405UserDisabledException,
      Status422InvalidDateOfBirthException,
      Status403AccessDeniedException,
      Status407IpBlockedException,
      Status550ServiceDomainClientException,
      Status490SoftSelfExclusionException,
      Status496PlayerCoolingOffException,
      Status423InvalidUsernameException,
      Status424PasswordNotComplexException,
      Status425InvalidLastNamePrefixException,
      Status471BalanceLimitNotProvidedException,
      Status472DepositLimitNotProvidedException,
      Status473TimeLimitNotProvidedException,
      Status450AccountFrozenSelfExcludedException,
      Status465DomainUnknownCountryException,
      Status478TimeSlotLimitException,
      Status463IncompleteUserRegistrationException {

    /**
     * Check to see if the supplied playerBasic object is associated to any incomplete user record and perform data mapping if an association is found
     */
    IncompleteUser incompleteUserFromLabelValue = incompleteUserService.incompleteUserRegistrationChecks(playerBasic);
    incompleteUserPlayerBasicMapping(playerBasic, incompleteUserFromLabelValue);

    lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(playerBasic.getDomainName());
    if (!Boolean.parseBoolean(cachingDomainClientService.getDomainSetting(playerBasic.getDomainName(), DomainSettings.ALLOW_REGISTER_V4))) {
      throw new Status403AccessDeniedException(RegistrationError.ACCESS_DENIED.getResponseMessageLocal(messageSource, playerBasic.getDomainName(), new Object[]{ DomainSettings.ALLOW_REGISTER_V4.key()}));
    }

    //Non root ecosystem domain specific validations
    if (!cachingDomainClientService.isDomainNameOfEcosystemRootType(playerBasic.getDomainName())) {
      if (domain.getPlayerBalanceLimit() && (playerBasic.getBalanceLimit() == null || Double.parseDouble(playerBasic.getBalanceLimit()) < 0)) {
        throw new Status471BalanceLimitNotProvidedException(
            RegistrationError.BALANCE_LIMIT_NOT_PROVIDED.getResponseMessageLocal(messageSource, playerBasic.getDomainName()));
      }
      if (domain.getPlayerDepositLimits())
      {
        if (!Stream.of(playerBasic.getDepositLimitDaily(), playerBasic.getDepositLimitWeekly(), playerBasic.getDepositLimitMonthly()).anyMatch(Objects::nonNull)) {
          throw new Status472DepositLimitNotProvidedException(RegistrationError.DEPOSIT_LIMIT_NOT_PROVIDED.getResponseMessageLocal(messageSource, playerBasic.getDomainName()));
        }
      }

      if (playTimeLimitsService.isPlayTimeLimitServiceActivatedForDomain(domain.getName())) {
        if (!Stream.of(playerBasic.getTimeCapAmount(), playerBasic.getTimeCap()).anyMatch(Objects::nonNull)) {
          throw new Status473TimeLimitNotProvidedException(
              RegistrationError.TIME_LIMIT_NOT_PROVIDED.getResponseMessageLocal(messageSource, playerBasic.getDomainName()));
        }
      }

      if (playerBasic.getUuid() == null) {
        validateUsernameComplexity(playerBasic.getUsername(), playerBasic.getDomainName());
      }
    }

    if (playerBasic.getUuid() == null) {
      validateLastNamePrefix(playerBasic.getLastNamePrefix(), playerBasic.getDomainName());
      validatePasswordComplexity(playerBasic.getUsername(), playerBasic.getDomainName(), playerBasic.getPassword());
    }
    User user = registerPlayer(playerBasic, request, authorization); //From register V4 flow
    log.debug("Registration successful - Retrieving Authorization Token (" + authorization + "," + user.guid() + ")");
    return getOauth2AccessToken(request, authorization, user, playerBasic);
  }

  private void incompleteUserPlayerBasicMapping(PlayerBasic playerBasic, IncompleteUser incompleteUserFromLabelValue) {
    if (incompleteUserFromLabelValue != null) {
      // Email and Cellphone number is allowed to be updated on final registration request
      playerBasic.setCellphoneNumber(playerBasic.getCellphoneNumber() != null ? playerBasic.getCellphoneNumber() : incompleteUserFromLabelValue.getCellphoneNumber());
      playerBasic.setEmail(playerBasic.getEmail() != null ? playerBasic.getEmail() : incompleteUserFromLabelValue.getEmail());

      // The following fields are sourced from iDin by default and can be updatable from player basic if not supplied via iDin
      playerBasic.setLastName(incompleteUserFromLabelValue.getLastName() != null ? incompleteUserFromLabelValue.getLastName() : playerBasic.getLastName());
      playerBasic.setLastNamePrefix(
          incompleteUserFromLabelValue.getLastNamePrefix() != null ? incompleteUserFromLabelValue.getLastNamePrefix() : playerBasic.getLastNamePrefix());
      playerBasic.setDobDay(incompleteUserFromLabelValue.getDobDay() != null ? incompleteUserFromLabelValue.getDobDay() : playerBasic.getDobDay());
      playerBasic.setDobMonth(incompleteUserFromLabelValue.getDobMonth() != null ? incompleteUserFromLabelValue.getDobMonth() : playerBasic.getDobMonth());
      playerBasic.setDobYear(incompleteUserFromLabelValue.getDobYear() != null ? incompleteUserFromLabelValue.getDobYear() : playerBasic.getDobYear());
      playerBasic.setGender(incompleteUserFromLabelValue.getGender() != null ? incompleteUserFromLabelValue.getGender() : playerBasic.getGender());
      if (incompleteUserFromLabelValue.getResidentialAddress() != null && incompleteUserFromLabelValue.getStatus().equals(IncompleteUserStatus.SUCCESS.id())) {
        AddressBasic residentialAddress = modelMapper.map(incompleteUserFromLabelValue.getResidentialAddress(), AddressBasic.class);
        playerBasic.setResidentialAddress(residentialAddress);
      }
    }
  }

  private OAuth2AccessToken getOauth2AccessToken(HttpServletRequest request, String authorization, User user, PlayerBasic playerBasic)
      throws lithium.exceptions.Status500InternalServerErrorException, Status405UserDisabledException, Status491PermanentSelfExclusionException, Status492ExcessiveFailedLoginBlockException,
      Status403AccessDeniedException, Status407IpBlockedException, Status447AccountFrozenException, Status460LoginRestrictedException, Status401UnAuthorisedException,
      Status500LimitInternalSystemClientException, Status490SoftSelfExclusionException, Status496PlayerCoolingOffException, Status450AccountFrozenSelfExcludedException, Status465DomainUnknownCountryException {
    // This is handled in user save in user Service
    // userLinkService.applyEcosystemUserDataSynchronisation(user);
    OAuth2AccessToken token = null;
    try {
      String ipAddress = ipAddress(request);
      String userAgent = userAgent(request);
      token = oauthApiInternalClientService.getToken(ipAddress, userAgent, authorization, user.domainName() + "/" + user.getUsername(),
          playerBasic.getPassword());
    } catch (Status405UserDisabledException userDisabledException) {
      throw new Status405UserDisabledException(LoginError.USER_DISABLED.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), userDisabledException.getStackTrace());

    } catch (Status491PermanentSelfExclusionException permanentSelfExclusionException) {
      throw new Status491PermanentSelfExclusionException(LoginError.PERMANENT_SELF_EXCLUSION.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), permanentSelfExclusionException.getStackTrace());

    } catch (Status492ExcessiveFailedLoginBlockException permanentSelfExclusionException) {
      throw new Status492ExcessiveFailedLoginBlockException(LoginError.EXCESSIVE_FAILED_LOGIN_BLOCK.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), permanentSelfExclusionException.getStackTrace());

    } catch (Status403AccessDeniedException accessDeniedException) {
      throw new Status403AccessDeniedException(LoginError.ACCESS_DENIED.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), accessDeniedException.getStackTrace());

    } catch (Status407IpBlockedException ipBlockedException) {
      throw new Status407IpBlockedException(LoginError.IP_BLOCKED.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), ipBlockedException.getStackTrace());

    } catch (Status447AccountFrozenException accountFrozenException) {
      throw new Status447AccountFrozenException(LoginError.ACCOUNT_FROZEN.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), accountFrozenException.getStackTrace());

    } catch (Status460LoginRestrictedException loginRestrictedException) {
      throw loginRestrictedException;

    } catch (Status450AccountFrozenSelfExcludedException accountFrozenSelfExcludedException) {
       throw accountFrozenSelfExcludedException;

    } catch (Status401UnAuthorisedException unAuthorisedException) {
      throw new Status401UnAuthorisedException(LoginError.UNAUTHORIZED.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), unAuthorisedException.getStackTrace());

    } catch (Status490SoftSelfExclusionException softSelfExclusionException) {
      throw new Status490SoftSelfExclusionException(LoginError.SOFT_SELF_EXCLUSION.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), softSelfExclusionException.getStackTrace());

    } catch (Status496PlayerCoolingOffException playerCoolingOffException) {
      throw new Status496PlayerCoolingOffException(LoginError.FLAGGED_AS_COOLING_OFF.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), playerCoolingOffException.getStackTrace());

    } catch (Status465DomainUnknownCountryException e){
      throw new Status465DomainUnknownCountryException(LoginError.DOMAIN_UNKNOWN_COUNTRY.getResponseMessageLocal(messageSource, playerBasic.getDomainName()));

    } catch (Status500LimitInternalSystemClientException e) {
      throw new Status500LimitInternalSystemClientException(LoginError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), e.getStackTrace());

    } catch (Exception e) {
      log.error("Failed to enhance the OAuth2AccessToken response object | " + e.getMessage(), e);
      throw new lithium.exceptions.Status500InternalServerErrorException(LoginError.INVALID_CLIENT_AUTH.getResponseMessageLocal(messageSource, playerBasic.getDomainName()), e.getStackTrace());
    }
    return token;
  }

  private void validatePasswordComplexity(String username, String domainName, String password) throws Status424PasswordNotComplexException {
    Pattern upperCaseLettersPattern = Pattern.compile("^[A-Z]*$");
    Pattern lowerCaseLettersPattern = Pattern.compile("^[a-z]*$");
    Pattern numbersPattern = Pattern.compile("^[0-9]*$");
    Pattern specialCharactersPattern = Pattern.compile("^[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?`~§±]*$");

    int complexity = 0;
    boolean uppercaseFound = false;
    boolean lowercaseFound = false;
    boolean numberFound = false;
    boolean specialCharactersFound = false;
    for (int i = 0; i < (password == null ? 0 : password.length()); i++) {
      if (complexity >= 3) {
        break;
      }
      if (!uppercaseFound) {
        uppercaseFound = upperCaseLettersPattern.matcher(String.valueOf(password.charAt(i))).find();
        complexity = uppercaseFound ? complexity +1 : complexity;
      }
      if (!lowercaseFound) {
        lowercaseFound = lowerCaseLettersPattern.matcher(String.valueOf(password.charAt(i))).find();
        complexity = lowercaseFound ? complexity + 1 : complexity;
      }
      if (!numberFound) {
        numberFound = numbersPattern.matcher(String.valueOf(password.charAt(i))).find();
        complexity = numberFound ? complexity + 1 : complexity;
      }
      if (!specialCharactersFound) {
        specialCharactersFound = specialCharactersPattern.matcher(String.valueOf(password.charAt(i))).find();
        complexity = specialCharactersFound ? complexity + 1 : complexity;
      }
    }

    if ( complexity < 3 || password == null || password.length() < 8 || password.equalsIgnoreCase(username)) { //validation fails (8+ characters and your password may not be your username and have at least 3 of the following [uppercase letters, lowercase letters, numbers and special characters]
      throw new Status424PasswordNotComplexException(RegistrationError.PASSWORD_NOT_COMPLEX.getResponseMessageLocal(messageSource, domainName));
    }
  }

  private void validateUsernameComplexity(String username, String domainName) throws Status423InvalidUsernameException {
    Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$"); // Only allow letters and numbers
    Matcher matcher = pattern.matcher(String.valueOf(username));
    if (!matcher.find() || username == null || username.length() < 8) { //validation fails (8+ characters and only allowing letters and numbers)
      throw new Status423InvalidUsernameException(RegistrationError.INVALID_USERNAME.getResponseMessageLocal(messageSource, domainName));
    }
  }

  private void validateLastNamePrefix(String lastNamePrefix, String domainName) throws Status425InvalidLastNamePrefixException {
    if (lastNamePrefix != null && !lastNamePrefix.isEmpty()) {
      Pattern regexPattern = Pattern.compile("^[a-zA-Z ]*");
      Matcher matcher = regexPattern.matcher(lastNamePrefix);
      if(!matcher.matches()) {
        throw new Status425InvalidLastNamePrefixException(RegistrationError.INVALID_LASTNAME_PREFIX.getResponseMessageLocal(messageSource, domainName));
      }
    }
  }

  public User createUserOnRootEcosystemDomain(String rootDomainName, User user, AutoRegistration registrationSuccess) throws Exception{
    return createUserOnRootEcosystemDomain(rootDomainName, user, registrationSuccess, false);
  }

  public User createUserOnRootEcosystemDomain(String rootDomainName, User user, AutoRegistration registrationSuccess, boolean singleChannelOptEnabled) throws Exception{
    PlayerBasic pb = PlayerBasic.builder()
        .domainName(rootDomainName)
        .username(user.getUsername())
        .password(registrationSuccess.getPassword())
        .email(user.getEmail())
        .emailValidated(user.isEmailValidated())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .lastNamePrefix(user.getLastNamePrefix())
        .countryCode(user.getCountryCode())
        .placeOfBirth(user.getPlaceOfBirth())
        .telephoneNumber(user.getTelephoneNumber())
        .cellphoneNumber(user.getCellphoneNumber())
        .residentialAddress(user.getResidentialAddress() != null ? modelMapper.map(user.getResidentialAddress(), AddressBasic.class) : null)
        .postalAddress(user.getPostalAddress() != null ? modelMapper.map(user.getPostalAddress(), AddressBasic.class) : null)
        .comments(user.getComments())
        .dobYear(user.getDobYear())
        .dobMonth(user.getDobMonth())
        .dobDay(user.getDobDay())
        .referrerGuid(user.getReferrerGuid())
        .timezone(user.getTimezone())
        .gender(user.getGender())
        .uuid(user.getUserApiToken().getToken())
        .promotionsOptOut(user.getPromotionsOptOut())
        .build();


    //If single channel opt-out/in domain setting is enabled for LSBET domain then sync only email and
    if(singleChannelOptEnabled) {
      pb.setEmailOptOut(registrationSuccess.isChannelOptOut());
      pb.setPushOptOut(registrationSuccess.isChannelOptOut());

      //LS Media only allows Email and push communication, It makes sense to opt out of the other communication channels for now
      //TODO: remove this once all communication channels are supported on LS Media
      pb.setCallOptOut(true);
      pb.setSmsOptOut(true);
      pb.setPostOptOut(true);
      pb.setLeaderboardOptOut(true);
    }
    else {
      //Carry on as normally since domain setting is missing or disabled on LSBET
      pb.setCallOptOut(user.getCallOptOut());
      pb.setSmsOptOut(user.getSmsOptOut());
      pb.setEmailOptOut(user.getEmailOptOut());
      pb.setPostOptOut(user.getPostOptOut());
      pb.setPushOptOut(user.getPushOptOut());
      pb.setLeaderboardOptOut(user.getLeaderboardOptOut());
    }

    lithium.service.domain.client.objects.Domain externalDomain = getExternalDomain(pb.getDomainName());
    AuthResultContainer arc = checkUserAgentValidityPreSignup(registrationSuccess.getIpAddress(),
        registrationSuccess.getUserAgent(),
        externalDomain,
        user.getResidentialAddress() != null ? modelMapper.map(user.getResidentialAddress(), AddressBasic.class) : null,
        user.getUsername(),
        registrationSuccess.getDeviceId(),
        pb);
    
    User registeredUser = registerPlayer(pb, arc); //From register root ecosystem flow
    log.debug("Registered the user on the root domain - registeredUser -> {}", registeredUser);
    return registeredUser;
  }

  private UserApiInternalClient getUserApiInternalClient() throws LithiumServiceClientFactoryException {
    return factory.target(UserApiInternalClient.class,
        "service-user", true);
  }

  /**
   * Allows for single opt out/in to communication channels
   * @param userGuid  the user to update opt out for
   * @param optOut the opt out value (true=optout, false=optin)
   * @throws Status550ServiceDomainClientException
   */
  private void overrideMarketingChannelOptOut(String userGuid, boolean optOut) throws Exception {
    User user = userService.findByUserGuidAlwaysRefresh(userGuid);
    String domainName = user.getDomain().getName();
    //check if domain is in an ecosystem
    if(cachingDomainClientService.isDomainInAnyEcosystem(domainName)) {

      //If it is domain is root only register changelogs
      if(cachingDomainClientService.isDomainNameOfEcosystemRootType(domainName)) {
        String ecosystemName = cachingDomainClientService.getEcosystemNameByDomainName(domainName);
        if(ecosystemName != null) {
          Optional<String> optional = domainService.getRegisteredExclusiveDomainName(ecosystemName, user.getEmail());
          if(optional.isPresent()) {
            String mutuallyExclusiveDomain = optional.get();
            Optional<String> singledOptInSetting = cachingDomainClientService.retrieveDomainFromDomainService(mutuallyExclusiveDomain).findDomainSettingByName(DomainSettings.SINGLE_OPT_ALL_CHANNELS.key());
            if(singledOptInSetting.isPresent() && singledOptInSetting.get().equalsIgnoreCase("true")) {
              registerChangelogsForCommunicationChannels(user, new String[]{"emailOptOut", "pushOptOut"});
            }
          }
        }
      }
      //If it is a bet domain, override the communication channels and register changelogs
      else if(cachingDomainClientService.isDomainNameOfEcosystemMutuallyExclusiveType(domainName)){
        Optional<String> singledOptInSetting = cachingDomainClientService.retrieveDomainFromDomainService(domainName).findDomainSettingByName(DomainSettings.SINGLE_OPT_ALL_CHANNELS.key());
        if (singledOptInSetting.isPresent() && singledOptInSetting.get().equalsIgnoreCase("true")) {
          user.setEmailOptOut(optOut);
          user.setSmsOptOut(optOut);
          user.setCallOptOut(optOut);
          user.setPostOptOut(optOut);
          userService.save(user, true);

          try {
            pubSubUserService.buildAndSendPubSubAccountCreate(user, PubSubEventType.ACCOUNT_CREATE);
          } catch (Exception ex) {
            log.error("pubSub message failed for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
          }

          //THE PO agreed that we need to have a changelog entry for every communication channel (PLAT-3042)
          registerChangelogsForCommunicationChannels(user, new String[]{"callOptOut", "smsOptOut", "emailOptOut", "postOptOut"});
        }
      }
    }
  }

  private void registerChangelogsForCommunicationChannels(User user, String[] channels) throws Exception {
    List<ChangeLogFieldChange> clfcs = changeLogService.copy(
        user,
        new User(),
        channels
    );

    for(ChangeLogFieldChange clfc: clfcs) {
      String type = clfc.getField().substring(0, clfc.getField().indexOf("OptOut"));
      String comment = String.format("You have successfully opted-%s to %s", Boolean.parseBoolean(clfc.getToValue()) ? "out": "in", type);

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "create", user.getId(), user.guid(), null, comment,
          null, Arrays.asList(clfc), Category.ACCOUNT, SubCategory.ACCOUNT_CREATION, 0, user.getDomain().getName());
    }
  }

}
