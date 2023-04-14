package lithium.service.user.services;

import static java.util.Objects.isNull;
import static org.apache.http.client.utils.DateUtils.formatDate;

import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.Granularity;
import lithium.service.datafeed.provider.google.exeptions.PubSubInternalErrorException;
import lithium.service.datafeed.provider.google.objects.DataType;
import lithium.service.datafeed.provider.google.objects.PubSubMessage;
import lithium.service.datafeed.provider.google.service.EnablePubSubExchangeStream;
import lithium.service.datafeed.provider.google.service.ServicePubSubStream;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.LimitType;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.PlayerCoolOff;
import lithium.service.limit.client.objects.PlayerExclusionV2;
import lithium.service.limit.client.objects.PlayerLimit;
import lithium.service.limit.client.objects.Restrictions;
import lithium.service.raf.client.RAFClient;
import lithium.service.raf.client.objects.Referral;
import lithium.service.user.client.objects.Device;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.PubSubAccountChange;
import lithium.service.user.client.objects.PubSubAccountCreate;
import lithium.service.user.client.objects.PubSubEventOrigin;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.PubSubMarketingPreferences;
import lithium.service.user.client.objects.PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder;
import lithium.service.user.client.objects.PubSubObj;
import lithium.service.user.client.objects.PubSubUserLinkChange;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.PlayerPlaytimeLimitV2Config;
import lithium.service.user.data.entities.SignupEvent;
import lithium.service.user.data.entities.StatusReason;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserApiToken;
import lithium.service.user.data.entities.UserLink;
import lithium.service.user.data.repositories.PlayerPlaytimeLimitV2ConfigRepository;
import lithium.service.user.data.schema.DatafeedResendAccountCreateResponse;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lithium.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@EnablePubSubExchangeStream
@AllArgsConstructor
@EnableLimitInternalSystemClient
public class PubSubUserService {

  @Autowired
  LithiumTokenUtilService tokenService;
  @Autowired
  AffiliateService affiliateService;
  @Autowired
  ReferralService referralService;
  @Autowired
  UserService userService;
  @Autowired
  LithiumServiceClientFactory lithiumServiceClientFactory;
  @Autowired
  SignupEventService signupEventService;
  //LSPLAT-5758 Autowired due to circular references
  @Autowired
  PlayerPlaytimeLimitV2ConfigRepository playerPlaytimeLimitV2ConfigRepository;
  @Autowired
  ServiceUserConfigurationProperties userConfigProperties;
  private final LimitService limitService;
  private final LimitInternalSystemService limitInternalSystemService;
  private final ServicePubSubStream servicePubSubStream;
  private final CachingDomainClientService cachingDomainClientService;

  //LSPLAT-5637 PLAT-6344 FIXME need a better way around circular dependencies, for now @Lazy will do just fine
  @Autowired @Lazy private UserLinkService userLinkService;

  private final static long FROZEN_STATUS_ID = 11;

  private boolean isPubSubUserChangeChannelActivated(String domainName) {
    Domain domain = getDomain(domainName);
    if (domain == null) {
      return false;
    }
    Optional<String> labelValue = domain.findDomainSettingByName(DomainSettings.PUB_SUB_USER_CHANGE.key());
    String result = labelValue.orElse(DomainSettings.PUB_SUB_USER_CHANGE.defaultValue());
    return result.equalsIgnoreCase("true");
  }

  private boolean isPubSubUserLinkChannelActivated(String domainName) {
    Domain domain = getDomain(domainName);
    if (domain == null) {
      return false;
    }
    Optional<String> labelValue = domain.findDomainSettingByName(DomainSettings.PUB_SUB_USER_LINK.key());
    String result = labelValue.orElse(DomainSettings.PUB_SUB_USER_LINK.defaultValue());
    return result.equalsIgnoreCase("true");
  }

  private boolean isPubSubMarketingPreferencesActivated(String domainName) {
    Domain domain = getDomain(domainName);
    if(domain == null) {
      return false;
    }
    Optional<String> labelValue = domain.findDomainSettingByName(DomainSettings.PUB_SUB_MARKETING_PREFS.key());
    String result = labelValue.orElse(DomainSettings.PUB_SUB_MARKETING_PREFS.defaultValue());
    return result.equalsIgnoreCase("true");
  }

  private Domain getDomain(String domainName) {
    Domain domain;
    try {
      domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    } catch (Status550ServiceDomainClientException e) {
      log.error("can't find domain from cachingDomainClientService" + e.getMessage());
      return null;
    }
    return domain;
  }

  public void publishAccountChange(User user, Principal principal)
      throws Status500LimitInternalSystemClientException, Status550ServiceDomainClientException {
    publishAccountChange(user, null, principal);
  }

  public void publishAccountChange(User user, UserAccountStatusUpdate accountStatusUpdate, Principal principal)
      throws Status500LimitInternalSystemClientException, Status550ServiceDomainClientException {
    if (!isPubSubUserChangeChannelActivated(user.domainName())) {
      log.warn("pub-sub channel is disabled for domain = " + user.domainName());
      return;
    }

    if (isNull(principal)) {
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
      principal = request.getUserPrincipal();
    }

    if (isNull(principal)) {
      log.warn("pub-sub Service can't init Principal object while try to publish account change request");
      return;
    }

    buildAndSendPubSubAccountChange(user, accountStatusUpdate, principal, PubSubEventType.ACCOUNT_UPDATE);
  }

  public void buildAndSendAccountLinkCreate(UserLink userLink, PubSubEventType pubSubEventType) {
    if (isPubSubUserLinkChannelActivated(userLink.getPrimaryUser().domainName())) {
      PubSubUserLinkChange.PubSubUserLinkChangeBuilder builder = PubSubUserLinkChange.builder();

      builder
          .eventType(pubSubEventType)
          .accountIdA(String.valueOf(userLink.getPrimaryUser().getGuid()))
          .accountIdB(String.valueOf(userLink.getSecondaryUser().getGuid()))
          .linkType(userLink.getUserLinkType().getCode())
          .reason(userLink.getLinkNote());
      try {
        addMessageToAccountLinkQueue(builder.build(), userLink.getPrimaryUser().domainName());
      } catch (PubSubInternalErrorException e) {
        log.error("can not send a pub-sub message to google service" + e.getMessage());
      }
    }
  }

  public void buildAndSendAccountLinkNoteUpdate(UserLink userLink) {
    if (isPubSubUserLinkChannelActivated(userLink.getPrimaryUser().domainName())) {
      PubSubUserLinkChange.PubSubUserLinkChangeBuilder builder = PubSubUserLinkChange.builder();

      builder
          .eventType(PubSubEventType.LINK_NOTE_UPDATE)
          .accountIdA(String.valueOf(userLink.getPrimaryUser().getGuid()))
          .accountIdB(String.valueOf(userLink.getSecondaryUser().getGuid()))
          .linkType(userLink.getUserLinkType().getCode())
          .reason(userLink.getLinkNote());
      try {
        addMessageToAccountLinkQueue(builder.build(), userLink.getPrimaryUser().domainName());
      } catch (PubSubInternalErrorException e) {
        log.error("can not send a pub-sub message to google service" + e.getMessage());
      }
    }
  }

  public void buildAndSendPubSubAccountCreate(User user, PubSubEventType eventType) {
    if (isPubSubUserChangeChannelActivated(user.domainName())) {
      PubSubAccountCreate.PubSubAccountCreateBuilder builder = PubSubAccountCreate.builder();
      builder
          .eventType(eventType)
          .domain(user.domainName())
          .userName(user.getUsername())
          .accountId(String.valueOf(user.getId()))
          .cellphoneNumber(user.getCellphoneNumber())
          .firstName(user.getFirstName())
          .lastName(user.getLastName())
          .DOB(formatDob(user.getDobYear(), user.getDobMonth(), user.getDobDay()))
          .registrationDate(String.valueOf(user.getCreatedDate().getTime()))
          .bonusCode(user.getBonusCode())
          .isEmailValidated(user.isEmailValidated())
          .isCellNumberValidated(user.isCellphoneValidated())
          .isAddressVerified(user.getAddressVerified())
          .build();
      try {
        addMessageToAccountChangeQueue(builder.build(), user.getDomain().getName(), DataType.ACCOUNT_CHANGES);
      } catch (PubSubInternalErrorException e) {
        log.error("Cant send pub-sub AccountCreate message" + e.getMessage());
      }
    }
  }

  public void buildAndSendPubSubAccountChange(User user, Principal principal, PubSubEventType eventType)
      throws Status500LimitInternalSystemClientException, Status550ServiceDomainClientException {
    buildAndSendPubSubAccountChange(user, null, principal, eventType);
  }

  public void buildAndSendPubSubAccountChange(User user, UserAccountStatusUpdate accountStatusUpdate, Principal principal, PubSubEventType eventType)
      throws Status500LimitInternalSystemClientException, Status550ServiceDomainClientException {
    if (!isPubSubUserChangeChannelActivated(user.domainName())){
      return;
    }
    List<PlayerLimit> playerLossLimits = limitService.findPlayerLimit(user.domainName(), user.getGuid(), principal);
    List<PlayerLimit> playerDepositLimits = new ArrayList<>();
    getDepositLimits(playerDepositLimits, user, principal);
    PlayerLimit balanceLimit = limitService.findPlayerLimit(user.domainName(), user.guid(), Granularity.GRANULARITY_TOTAL.granularity(),
        LimitType.TYPE_BALANCE_LIMIT.type(), principal).getData();

    String locale = resolveLocale(user);

    Restrictions restrictions = limitInternalSystemService.getPlayerRestrictions(user.guid(), locale);
    PlayerExclusionV2 playerExclusion = restrictions.getPlayerExclusionV2();
    PlayerCoolOff playerCoolOff = restrictions.getPlayerCoolOff();

    boolean selfExcluded = false;
    boolean selfExclusionPermanent = false;
    String selfExclusionCreated = null;
    String selfExclusionExpiry = null;
    boolean coolOff = false;
    String playerCoolOffCreated = null;
    String playerCoolOffExpiry = null;

    if (accountStatusUpdate != null) {

      selfExcluded = Optional.ofNullable(accountStatusUpdate.getSelfExcluded()).orElse(false);
      selfExclusionPermanent = Optional.ofNullable(accountStatusUpdate.getSelfExclusionPermanent()).orElse(false);
      selfExclusionCreated = accountStatusUpdate.getSelfExclusionCreated();
      selfExclusionExpiry = accountStatusUpdate.getSelfExclusionExpiry();

      coolOff = Optional.ofNullable(accountStatusUpdate.getCoolingOff()).orElse(false);
      playerCoolOffCreated = accountStatusUpdate.getCoolingOffCreated();
      playerCoolOffExpiry = accountStatusUpdate.getCoolingOffExpiry();

    } else {
      if (user.getStatus().getId().equals(FROZEN_STATUS_ID) && playerExclusion != null) {
        selfExcluded = true;
        selfExclusionPermanent = playerExclusion.isPermanent();
        selfExclusionCreated = formatDate(playerExclusion.getCreatedDate());
        selfExclusionExpiry = playerExclusion.getExpiryDate() != null ? formatDate(playerExclusion.getExpiryDate()) : null;
      }
      if (user.getStatus().getId().equals(FROZEN_STATUS_ID) && playerCoolOff != null) {
        coolOff = true;
        playerCoolOffCreated = formatDate(playerCoolOff.getCreatedDate());
        playerCoolOffExpiry = playerCoolOff.getExpiryDate() != null ? formatDate(playerCoolOff.getExpiryDate()) : null;
      }
    }

    Long playTimeLimitGranularity = null;
    Long playTimeLimitTime = null;
    Optional<PlayerPlaytimeLimitV2Config> playerPlaytimeLimitV2Config = playerPlaytimeLimitV2ConfigRepository.findByUser_Guid(user.guid());

    if (playerPlaytimeLimitV2Config.isPresent()) {
      playTimeLimitGranularity = playerPlaytimeLimitV2Config.get().getCurrentConfigRevision().getGranularity().getId();
      playTimeLimitTime = playerPlaytimeLimitV2Config.get().getCurrentConfigRevision().getSecondsAllocated();
    }

    PubSubMarketingPreferences marketingPreferences;
    PubSubMarketingPreferences.PubSubMarketingPreferencesBuilder marketingPreferencesBuilder = PubSubMarketingPreferences.builder();

    Access access = limitInternalSystemService.checkAccess(user.getGuid());
    User linkedUser = userLinkService.getLinkedEcosystemUser(user);
    HashMap<String, String> affiliatePlayerLabelValueMap = affiliateService.getAffiliateLabelValues(user);
    String referralUserName = resolveReferralUserName(user);
    String referralCode = resolveReferralCode(user.getUserApiToken());
    boolean converted = resolveConverted(user);
    Device device = getSignUpDevice(user);

    PubSubAccountChange pubSubAccountChange = PubSubAccountChange.builder()
        .eventType(eventType)
        .accountId(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .addressLine1(Optional.ofNullable(user.getResidentialAddress()).map(Address::getAddressLine1).orElse(null))
        .city(Optional.ofNullable(user.getResidentialAddress()).map(Address::getCity).orElse(null))
        .country(Optional.ofNullable(user.getResidentialAddress()).map(Address::getCountry).orElse(null))
        .countryCode(Optional.ofNullable(user.getResidentialAddress()).map(Address::getCountryCode).orElse(null))
        .postalCode(Optional.ofNullable(user.getResidentialAddress()).map(Address::getPostalCode).orElse(null))
        .DOB(formatDob(user.getDobYear(), user.getDobMonth(), user.getDobDay()))
        .registrationDate(String.valueOf(user.getCreatedDate().getTime()))
        .lossLimitDaily(getDailyLossLimit(playerLossLimits))
        .lossLimitWeekly(getWeeklyLossLimit(playerLossLimits))
        .lossLimitMonthly(getMonthlyLossLimit(playerLossLimits))
        .dailyDepositLimit(getDepositLimitByGranularity(playerDepositLimits, Granularity.GRANULARITY_DAY.granularity()))
        .weeklyDepositLimit(getDepositLimitByGranularity(playerDepositLimits, Granularity.GRANULARITY_WEEK.granularity()))
        .monthlyDepositLimit(getDepositLimitByGranularity(playerDepositLimits, Granularity.GRANULARITY_MONTH.granularity()))
        .balanceLimit(Optional.ofNullable(balanceLimit).map(PlayerLimit::getAmount).orElse(null))
        .depositBlock(access.isDepositAllowed())
        .status(user.getStatus().getName())
        .verificationStatus(user.getVerificationStatus().toString())
        .biometricsStatus(user.getBiometricsStatus().getValue())
        .closureReason(Optional.ofNullable(user.getStatusReason()).map(StatusReason::getName).orElse(null))
        .domain(user.domainName())
        .gender(user.getGender())
        .cellphoneNumber(user.getCellphoneNumber())
        .selfExcluded(selfExcluded)
        .selfExclusionPermanent(selfExclusionPermanent)
        .selfExclusionCreated(selfExclusionCreated)
        .selfExclusionExpiry(selfExclusionExpiry)
        .coolingOff(coolOff)
        .coolingOffCreated(playerCoolOffCreated)
        .coolingOffExpiry(playerCoolOffExpiry)
        .playTimeLimitGranularity(playTimeLimitGranularity)
        .playTimeLimitTime(playTimeLimitTime)
        .isEmailValidated(user.isEmailValidated())
        .contraAccountSet(limitInternalSystemService.isContraAccountSet(user.guid()))
        .isTestAccount(user.getTestAccount())
        .externalUserId(null) //PLAT-6344/LSPLAT-5637 - we can leave external user id out of the messages for now
        .kycVerificationName(limitInternalSystemService.getVerificationStatusCode(user.getVerificationStatus()))
        .linkedUserId(Optional.ofNullable (linkedUser).map(User::getId).orElse(null))
        .isCasinoBonusAllowed(access.isCompsAllowed())
        .isCasinoBlocked(!access.isCasinoAllowed())
        .isSportsBookBlocked(!access.isBetPlacementAllowed())
        .isLoginBlocked(!access.isLoginAllowed())
        .isCompedBlocked(!access.isCompsAllowed())
        .isWithdrawalBlocked(!access.isWithdrawAllowed())
        .affiliateGuid(affiliatePlayerLabelValueMap.get(Label.AFFILIATE_GUID_LABEL))
        .affiliateSecondaryGuid1(affiliatePlayerLabelValueMap.get(Label.AFFILIATE_SECONDARY_GUID_1_LABEL))
        .affiliateSecondaryGuid2(affiliatePlayerLabelValueMap.get(Label.AFFILIATE_SECONDARY_GUID_2_LABEL))
        .affiliateSecondaryGuid3(affiliatePlayerLabelValueMap.get(Label.AFFILIATE_SECONDARY_GUID_3_LABEL))
        .referrerGuid(Optional.ofNullable(user.getReferrerGuid()).filter(Predicate.not(String::isEmpty)).orElse(null))
        .referrer(referralUserName)
        .referralCode(referralCode)
        .converted(converted)
        .signupDevice(device)
        .build();

    marketingPreferencesBuilder
        .eventType(PubSubEventType.MARKETING_PREFERENCES)
        .accountId(user.getId())
        .domain(user.domainName())
        .guid(user.guid())
        .callOptOut(user.getCallOptOut())
        .leaderBoardOptOut(user.getLeaderboardOptOut())
        .smsOptOut(user.getSmsOptOut())
        .emailOptOut(user.getEmailOptOut())
        .pushOptOut(user.getPushOptOut())
        .promotionsOptOut(user.getPromotionsOptOut());
    marketingPreferences = marketingPreferencesBuilder.build();

    log.debug("pubSubAccountChange object built " + pubSubAccountChange.toString());
    try {
      if (isPubSubMarketingPreferencesActivated(user.domainName())) {
        addMessageToMarketingPreferences(marketingPreferences, user.getDomain().getName());
      }
      addMessageToAccountChangeQueue(pubSubAccountChange, user.getDomain().getName(), DataType.ACCOUNT_CHANGES);
    } catch (PubSubInternalErrorException e) {
      log.error("can not send a pub-sub message to google service" + e.getMessage());
    }
  }

  private String resolveReferralCode(UserApiToken userApiToken) {
    return Optional.ofNullable(userApiToken)
        .map(UserApiToken::getShortGuid)
        .filter(Predicate.not(String::isEmpty))
        .orElse(null);
  }

  private boolean resolveConverted(User user) {
    if (StringUtil.isEmpty(user.getReferrerGuid()) || !userConfigProperties.getReferralFlag().isEnabled()) {
      return false;
    }
    Referral referral = getRAFClient().map(rafClient -> rafClient.findByPlayerGuid(user.getDomain().getName(), user.getReferrerGuid()).getData())
        .orElse(null);
    if (referral != null) {
      return BooleanUtils.isTrue(referral.getConverted());
    }
    return false;
  }

  private String resolveReferralUserName(User user) {
    if (StringUtil.isEmpty(user.getReferrerGuid())) {
      return null;
    }
    User referrerUser = userService.findFromGuid(user.getReferrerGuid());
    if (referrerUser != null && !StringUtil.isEmpty(referrerUser.getUsername())) {
      return referrerUser.getUsername();
    }
    return null;
  }

  private String resolveLocale(User user) {
    try {
      return cachingDomainClientService.getDomainClient().findByName(user.domainName()).getData().getDefaultLocale().replace("-", "_");
    } catch (Status550ServiceDomainClientException e) {
      log.error("pub-sub Failed to retrieve domainClient ");
      throw e;
    }
  }

  private Device getSignUpDevice(User user) {
    Device device = null;
    String pattern  = "dd/MM/yyyy";
    List domainList = new ArrayList();
    domainList.add(user.getDomain());
    DateFormat formatter = new SimpleDateFormat(pattern);
    Date date;
    try {
      date = formatter.parse(formatter.format(user.getCreatedDate()));
      LocalDate parse = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      Page<SignupEvent> page = signupEventService.find(domainList, user.getId(), parse.minusDays(1).format(DateTimeFormatter.ofPattern(pattern)),
          parse.plusDays(1L).format(DateTimeFormatter.ofPattern(pattern)), true, null, PageRequest.of(0, 1));
      Optional<SignupEvent> first = page.stream().findFirst();
      if(first.isPresent()) {
        SignupEvent signupEvent = first.get();
        device = Device.builder()
            .os(signupEvent.getOs())
            .browser(signupEvent.getBrowser())
            .ipAddress(signupEvent.getIpAddress())
            .userAgent(signupEvent.getUserAgent())
            .build();
      }
    } catch (ParseException ex) {
      log.warn("Failed to create object for SignUpDevice : " + ex);
    }
    return device;
  }

  public void buildAndSendPubSubAccountChangeOpt(
      User user,
      PubSubEventOrigin pubSubEventOrigin,
      PubSubMarketingPreferencesBuilder builder,
      LithiumTokenUtil util,
      PubSubEventType pubSubEventType
  ) {
    if (isPubSubMarketingPreferencesActivated(user.domainName())) {
      PubSubMarketingPreferences pubSubMarketingPreferences = null;
      try {
        builder.eventType(pubSubEventType);
        builder.accountId(user.getId());
        builder.guid(user.guid());
        builder.domain(user.getDomain().getName());
        builder.origin(pubSubEventOrigin.toString());
        pubSubMarketingPreferences = builder.build();
        log.debug("buildAndSendPubSubAccountChangeOpt object built " + pubSubMarketingPreferences.toString());
      } catch (Exception e) {
        log.error("PubSubMarketingPreferences failed " + e.getMessage(), e);
      }

      try {
        if (pubSubMarketingPreferences != null && isPubSubMarketingPreferencesActivated(user.getDomain().getName())) {
          addMessageToMarketingPreferences(pubSubMarketingPreferences, user.getDomain().getName());
        }
      } catch (Exception ex) {
        log.error("addMessageToMarketingPreferences failed for : " + user.getGuid() + " :: " + ex.getMessage(), ex);
      }
    }
  }

  private String formatDob(Integer year, Integer month, Integer day) {
    if (year == null || month == null || day == null) {
      return "Dob not set";
    }
    String monthS = month < 10 ? String.format("%02d", month) : month.toString();
    String dayS = day < 10 ? String.format("%02d", day) : day.toString();
    return year.toString() + "-" + monthS + "-" + dayS;
  }

  private List<PlayerLimit> getDepositLimits(List<PlayerLimit> playerDepositLimits, User user, Principal principal) {
    Response<PlayerLimit> dailyLimit = limitService
        .findPlayerLimit(user.domainName(), user.guid(), Granularity.GRANULARITY_DAY.granularity(), 3, principal);
    Response<PlayerLimit> weeklyLimit = limitService
        .findPlayerLimit(user.domainName(), user.guid(), Granularity.GRANULARITY_WEEK.granularity(), 3, principal);
    Response<PlayerLimit> monthlyLimit = limitService
        .findPlayerLimit(user.domainName(), user.guid(), Granularity.GRANULARITY_MONTH.granularity(), 3, principal);
    if (dailyLimit.isSuccessful()) {
      playerDepositLimits.add(dailyLimit.getData());
    }
    if (weeklyLimit.isSuccessful()) {
      playerDepositLimits.add(weeklyLimit.getData());
    }
    if (monthlyLimit.isSuccessful()) {
      playerDepositLimits.add(monthlyLimit.getData());
    }
    return playerDepositLimits;
  }

  private long getMonthlyLossLimit(List<PlayerLimit> playerLimits) {
    return playerLimits.stream()
        .filter(playerLimit -> playerLimit != null && playerLimit.getType() == 2 && playerLimit.getGranularity() == Granularity.GRANULARITY_MONTH
            .granularity())
        .map(PlayerLimit::getAmount)
        .findFirst()
        .orElse(0L);
  }

  private long getWeeklyLossLimit(List<PlayerLimit> playerLimits) {
    return playerLimits.stream()
        .filter(playerLimit -> playerLimit != null && playerLimit.getType() == 2 && playerLimit.getGranularity() == Granularity.GRANULARITY_WEEK
            .granularity())
        .map(PlayerLimit::getAmount)
        .findFirst()
        .orElse(0L);
  }

  private long getDailyLossLimit(List<PlayerLimit> playerLimits) {
    return playerLimits.stream()
        .filter(playerLimit -> playerLimit != null && playerLimit.getType() == 2 && playerLimit.getGranularity() == Granularity.GRANULARITY_DAY
            .granularity())
        .map(PlayerLimit::getAmount)
        .findFirst()
        .orElse(0L);
  }

  private long getDepositLimitByGranularity(List<PlayerLimit> playerLimits, int granularity) {
    return playerLimits.stream()
        .filter(limit -> limit != null && limit.getType() == 3 && limit.getGranularity() == granularity)
        .map(PlayerLimit::getAmount)
        .findFirst()
        .orElse(0L);
  }

  private void addMessageToAccountChangeQueue(PubSubObj change, String domainName, DataType dataType) throws PubSubInternalErrorException {
    log.debug("AccountChangeQueue (PubSub)(" + domainName + "): " + change);
    servicePubSubStream.processUserChange(
        PubSubMessage
            .builder()
            .timestamp(new Date().getTime())
            .data(change)
            .dataType(dataType)
            .eventType(change.getEventType() != null ? change.getEventType().name() : null)
            .domainName(domainName)
            .build()
    );
  }

  private void addMessageToAccountLinkQueue(PubSubObj change, String domainName) throws PubSubInternalErrorException {
    servicePubSubStream.processAccountLinkChange(
        PubSubMessage
            .builder()
            .timestamp(new Date().getTime())
            .data(change)
            .dataType(DataType.ACCOUNT_LINK_CHANGES)
            .eventType(change.getEventType().name())
            .domainName(domainName)
            .build()
    );
  }

  private void addMessageToMarketingPreferences(PubSubObj message, String domainName) throws PubSubInternalErrorException {
    servicePubSubStream.processMarketingPreferences(
        PubSubMessage.builder()
            .timestamp(new Date().getTime())
            .data(message)
            .dataType(DataType.MARKETING_PREFERENCES)
            .eventType(message.getEventType().name())
            .domainName(domainName)
            .build()
    );
  }

  public DatafeedResendAccountCreateResponse getDatafeedResendAccountResponse(String guid, User user, String error) {
    return DatafeedResendAccountCreateResponse.builder().guid(guid).user(user).error(error).build();
  }

  public Optional<RAFClient> getRAFClient() {
    try {
      return Optional.ofNullable(lithiumServiceClientFactory.target(RAFClient.class, "service-raf", true));
    } catch (LithiumServiceClientFactoryException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
}
