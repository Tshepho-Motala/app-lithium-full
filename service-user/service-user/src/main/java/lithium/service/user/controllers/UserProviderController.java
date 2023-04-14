package lithium.service.user.controllers;

import static java.util.Objects.nonNull;
import static lithium.service.Response.Status.CUSTOM;
import static lithium.service.Response.Status.DOMAIN_UNKNOWN_COUNTRY_EXCEPTION;
import static lithium.service.Response.Status.FAILED_LOGIN_BLOCK;
import static lithium.service.Response.Status.FORBIDDEN;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.LOGIN_RESTRICTED;
import static lithium.service.Response.Status.OK;
import static lithium.service.UserGuidStrategy.USERNAME;
import static lithium.service.access.client.AccessService.MAP_IP;
import static lithium.service.access.client.AccessService.MAP_USERAGENT;
import static lithium.services.LithiumServiceApplication.GUID_STRATEGY;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status465DomainUnknownCountryException;
import lithium.metrics.LithiumMetricsService;
import lithium.service.Response;
import lithium.service.access.client.AccessService;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.promo.client.stream.MissionStatsStream;
import lithium.service.stats.client.enums.Event;
import lithium.service.stats.client.objects.StatEntry;
import lithium.service.stats.client.stream.QueueStatEntry;
import lithium.service.stats.client.stream.StatsStream;
import lithium.service.translate.client.objects.LoginError;
import lithium.service.translate.client.objects.Module;
import lithium.service.user.client.UserClient;
import lithium.service.user.client.enums.Status;
import lithium.service.user.client.objects.AccountStatusErrorCodeAndMessage;
import lithium.service.user.client.objects.Address;
import lithium.service.user.client.objects.AuthRequest;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.User;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.entities.SignupEvent;
import lithium.service.user.services.DomainService;
import lithium.service.user.services.ExclusionCheckService;
import lithium.service.user.services.LoginEventService;
import lithium.service.user.services.PasswordResetService;
import lithium.service.user.services.SessionInactivityService;
import lithium.service.user.services.SignupEventService;
import lithium.service.user.services.UserActiveSessionsMetadataService;
import lithium.service.user.services.UserApiTokenService;
import lithium.service.user.services.UserLinkService;
import lithium.service.user.services.UserProviderService;
import lithium.service.user.services.UserService;
import lithium.service.user.services.notify.KeepFailLoginAttemptsService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.Hash;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserProviderController {

	@Autowired UserProviderService userProviderService;
	@Autowired CachingDomainClientService cachingDomainClientService;
	@Autowired DomainService domainService;
	@Autowired LithiumServiceClientFactory serviceFactory;
	@Autowired ModelMapper modelMapper;
	@Autowired UserApiTokenService userApiTokenService;
	@Autowired LithiumMetricsService metrics;
	@Autowired AccessService accessService;
	@Autowired LoginEventService loginEventService;
	@Autowired LimitInternalSystemService limits;
	@Autowired SignupEventService signupEventService;
	@Autowired StatsStream statsStream;
	@Autowired MissionStatsStream missionStatsStream;
	@Autowired UserService userService;
	@Autowired ProviderClientService providerClientService;
	@Autowired PasswordResetService passwordResetService;
	@Autowired KeepFailLoginAttemptsService keepFailLoginAttemptsService;
	@Autowired UserLinkService userLinkService;
	@Autowired SessionInactivityService sessionInactivityService;
	@Autowired MessageSource messageSource;
  @Autowired LocaleContextProcessor localeContextProcessor;
  @Autowired UserActiveSessionsMetadataService userActiveSessionsMetadataService;

	@Setter
	@Autowired
  private ExclusionCheckService exclusionCheckService;

//TODO: This class needs a serious rework, will tackle it as soon as time permits
	private Domain domain(String domainName) throws Exception {
		return domainService.findOrCreate(domainName);
	}
	
	private List<Provider> providersAuth(String domainName) throws Exception {
		return providerClientService.providers(domainName, ProviderType.AUTH);
	}
	private List<Provider> providersUser(String domainName) throws Exception {
		return providerClientService.providers(domainName, ProviderType.USER);
	}
//	private List<Provider> providers(String domainName, ProviderType providerType) throws Exception {
//		ProviderClient providerClient = serviceFactory.target(ProviderClient.class, true);
//		Response<Iterable<Provider>> response = providerClient.listByDomainAndType(domainName, providerType.type());
//		List<Provider> providers = new ArrayList<>();
//
//		if (response.isSuccessful()) {
//			response.getData().forEach(providers::add);
//			providers.removeIf(p -> p.getEnabled() == false);
//			providers.sort(Comparator.comparingInt(Provider::getPriority));
//
//		}
//
//		if (providers.size() == 0) {
//			Provider provider = Provider.builder().name("provider-internal").url("internal").build();
//			provider.providerType(providerType.type());
//			providers.add(provider);
//		}
//
//		return providers;
//	}

    private Response<User> saveLocalUser(Response<User> responseEntity, String domain, String username) throws Exception {
        lithium.service.user.data.entities.User userTarget = userService.findByUsernameThenEmailThenCell(domain, username);
        Long userId = (userTarget != null) ? userTarget.getId() : null;
        Date createdDate = (userTarget != null) ? userTarget.getCreatedDate() : new Date();
        userTarget = modelMapper.map(responseEntity.getData(), lithium.service.user.data.entities.User.class);
        userTarget.setDomain(domain(domain));
        userTarget.setId(userId);
        userTarget.setCreatedDate(createdDate);
        userTarget.setStatus(userService.findStatus(Status.OPEN.statusName()));
        if (userTarget.getPostalAddress() != null)
            userTarget.setPostalAddress(userProviderService.saveAddress(userTarget.getPostalAddress()));
        if (userTarget.getResidentialAddress() != null)
            userTarget.setResidentialAddress(userProviderService.saveAddress(userTarget.getResidentialAddress()));
        userTarget = userProviderService.save(userTarget);
        User clientUser = modelMapper.map(userTarget, User.class);
        populateDomain(responseEntity.getData());

        //Add back missing fields from client user object received.
        clientUser.setApiToken(responseEntity.getData().getApiToken());
        clientUser.setLabels(responseEntity.getData().getLabels());
        clientUser.setLabelAndValue(responseEntity.getData().getLabelAndValue());
        clientUser.setAcceptTerms(responseEntity.getData().isAcceptTerms());
        clientUser.setOptIn(responseEntity.getData().isOptIn());
        clientUser.setCompanyName(responseEntity.getData().getCompanyName());
        clientUser.setWebsiteURL(responseEntity.getData().getWebsiteURL());
        clientUser.setPaymentDetails(responseEntity.getData().getPaymentDetails());
        clientUser.setLastLogin(responseEntity.getData().getLastLogin());

        responseEntity.setData(clientUser);

        // This is handled in UserService
 //       userLinkService.applyEcosystemUserDataSynchronisation(userTarget);
        return responseEntity;
    }

    private Map<String, String> ipAndUserAgentData(String ipAddress, String userAgent) {
        try {
            return accessService.parseIpAndUserAgent(ipAddress, userAgent);
        } catch (Throwable e) {
            log.error(e.getMessage());
            return Collections.emptyMap();
        }
    }

    @SneakyThrows
    private String encryptOrigin(String key, String value) {
      return Hash.builder(key, value).hmacSha256();
    }

    @RequestMapping("/users/auth")
    public Response<User> auth(
            @RequestParam("domain") String domainNameOrEcosystemName,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("ipAddress") String ipAddress,
            @RequestParam("userAgent") String userAgent,
            @RequestParam(required = false) Map<String, String> extraParameters,
            @RequestParam("locale") String locale
    ) throws Exception {
        String domainName;
        try {
          domainName = domainService.domainResolver(domainNameOrEcosystemName, username);
        } catch (Exception e) {
          log.warn("Failed to resolve domain or ecosystem name used in combination with username = " + domainNameOrEcosystemName + "/" + username + " and ip address = " + ipAddress);
          return Response.<User>builder()
              .status(Response.Status.FORBIDDEN)
              .build();
        }
        localeContextProcessor.setLocaleContextHolder(locale, domainName);
        Response<User> userAuthResponse = metrics.timer(log).time("auth." + domainName, (StopWatch sw) -> {
            log.debug("auth domain : '" + domainName + "' username : '" + username + "' ip address : '" + ipAddress + "'");
            log.debug("parameters  : " + extraParameters.entrySet().stream().filter(p -> !p.getKey().equals("password")).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue())));
            sw.start("user: " + username);
            sw.stop();
            try {
                sw.start("domain");
                Domain domain = domain(domainName);
                sw.stop();

                sw.start("origin-pass");
                Response<User> originLoginBlock = checkOriginLoginBlock(domainNameOrEcosystemName + "/" + username, extraParameters, domainName);
                if (!originLoginBlock.isSuccessful()) {
                  log.warn("ORIGIN-LOGIN-BLOCK: Identified a user who is not allowed to use a player and/or backoffice login = " + domainNameOrEcosystemName + "/" + username + " and ip address = " + ipAddress);
                  return originLoginBlock;
                }
                sw.stop();

                sw.start("providers list");
                List<Provider> providerList = providersAuth(domainName);
                log.debug("providers list (" + providerList.size() + ") : " + providerList);
                sw.stop();

                if (extraParameters.containsKey("provider")) {
                    providerList.removeIf(p -> (!p.getUrl().equalsIgnoreCase(extraParameters.get("provider"))));
                }

                String providerAuthClient = "";
                if (extraParameters.containsKey("pac")) {
                    providerAuthClient = extraParameters.get("pac");
                    log.debug("decodedAuthString received as parameter. :: " + providerAuthClient);
                }

                sw.start("ipAndUserAgentData");
                Map<String, String> ipAndUserAgentData = ipAndUserAgentData(ipAddress, userAgent);
                sw.stop();

                Map<String, Object> checkPreAccessRuleResponse = preLoginCheck(domain, ipAndUserAgentData, extraParameters, username);
                if(!(Boolean) checkPreAccessRuleResponse.get("response")){
                  return Response.<User>builder()
                      .status(LOGIN_RESTRICTED)
                      .message(checkPreAccessRuleResponse.get("message").toString())
                      .build();
                }
                sw.start("new auth");
                Response<User> responseUser;
                for (Provider provider : providerList) {

                  boolean externalSignup = false;
                    extraParameters.putAll(provider.propertyMap());
                    Response<User> localUser = userProviderService.findLocalUser(domainName, username);
                    log.debug("findLocalUser response : " + localUser);
                    log.debug("provider internal : " + provider.internal());
                    //if gamstop blocked, we have to check first if removed

                  if(!checkAllowLoginFromUnknownCountryPermission(ipAndUserAgentData, domainName)) {
                    log.debug("Field \"allow_login_from_unknown_country\" is \"false\", access is forbidden to domain: {}; "
                        + "service-geo doesn't return any data about location / country / country code", domainName);
                    String errorMessage = "You can't login from an unknown country. Please contact customer support.";
                    loginEventService.saveLoginEvent(ipAndUserAgentData, errorMessage, localUser.getData(), domain, false, true, provider.getName(), provider.getUrl(), DOMAIN_UNKNOWN_COUNTRY_EXCEPTION.id(),
                        providerAuthClient);
                    return Response.<User>builder().status(DOMAIN_UNKNOWN_COUNTRY_EXCEPTION)
                        .message(errorMessage)
                        .build();
                  }
                    localUser = exclusionCheckService.checkGamstopStatus(localUser,domainName, username);
                    if (provider.internal()) {
                        // localUser can not be null and checking for it is silly since dereferencing is happening in this conditional.
                        // User does not exist internally
                        if (!localUser.isSuccessful()) {
                            int errorCode = (localUser.getData2() != null) ? localUser.getStatus().id() : FORBIDDEN.id();
                            String errorMsg = (localUser.getData2() != null) ? localUser.getMessage() : "Failed login attempt (unauthorized) [un:" + username + "]";
                            LoginEvent loginEvent = loginEventService.saveLoginEvent(
                                    ipAndUserAgentData,
                                    errorMsg,
                                    localUser.getData(),
                                    domain,
                                    false,
                                    true,
                                    null,
                                    null,
                                    errorCode,
                                    providerAuthClient
                            );
                            log.debug("loginEvent (failed internal) " + loginEvent);
                            log.warn("Failed login attempt, user not found in internal db. (domain : '" + domainName + "' username : '" + username + "')");
                            statsStream(domainName, localUser.getData(), ipAddress, userAgent, false);
                            // Distinction between disabled account and an unknown user error message.
                            if (localUser.getData2() != null) {
                                return Response.<User>builder()
                                        .status(localUser.getStatus())
                                        .message(localUser.getMessage())
                                        .build();
                            } else {
                              //Perform ecosystem checks for the various scenarios
                              responseUser = userProviderService.applyEcosystemLogicToLogin(
                                  domainName,
                                  username,
                                  password,
                                  localUser.isSuccessful(),
                                  ipAddress,
                                  userAgent,
                                  extraParameters.get("deviceId"),
                                  extraParameters,
                                  locale);
                              if (responseUser != null) {
                                updateLoginFailIp(domainName, responseUser.isSuccessful(), ipAddress);
                                return responseUser;
                              }
                              return Response.<User>builder()
                                      .status(FORBIDDEN)
                                      .message(Module.SERVICE_USER.getResponseMessageLocal(messageSource, domainName, "SERVICE_USER.INVALID_USERNAME_MESSAGE", "The credentials entered are incorrect."))
                                      .build();
                            }
                        }
                        log.debug("Internal Auth, finding user in local db.");
                        log.debug("localUser: " + localUser);
                        boolean success = false;
                        success = userProviderService.validatePassword(password, localUser);
                        // Handling the outcome of the password validation in failure scenarios
                        if (!success) {
                            if (localUser.getData() != null) {
                                LoginEvent loginEvent = loginEventService.saveLoginEvent(
                                        ipAndUserAgentData,
                                        "Failed login attempt (unauthorized) [un:" + username + "]",
                                        localUser.getData(),
                                        domain,
                                        false,
                                        true,
                                        null,
                                        null,
                                        FORBIDDEN.id(),
                                        providerAuthClient
                                );
                                log.debug("loginEvent " + loginEvent);
                            }
                            sw.stop();
                            statsStream(domainName, localUser.getData(), ipAddress, userAgent, false);

                            if (loginEventService.checkIfUserShouldBeBlockedForFailedLoginAttempts(localUser.getData(), false)) {
                                return doUserBlockAndResponse(localUser.getData(), domainName);
                            }

                            return Response.<User>builder()
                                    .status(FORBIDDEN)
                                    .message(Module.SERVICE_USER.getResponseMessageLocal(messageSource, domainName, "SERVICE_USER.INVALID_PASSWORD_MESSAGE", "The credentials entered are incorrect."))
                                    .build();
                        }

                        Response<User> checkAccessRuleResponse = checkAccessRule(domain, provider, extraParameters, ipAndUserAgentData, localUser);
                        if (nonNull(checkAccessRuleResponse)) {
                          sw.stop();
                          statsStream(domainName, localUser.getData(), ipAddress, userAgent, false);
                          return checkAccessRuleResponse;
                        }

                        if (loginEventService.checkIfUserShouldBeBlockedForFailedLoginAttempts(localUser.getData(), true)) {
                            log.debug("User had the correct password but was locked out due to previous attempts. User needs to reset password.");
                            return Response.<User>builder()
                                    .status(FAILED_LOGIN_BLOCK)
                                    .message(Module.SERVICE_USER.getResponseMessageLocal(messageSource, domainName, "SERVICE_USER.FAILED_LOGIN_BLOCK", "Your account has been blocked due to excessive failed logins. Please reset your password to log in."))
                                    .build();
                        }
                        responseUser = localUser;
                    } else { //provider.internal() === false
                        UserClient userProviderClient = serviceFactory.target(UserClient.class, provider.getUrl(), true);
                        log.debug("Calling provider auth with domain : '" + domainName + "' username : '" + username + "'");
                        log.debug("parameters  : " + extraParameters);

                      AuthRequest authRequest = AuthRequest.builder()
                          .domain(domainName)
                          .username(username)
                          .ipAddress(ipAddress)
                          .userAgent(userAgent)
                          .extraParameters(extraParameters)
                          .password(password)
                          .locale(locale)
                          .build();

                        responseUser = userProviderClient.auth(authRequest);
                        log.debug("ResponseUser : " + responseUser);
                        log.debug("localUser : " + localUser);
                        if ((responseUser == null) || (!responseUser.isSuccessful())) {
                            String errorMsgToFrontend = null;
                            String errorMsg = "Failed login attempt (unauthorized)";
                            if (!password.isEmpty()) errorMsg += " [pass: " + password + "]";
                            if (responseUser != null) {
                                if ((responseUser.getMessage() != null) && (!responseUser.getMessage().isEmpty())) {
                                    errorMsg += " [msg:" + responseUser.getMessage() + "]";
                                    errorMsgToFrontend = responseUser.getMessage();
                                }
                                if (responseUser.getData() != null) {
                                    User u = responseUser.getData();
                                    if ((u.getUsername() != null) && (!u.getUsername().isEmpty()))
                                        errorMsg += " [un:" + u.getUsername() + "]";

                                }
                            }
//							if (localUser.isSuccessful()) {
                            LoginEvent loginEvent = loginEventService.saveLoginEvent(
                                    ipAndUserAgentData,
                                    errorMsg,
                                    localUser.getData(),
                                    domain,
                                    false,
                                    false,
                                    provider.getName(),
                                    provider.getUrl(),
                                    localUser.getStatus().id(),
                                    providerAuthClient
                            );
                            log.debug("loginEvent (failed external) " + loginEvent);
//							}

                            if (loginEventService.checkIfUserShouldBeBlockedForFailedLoginAttempts(localUser.getData(), false)) {
                                return doUserBlockAndResponse(localUser.getData(), domainName);
                            }

                            responseUser = Response.<User>builder()
                                    .status(FORBIDDEN)
                                    .message(errorMsgToFrontend != null ? errorMsgToFrontend : Module.SERVICE_USER.getResponseMessageLocal(messageSource, domainName, "SERVICE_USER.INVALID_USERNAME_MESSAGE", "The credentials entered are incorrect."))
                                    .build();
                            statsStream(domainName, localUser.getData(), ipAddress, userAgent, false);
                            return responseUser;
                        } else {
                            //Check if local user can be found again.
                            localUser = userProviderService.findLocalUser(domainName, responseUser.getData().getUsername());
                            externalSignup = ((localUser.getData() == null) || (localUser.getData().getId() == null)) ? true : false;
                            if (externalSignup) log.warn("new external signup : " + responseUser.getData().guid());
                        }
                        if ((localUser != null) && (localUser.isSuccessful())) {
                            responseUser.getData().setId(localUser.getData().getId());
                            responseUser.getData().setCreatedDate(localUser.getData().getCreatedDate());
                            responseUser.getData().setGroups(localUser.getData().getGroups());
                        }

                        Response<User> checkAccessRuleResponse = checkAccessRule(domain, provider, extraParameters, ipAndUserAgentData, responseUser);
                        if (nonNull(checkAccessRuleResponse)) {
                            sw.stop();
                            statsStream(domain.getName(), responseUser.getData(), ipAndUserAgentData.get(MAP_IP), ipAndUserAgentData.get(MAP_USERAGENT), false);
                            return checkAccessRuleResponse;
                        }

                    }
                    log.debug("Response : " + responseUser);
                    responseUser.getData().setPasswordPlaintext(password);
                    // TODO: 2019/07/19 Combine below to avoid double call to API token service (could use findLocalUser method response for api data now)
                    responseUser.getData().setApiToken(userApiTokenService.saveApiToken(responseUser.getData().guid(), userApiTokenService.findOrGenerateApiToken(responseUser.getData().guid())).getToken());
                    responseUser.getData().setShortGuid(userApiTokenService.findOrGenerateShortGuid(responseUser.getData().guid()));
                    // FIXME: 2019/09/03 I am not sure if the below logic sould live in /users/user as sell as in Auth, will make a not to chat about it.
                    log.debug("LocalUsr : " + responseUser);

                    if (responseUser.isSuccessful()) {
						if ((responseUser.getData().getFailedResetCount()!=null) && (responseUser.getData().getFailedResetCount() > 0)) resetFailedPasswordResetCount(responseUser.getData().getGuid());
                      populateDomain(responseUser.getData());
                      if (!provider.internal()) {
                          responseUser = saveLocalUser(responseUser, domainName, responseUser.getData().getUsername());
                          if (externalSignup) {
                              SignupEvent signupEvent = signupEventService.saveSignupEvent(
                                      ipAndUserAgentData,
                                      domain,
                                      null,
                                      responseUser.getData().getId(),
                                      true
                              );
                              log.debug("signupEvent (success) " + signupEvent);
                          }
                      }
                      finalSecurityChecks(responseUser);
                      boolean isSuccessfulLogin = responseUser.isSuccessful();
                      String failReason = null;
                      Integer errorCode = null;
                      if(!isSuccessfulLogin) {
                        errorCode = responseUser.getStatus().id();
                        // We want to set the fail reasons here
                        // User response with Login Restriction returns a null value   from getMessage()
                        if (responseUser.getMessage() == null) {
                          if (responseUser.getStatus().message() != "LOGIN_RESTRICTED") {
                            failReason = Module.SERVICE_USER.getResponseMessageLocal(messageSource, domainName, "SERVICE_USER.LOGIN_HISTORY.FAIL_REASON", "Login is restricted");
                          } else {
                            failReason = Module.SERVICE_USER.getResponseMessageLocal(messageSource, domainName, "SERVICE_USER.LOGIN_HISTORY.RAW_STATUS_FAIL_REASON", "Login failed with the following status:") + " " + responseUser.getStatus().message();
                          }
                        } else {
                          failReason = responseUser.getMessage();
                        }

                      }
                      LoginEvent loginEvent = loginEventService.saveLoginEvent(
                          ipAndUserAgentData,
                          failReason,
                          responseUser.getData(),
                          domain,
                          responseUser.isSuccessful(),
                          provider.internal(),
                          provider.getName(),
                          provider.getUrl(),
                          errorCode,
                          providerAuthClient
                      );

                      statsStream(domainName, responseUser.getData(), ipAddress, userAgent, responseUser.isSuccessful());
                      log.debug("loginEvent (" + ((responseUser.isSuccessful()) ? "success" : "failed") + ") " + loginEvent);
                      if (responseUser.isSuccessful()) {

                        responseUser.getData().setSession(fromLoginEventEntity(loginEvent));

                        lithium.service.domain.client.objects.Domain coDomain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
                        if (coDomain.getPlayers() != null && coDomain.getPlayers()) {
                          userActiveSessionsMetadataService.updateMetadata(responseUser.getData().getId(), true);
                        }
                      }
                      sw.stop();
                      log.debug("Response User : " + responseUser);

                      return responseUser;
                    }
                }
                sw.stop();
                return Response.<User>builder().status(FORBIDDEN).message(Module.SERVICE_USER.getResponseMessageLocal(messageSource, domainName, "SERVICE_USER.UNEXPECTED_ERROR_MESSAGE", "Please contact customer support. We are unable to process your login.")).build();
            } catch (Status465DomainUnknownCountryException e) {
                return Response.<User>builder().status(DOMAIN_UNKNOWN_COUNTRY_EXCEPTION)
                    .message(e.getMessage())
                    .build();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Response.<User>builder().status(INTERNAL_SERVER_ERROR).build();
            }
        });
        updateLoginFailIp(domainName, userAuthResponse.isSuccessful(), ipAddress);
        return userAuthResponse;
    }

  @RequestMapping(value = "/users/auth", method = RequestMethod.POST)
  public Response<User> auth(@RequestBody AuthRequest request) throws Exception {
    return auth(request.getDomain(), request.getUsername(), request.getPassword(), request.getIpAddress(), request.getUserAgent(), request.getExtraParameters(), request.getLocale());
  }

  private boolean checkAllowLoginFromUnknownCountryPermission(Map<String, String> ipAndUserAgentData, String domainName) {
    if (!ipAndUserAgentData.containsKey(AccessService.MAP_COUNTRY_CODE) || ipAndUserAgentData.get(AccessService.MAP_COUNTRY_CODE) == null
        || !ipAndUserAgentData.containsKey(AccessService.MAP_COUNTRY) || ipAndUserAgentData.get(AccessService.MAP_COUNTRY) == null) {
      return cachingDomainClientService.allowLoginFromUnknownCountry(domainName);
    }

    return true;
  }

  private Response<User> checkOriginLoginBlock(String username, Map<String, String> extraParameters, String domainName)
      throws Status550ServiceDomainClientException {
    boolean allowPlayerLogin = false;
    boolean allowBackofficeLogin = false;
    String originPass = extraParameters.get("origin-pass");
    if (originPass != null) {
      String playerOriginPass = encryptOrigin("player", username);
      String backofficeOriginPass = encryptOrigin("backoffice", username);
      if (originPass.equals(playerOriginPass)) { allowPlayerLogin = true; }
      else if (originPass.equals(backofficeOriginPass)) { allowBackofficeLogin = true; }
    }

    boolean originLoginBlock = true;
    boolean isPlayerDomain = cachingDomainClientService.retrieveDomainFromDomainService(domainName).getPlayers();
    String message = LoginError.RESTRICTED.getResponseMessageLocal(messageSource, domainName);

    if (isPlayerDomain) {
      if (allowPlayerLogin) {
        originLoginBlock = false;
      } else {
        message = LoginError.PLAYER_ORIGIN_LOGIN_BLOCK.getResponseMessageLocal(messageSource, domainName);
      }
    }
    if (!isPlayerDomain) {
      if (allowBackofficeLogin) {
        originLoginBlock = false;
      } else {
        message = LoginError.BACKOFFICE_ORIGIN_LOGIN_BLOCK.getResponseMessageLocal(messageSource, domainName);
      }
    }

    if (originLoginBlock) {
      return Response.<User>builder()
          .status(LOGIN_RESTRICTED)
          .message(message)
          .build();
    }
    return Response.<User>builder().status(OK).build();
  }

  private void finalSecurityChecks(Response<User> responseUser) {
      lithium.service.domain.client.objects.Domain domain = null;
      try {
        domain = cachingDomainClientService.retrieveDomainFromDomainService(responseUser.getData().getDomain().getName());
      } catch (Status550ServiceDomainClientException e) {
        log.error("Failed to retrieve external domain on auth request for " + responseUser.getData().guid() + " | " + e.getMessage(), e);
        responseUser = Response.<User>builder().status(INTERNAL_SERVER_ERROR).build();
      }
      if (domain != null && domain.getPlayers()) {
        Access access = null;
        try {
          access = limits.checkAccess(responseUser.getData().guid());
        } catch (Status500LimitInternalSystemClientException e) {
          log.error("Failed to check access for " + responseUser.getData().guid() + " | " + e.getMessage(), e);
          responseUser = Response.<User>builder().status(INTERNAL_SERVER_ERROR).build();
        }
        if (responseUser.isSuccessful() && !access.isLoginAllowed()) {
          responseUser.setStatus(LOGIN_RESTRICTED);
          responseUser.setMessage(access.getLoginErrorMessage());
        } else if (responseUser.isSuccessful()) {
          try {
            limits.checkPlayerRestrictions(responseUser.getData().guid(), domain.getDefaultLocale());
          } catch (Status500LimitInternalSystemClientException e) {
            log.error("Failed to check player restrictions for " + responseUser.getData().guid() + " | " + e.getMessage(), e);
            responseUser = Response.<User>builder().status(INTERNAL_SERVER_ERROR).build();
          } catch (Status490SoftSelfExclusionException |
              Status491PermanentSelfExclusionException |
              Status496PlayerCoolingOffException e) {
            responseUser.setStatus(CUSTOM.id(e.getCode()));
            responseUser.setMessage(e.getMessage());
          }
        }
      }
    }

    private lithium.service.user.client.objects.LoginEvent fromLoginEventEntity(LoginEvent loginEvent) {
	    return lithium.service.user.client.objects.LoginEvent.builder()
        .id(loginEvent.getId())
        .date(loginEvent.getDate())
        .ipAddress(loginEvent.getIpAddress())
        .country(loginEvent.getCountry())
        .countryCode(loginEvent.getCountryCode())
        .state(loginEvent.getState())
        .city(loginEvent.getCity())
        .os(loginEvent.getOs())
        .browser(loginEvent.getBrowser())
        .comment(loginEvent.getComment())
        .userAgent(loginEvent.getUserAgent())
        .successful(loginEvent.getSuccessful())
        .logout(loginEvent.getLogout())
        .duration(loginEvent.getDuration())
        .sessionKey(loginEvent.getSessionKey())
            .user(modelMapper.map(loginEvent.getUser(), lithium.service.user.client.objects.User.class))
            .domain(modelMapper.map(loginEvent.getDomain(), lithium.service.user.client.objects.Domain.class))
        .build();
    }

    @RequestMapping(value = "/frontend/player/logout", method = RequestMethod.POST)
    public Response<User> exit(LithiumTokenUtil tokenUtil) throws Status401UnAuthorisedException {
      loginEventService.validateSession(tokenUtil.domainName(), tokenUtil.sessionId());
      loginEventService.logout(tokenUtil.guid(), tokenUtil.sessionId());
      return Response.<User>builder().status(OK).build();
    }

    private Response<User> checkAccessRule(Domain domain, Provider provider, Map<String, String> extraParameters, Map<String, String> ipAndUserAgentData, Response<User> responseUser) throws LithiumServiceClientFactoryException, Status551ServiceAccessClientException, Status465DomainUnknownCountryException {
        if (responseUser.isSuccessful() && nonNull(responseUser.getData())) {
            User userData = responseUser.getData();
            String loginAccessRule = cachingDomainClientService.retrieveDomainFromDomainService(userData.getDomain().getName()).getLoginAccessRule();
            if (loginAccessRule != null && !loginAccessRule.isEmpty() && accessService.isAccessRuleEnabled(domain.getName(), loginAccessRule, null)) {
                Address address = responseUser.getData().getResidentialAddress();
                Map<String, String> claimedGeoData = new LinkedHashMap<String, String>();
                claimedGeoData.put(AccessService.MAP_CLAIMED_COUNTRY, (address != null) ? address.getCountry() : null);
                claimedGeoData.put(AccessService.MAP_CLAIMED_STATE, (address != null) ? address.getAdminLevel1() : null);
                claimedGeoData.put(AccessService.MAP_CLAIMED_CITY, (address != null) ? address.getCity() : null);
                String userGuid;
                if (userData.guid() != null) {
                    userGuid = responseUser.getData().guid();
                } else {
                    userGuid = GUID_STRATEGY == USERNAME ?
                            userData.getUsername()  :  userData.getDomain().getName() + "/" + userData.getId();
                }
                PlayerBasic playerBasic = modelMapper.map(userData, PlayerBasic.class);
                AuthorizationResult authorizationResult =
                        accessService.checkAuthorization(
                                domain.getName(),
                                loginAccessRule,
                                claimedGeoData,
                                ipAndUserAgentData,
                                extraParameters.get("deviceId"),
                                userGuid,
                                false,
                                playerBasic);
                log.info("authorizationResult " + authorizationResult);
                boolean authSuccessful = (authorizationResult != null) ? authorizationResult.isSuccessful() : true;
                if (!authSuccessful) {
                    LoginEvent loginEvent = loginEventService.saveLoginEvent(
                            ipAndUserAgentData,
                            authorizationResult.getMessage(),
                            responseUser.getData(),
                            domain,
                            authSuccessful,
                            provider.internal(),
                            provider.getName(),
                            provider.getUrl(),
                            LOGIN_RESTRICTED.id(),
                            Optional.ofNullable(extraParameters.get("pac")).orElse("")
                    );
                    log.debug("loginEvent " + loginEvent);

                    if (loginEventService.checkIfUserShouldBeBlockedForFailedLoginAttempts(responseUser.getData(), false)) {
                        return doUserBlockAndResponse(responseUser.getData(), domain.getName());
                    }
                    String message;
                    if (authorizationResult.getErrorMessage() != null) {
                      message = authorizationResult.getErrorMessage();
                    } else {
                      message = Module.SERVICE_USER.getResponseMessageLocal(messageSource, domain.getName(), "SERVICE_USER.ACCESS_RULECHECK_REJECT_MESSAGE", "Your account is currently disabled. Please contact customer support.");
                    }

                  final lithium.service.user.data.entities.User byUserGuidAlwaysRefresh= userService.findByUserGuidAlwaysRefresh(userGuid);
                  if ((byUserGuidAlwaysRefresh.getStatus() != null) && (!byUserGuidAlwaysRefresh.getStatus().getUserEnabled())) {
                    AccountStatusErrorCodeAndMessage errorCodeAndMessage = userService.getAccountStatusErrorCodeAndMessage(byUserGuidAlwaysRefresh);
                    return Response.<User>builder()
                        .data2(errorCodeAndMessage)
                        .status(Response.Status.CUSTOM.id(errorCodeAndMessage.getErrorCode()))
                        .message(errorCodeAndMessage.getErrorMsg())
                        .build();
                  }

                  return Response.<User>builder()
                            .status(LOGIN_RESTRICTED)
                            .message(message)
                            .build();
                }
            }
        }
        return null;
    }

    private Map<String, Object> preLoginCheck(Domain domain, Map<String, String> ipAndUserAgentData,
        Map<String, String> extraParameters, String username)
        throws LithiumServiceClientFactoryException, Status551ServiceAccessClientException {
      Map<String, Object> result = new HashMap<>();
      String preLoginAccessRule = cachingDomainClientService.retrieveDomainFromDomainService(domain.getName()).getPreLoginAccessRule();
      if (!ObjectUtils.isEmpty(preLoginAccessRule) &&
          accessService.isAccessRuleEnabled(domain.getName(), preLoginAccessRule, null)) {
        String userGuid = domain.getName() + "/" + username;
        AuthorizationResult authorizationResult =
            accessService.checkAuthorization(
                domain.getName(),
                preLoginAccessRule,
                null,
                ipAndUserAgentData,
                extraParameters.get("deviceId"),
                userGuid,
                false,
                null);
        result.put("response", authorizationResult == null || authorizationResult.isSuccessful());
        result.put("message", authorizationResult.getErrorMessage());

        return result;
      }
      result.put("response", true);
      return result;
    }

    private void updateLoginFailIp(String domainName, boolean isLoginSuccess, String ipAddress) throws LithiumServiceClientFactoryException {
        if (ipAddress.contains(",")) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(",")).trim();
        }
        keepFailLoginAttemptsService.update(domainName, ipAddress, isLoginSuccess);
    }

	private void resetFailedPasswordResetCount(String guid) {
		lithium.service.user.data.entities.User user = userService.findFromGuid(guid);
		user.setFailedResetCount(0);
		userService.save(user);
		clearResetTokens(user);
	}

	private void clearResetTokens(lithium.service.user.data.entities.User user) {
		passwordResetService.clearResetTokens(user);
	}

    private Response<User> doUserBlockAndResponse(User user, String domainName) {
        userProviderService.blockUserForExcessiveFailedLogins(user);
        return Response.<User>builder()
                .status(FAILED_LOGIN_BLOCK)
                .message(Module.SERVICE_USER.getResponseMessageLocal(messageSource, domainName, "SERVICE_USER.FAILED_LOGIN_BLOCK", "Your account has been blocked due to excessive failed logins. Please reset your password to log in."))
                .build();
    }

    private void statsStream(String domainName, User user, String ipAddress, String userAgent, Boolean success) {
        if (success) {
            statsStream(true, domainName, user.guid(), ipAddress, userAgent);
//            dailyMissionStream(user);
        } else {
            if (user != null) statsStream(false, domainName, user.guid(), ipAddress, userAgent);
        }
    }

    private void statsStream(boolean success, String domainName, String userGuid, String ipAddress, String userAgent) {
        Event event = null;
        if (success) {
            event = Event.LOGIN_SUCCESS;
        } else {
            event = Event.LOGIN_FAIL;
        }
        QueueStatEntry queueStatEntry = QueueStatEntry.builder()
                .type(lithium.service.stats.client.enums.Type.USER.type())
                .event(event.event())
                .entry(
                        StatEntry.builder()
                                .name(
                                        "stats.user." +
                                        userGuid.replaceAll("/", ".") + "." +
                                        event.event()
                                )
                                .domain(domainName)
                                .ownerGuid(userGuid)
                                .ipAddress(ipAddress)
                                .userAgent(userAgent)
                                .build()
                )
                .build();
        statsStream.register(queueStatEntry);
    }

//    private void dailyMissionStream(User user) {
//        MissionStatBasic mse = MissionStatBasic.builder()
//                .ownerGuid(user.guid())
//                .type(Type.TYPE_USER)
//                .action(Action.ACTION_LOGIN)
//                .timezone(user.getTimezone())
//                .build();
//        log.debug("MissionStatBasic : " + mse);
//        missionStatsStream.register(mse);
//    }

    private void populateDomain(User user) throws Exception {
        Domain domainResponse = domain(user.getDomain().getName());
        log.debug("Found domain : " + domainResponse);
        user.setDomain(modelMapper.map(domainResponse, lithium.service.user.client.objects.Domain.class));
    }

    @RequestMapping("/users/user")
    public Response<User> user(
            @RequestParam("domain") String domainName,
            @RequestParam("username") String username,
            @RequestParam(required = false) Map<String, String> parameters
    ) throws Exception {
        return metrics.timer(log).time("user." + domainName, (StopWatch sw) -> {
            log.info("user domain : '" + domainName + "' username : '" + username + "' ");
            log.debug("parameters  : " + parameters);
            sw.start("user: " + username);
            sw.stop();
            try {
                sw.start("providers list");
                List<Provider> providerList = providersUser(domainName);
                log.debug("providers list (" + providerList.size() + ") : " + providerList);
                sw.stop();

                Response<User> responseUser = null;
                for (Provider provider : providerList) {
                    parameters.putAll(provider.propertyMap());

                    if (!provider.internal()) {
                        UserClient userProviderClient = serviceFactory.target(UserClient.class, provider.getUrl(), true);
                        log.debug("Calling provider user with domain : '" + domainName + "' username : '" + username + "' parameters : '" + parameters);
                        responseUser = userProviderClient.user(domainName, username, parameters);
                        log.debug("Provider Response :: " + responseUser);
                        if (responseUser.isSuccessful()) {
                            return responseUser;
//							return saveLocalUser(responseUser, domainName, username);
                        }
                    }
                }
                responseUser = userProviderService.findLocalUser(domainName, username);
                return responseUser;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Response.<User>builder().status(INTERNAL_SERVER_ERROR).build();
            }
        });
    }

    public Response<User> create(User user) {
        return null;
    }

    @RequestMapping(path = "/users/custom", method = RequestMethod.POST)
    public Response<User> update(@RequestParam Integer id, @RequestParam String message) {
//		users.save(user);
        return Response.<User>builder().status(CUSTOM.fromId(id)).message(message).build();
    }

    @DeleteMapping(path = "/users/d/{domainName}")
    public Response<String> delete(
            @PathVariable("domainName") String domainName,
            @RequestParam(required = false) Map<String, String> parameters
    ) throws Exception {
        return metrics.timer(log).time("deleteUser." + domainName, (StopWatch sw) -> {
            log.info("deleteUser domain : '" + domainName + "' ");
            log.debug("parameters  : " + parameters);
            try {
                String username = parameters.getOrDefault("u", "");
                sw.start("user: " + username);
                sw.stop();
                if (!username.isEmpty()) userProviderService.obfuscateUserData(domainName, username);
                return Response.<String>builder().status(OK).build();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Response.<String>builder().status(INTERNAL_SERVER_ERROR).build();
            }
        });
    }
}
