package lithium.service.user.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status414UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status438PlayTimeLimitConfigurationNotFoundException;
import lithium.exceptions.Status465DomainUnknownCountryException;
import lithium.service.Response;
import lithium.service.access.client.AccessService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.geo.client.GeoClient;
import lithium.service.geo.client.objects.Location;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.objects.LoginEventBO;
import lithium.service.user.client.objects.LoginEventFE;
import lithium.service.user.client.objects.LoginEventQuery;
import lithium.service.user.client.objects.User;
import lithium.service.user.config.ServiceUserConfigurationProperties;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.repositories.LoginEventRepository;
import lithium.service.user.data.repositories.UserRepository;
import lithium.service.user.data.specifications.LoginEventSpecification;
import lithium.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class LoginEventService {
	@Autowired LithiumServiceClientFactory services;
	@Autowired LoginEventRepository loginEventRepository;
	@Autowired ServiceUserConfigurationProperties properties;
	@Autowired ModelMapper mapper;
	@Autowired CachingDomainClientService cachingDomainClientService;
	@Autowired SessionInactivityService sessionInactivityService;
	@Autowired UserRepository userRepository;
	@Autowired MessageSource messageSource;
  @Autowired UserActiveSessionsMetadataService userActiveSessionsMetadataService;

	@Transactional
  @Retryable(backoff = @Backoff(delay = 10, multiplier = 10.0), include = { ObjectOptimisticLockingFailureException.class }, exclude = { Exception.class })
	public LoginEvent saveLoginEvent(
			Map<String, String> ipAndUserAgentData,
			String comment,
			User user,
			Domain domain,
			Boolean successful,
			Boolean internal,
			String providerName,
			String providerUrl,
			Integer errorCode,
			String providerAuthClient
	) throws LithiumServiceClientFactoryException, Status465DomainUnknownCountryException {
		lithium.service.user.data.entities.User userEntity = null;
		if ((user!=null) && (user.getId()!=null)) userEntity = userRepository.findForUpdate(user.getId());
		
		LoginEvent loginEvent = LoginEvent.builder()
				.ipAddress((ipAndUserAgentData.get(AccessService.MAP_IP) != null)? ipAndUserAgentData.get(AccessService.MAP_IP): null)
				.country((ipAndUserAgentData.get(AccessService.MAP_COUNTRY) != null) ? ipAndUserAgentData.get(AccessService.MAP_COUNTRY) : null)
        .countryCode((ipAndUserAgentData.get(AccessService.MAP_COUNTRY_CODE) != null) ? ipAndUserAgentData.get(AccessService.MAP_COUNTRY_CODE) : null)
				.state((ipAndUserAgentData.get(AccessService.MAP_STATE) != null)? ipAndUserAgentData.get(AccessService.MAP_STATE): null)
				.city((ipAndUserAgentData.get(AccessService.MAP_CITY) != null)? ipAndUserAgentData.get(AccessService.MAP_CITY): null)
				.os((ipAndUserAgentData.get(AccessService.MAP_OS) != null)? ipAndUserAgentData.get(AccessService.MAP_OS): null)
				.browser((ipAndUserAgentData.get(AccessService.MAP_BROWSER) != null)? ipAndUserAgentData.get(AccessService.MAP_BROWSER): null)
				.userAgent((ipAndUserAgentData.get(AccessService.MAP_USERAGENT) != null)? ipAndUserAgentData.get(AccessService.MAP_USERAGENT): null)
				.comment(comment)
				.user(userEntity)
				.domain(domain)
				.successful(successful)
				.internal(internal)
				.providerName(providerName)
				.providerUrl(providerUrl)
				.errorCode(errorCode)
				.providerAuthClient(providerAuthClient)
				.sessionKey(UUID.randomUUID().toString())
        .lastActivity(new Date())
				.build();

		loginEvent = loginEventRepository.save(loginEvent);

    if (userEntity!=null) {
      userEntity.setLastLogin(loginEvent);
      userRepository.save(userEntity);
    }

		return loginEvent;
	}

  public String getCountryCodeByIp(String ipv4, String domainName) throws LithiumServiceClientFactoryException, Status465DomainUnknownCountryException {
    GeoClient geoClient = services.target(GeoClient.class, "service-geo", true);
    if (geoClient == null) return null;
    Response<Location> locationResponse = geoClient.location(ipv4);
    log.info("ipAddress: {}, response: {}", ipv4, locationResponse.toString());
    return getCountryCodeFromLocationResponse(locationResponse, domainName);
  }

  private String getCountryCodeFromLocationResponse(Response<Location> locationResponse, String domainName) throws Status465DomainUnknownCountryException {
    if (locationResponse.getData() == null || locationResponse.getData().getCountry() == null || locationResponse.getData().getCountry().getCode() == null) {
      return checkAllowLoginFromUnknownCountryPermission(domainName);
    }
    return locationResponse.getData().getCountry().getCode().toLowerCase();
  }

  private String checkAllowLoginFromUnknownCountryPermission(String domainName) throws Status465DomainUnknownCountryException {
    if (cachingDomainClientService.allowLoginFromUnknownCountry(domainName)) {
      return null;
    }
    log.error("Field \"allow_login_from_unknown_country\" is \"false\", access is forbidden to domain: {}; "
        + "service-geo doesn't return any data about location / country / country code, response.getData(): null", domainName);
    String errorMessage = "You can't login from an unknown country. Please contact customer support.";
    throw new Status465DomainUnknownCountryException(errorMessage);
  }

  /**
	 * Determines if a user has exceeded the configured login block threshold if it exists.
	 *
	 * @param user
	 * @return true when user should be blocked, false if user should not be blocked
	 */
	public boolean checkIfUserShouldBeBlockedForFailedLoginAttempts(User user, boolean onlyCheckExistingBlockStatus) {
		log.debug("Start checkIfUserShouldBeBlockedForFailedLoginAttempts: " + user);
		lithium.service.user.data.entities.User userEntity = null;
		if ((user!=null) && (user.getId()!=null)) {
			userEntity = userRepository.findOne(user.getId());
		}
		log.debug("User entity found in checkIfUserShouldBeBlockedForFailedLoginAttempts: " + userEntity);
		if (userEntity == null) {
			log.debug("Unable to find user to check excessive login block, not blocking in checkIfUserShouldBeBlockedForFailedLoginAttempts: " + user);
			return false;
		}

		if (userEntity.getExcessiveFailedLoginBlock() != null && userEntity.getExcessiveFailedLoginBlock() == true) {
			log.debug("User has been blocked due to excessive logins in checkIfUserShouldBeBlockedForFailedLoginAttempts: " + userEntity);
			return true;
		} else {
			log.debug("User has not been blocked yet, going to check if they should be blocked in checkIfUserShouldBeBlockedForFailedLoginAttempts: " + userEntity);
			if (onlyCheckExistingBlockStatus) {
				log.debug("The checkIfUserShouldBeBlockedForFailedLoginAttempts only checked for current block status, no evaluation is taking place. " + userEntity);
				return false;
			}
		}
		ServiceUserConfigurationProperties.LoginBlockFailure loginBlockFailure = properties.getLoginBlockFailure();

		if (loginBlockFailure.getThreshold() == null ||
			loginBlockFailure.getIntervalMs() == null ||
			loginBlockFailure.getThreshold() <= 0 ||
			loginBlockFailure.getIntervalMs() <= 0) {
			log.debug("No login block properties exist or is invalid in checkIfUserShouldBeBlockedForFailedLoginAttempts: " + userEntity);
			return false;
		} else {
			log.debug("Properties for checkIfUserShouldBeBlockedForFailedLoginAttempts: " + loginBlockFailure);
		}

		DateTime startOfIntervalDate = new DateTime();
		startOfIntervalDate = startOfIntervalDate.minusMillis(loginBlockFailure.getIntervalMs());
		LocalDateTime localDateTimeUTC = startOfIntervalDate.withZone(DateTimeZone.UTC).toLocalDateTime();

		log.debug("Interval start parameter in checkIfUserShouldBeBlockedForFailedLoginAttempts: " + startOfIntervalDate + " toDate: " + startOfIntervalDate.toDate() + " local date utc: "+ localDateTimeUTC.toDate());

		Long failedLoginCount = loginEventRepository
				.countByUserAndSuccessfulAndDateAfter(userEntity, false, localDateTimeUTC.toDate());
		log.debug("Login count returned : " + failedLoginCount + " on user :" + userEntity);
		if (failedLoginCount > loginBlockFailure.getThreshold()) {
			//TODO: Could possibly add a check for a successful login in that timeperiod in here, if one exists, don't lock. That was not in the scope though.
			log.debug("Blocking user flag true for excessive logins in checkIfUserShouldBeBlockedForFailedLoginAttempts : " + userEntity);
			return true;
		} else {
			log.debug("Blocking user flag false for excessive logins in checkIfUserShouldBeBlockedForFailedLoginAttempts : " + userEntity);
			return false;
		}
	}

	public Page<LoginEventFE> mapLoginEventListToLoginEventFullList(Page<LoginEvent> loginEvents) {
		ArrayList<LoginEventFE> loginEventFeList = new ArrayList<>(loginEvents.getSize());
		loginEvents.forEach(event -> {
			LoginEventFE lefe = mapper.map(event, LoginEventFE.class);
			loginEventFeList.add(lefe);
			log.debug("LoginEventFE: "+lefe);
		});
		Page<LoginEventFE> loginEventFullPage =
				new SimplePageImpl<>(loginEventFeList, loginEvents.getNumber(), loginEvents.getSize(), loginEvents.getTotalElements());
		return loginEventFullPage;
	}

	public LoginEvent loginEventBefore(String userGuid, Long id) {
		return loginEventRepository.findTop1ByUserGuidAndIdNotOrderByIdDesc(userGuid, id);
	}

	public LoginEvent loginEventForSessionKey(String sessionKey) {
		return loginEventRepository.findBySessionKey(sessionKey);
	}

  public void validateSession(String domainName, Long loginEventId) throws Status401UnAuthorisedException {
    Long inactiveSession = sessionInactivityService.cacheGet(loginEventId);
    if (inactiveSession != null) {
      throw new Status401UnAuthorisedException(
          messageSource.getMessage("ERROR_DICTIONARY.SESSION.CLOSED",
              new Object[] { new lithium.service.translate.client.objects.Domain(domainName) },
              "Session is closed", LocaleContextHolder.getLocale()));
    } else {
      log.trace("Valid session, loginEventId {} not found in cache", loginEventId);
    }
  }

  private void updateSession(Long loginEventId) {
    LoginEvent session = loginEventRepository.findOne(loginEventId);
    Date previousActivity = new Date(session.getLastActivity().getTime());
    session.setLastActivity(new Date());
    session = loginEventRepository.save(session);
    log.trace("Session updated | previousActivity: {}, lastActivity: {}", previousActivity, session.getLastActivity());
  }

  public void validateAndUpdateSession(String domainName, Long loginEventId) throws Status401UnAuthorisedException {
    validateSession(domainName, loginEventId);
    updateSession(loginEventId);
  }

  /**
   *
   * @param userGuid Required.
   * @param loginEventId Nullable. If not passed, all active sessions will be logged out.
   */
  public void logout(String userGuid, Long loginEventId) {
    Assert.notNull(userGuid, "userGuid must not be null");
    List<LoginEvent> loginEvents = new ArrayList<>();
	  if (loginEventId != null) {
	    LoginEvent loginEvent = loginEventRepository.findOne(loginEventId);
	    if (loginEvent != null) loginEvents.add(loginEvent);
    } else {
	    lithium.service.user.data.entities.User user = userRepository.findByGuid(userGuid);
      loginEvents = loginEventRepository.findByUserAndSuccessfulTrueAndLogoutIsNull(user);
    }
    logout(loginEvents);
  }

  // FIXME: We might want to reconsider out reporting of the logout/session duration in LBO, unless all we care about is the
  //        very last login.
  private void logout(List<LoginEvent> loginEvents) {
    loginEvents.stream().forEach(loginEvent -> {
      Date now = new Date();

      loginEvent.setLogout(new Timestamp(now.getTime()));
      long duration = now.getTime() - loginEvent.getDate().getTime();
      loginEvent.setDuration(duration);
      loginEventRepository.save(loginEvent);

      int sessionTimeoutSeconds = sessionInactivityService.getSessionTimeoutSetting(loginEvent.getDomain().getName());
      int sessionTimeUsedSeconds = DateUtil.secondsBetween(loginEvent.getDate(), new Date());

      if (sessionTimeoutSeconds > sessionTimeUsedSeconds) {
        int ttlSeconds = sessionTimeoutSeconds - sessionTimeUsedSeconds;
        log.trace("sessionTimeoutSeconds: {}, sessionTimeUsedSeconds: {}, ttlSeconds: {}", sessionTimeoutSeconds, sessionTimeUsedSeconds, ttlSeconds);
        sessionInactivityService.cachePut(loginEvent.getId(), ttlSeconds);
      }

      // TODO: This should be transactional
      try {
        userActiveSessionsMetadataService.updateMetadata(loginEvent.getUser().getId(), false);
      } catch (Status414UserNotFoundException | Status426InvalidParameterProvidedException | Status550ServiceDomainClientException |
               Status438PlayTimeLimitConfigurationNotFoundException e) {
        log.error("Could not retrieve domain from domain service | {}", e.getMessage(), e);
      }

    });
  }

  public int getActiveSessionCountForUser(lithium.service.user.data.entities.User user) {
    return loginEventRepository.countByUserAndSuccessfulTrueAndLogoutIsNull(user);
  }

  public LoginEvent findOldestActiveSession(lithium.service.user.data.entities.User user) {
    return loginEventRepository.findTop1ByUserAndSuccessfulTrueAndLogoutIsNullOrderByDateAsc(user);
  }

  public DataTableResponse<LoginEventBO> search(LoginEventQuery loginEventQuery) {
    int size = Optional.of(loginEventQuery.getSize()).orElse(10);
    int page = Optional.ofNullable(loginEventQuery.getPage()).orElse(0);
    Pageable pageable = PageRequest.of(page, size);

    lithium.service.user.data.entities.User user = userRepository.findByGuid(loginEventQuery.getUserGuid());
    if (user == null) {
      throw new Status411UserNotFoundException(messageSource.getMessage("SERVICE_USER.SYSTEM.USERGUID404", null, Locale.getDefault()));
    }

    Specification<LoginEvent> specification = LoginEventSpecification.user(user);

    if (loginEventQuery.getStartDate() != null) {
      specification = specification.and(LoginEventSpecification.loginDateRangeStart(loginEventQuery.getStartDate()));
    }


    if (loginEventQuery.getEndDate() != null) {
      specification = specification.and(LoginEventSpecification.loginDateRangeEnd(loginEventQuery.getEndDate()));
    }

    Page<LoginEvent> loginEventPage = loginEventRepository.findAll(specification, pageable);
    Page<LoginEventBO> loginEventBOPage = loginEventPage.map(event -> mapper.map(event, LoginEventBO.class));

    return new DataTableResponse<LoginEventBO>(new DataTableRequest(), loginEventBOPage);
  }
}
