package lithium.server.security.configuration;

import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status407IpBlockedException;
import lithium.exceptions.Status428AccountFrozenGamstopSelfExcludedException;
import lithium.exceptions.Status430UserUpgradeRequiredInEcosystemException;
import lithium.exceptions.Status433AccountBlockedPlayerRequestException;
import lithium.exceptions.Status434AccountBlockedResponsibleGamingException;
import lithium.exceptions.Status435AccountBlockedAMLException;
import lithium.exceptions.Status436AccountBlockedDuplicatedAccountException;
import lithium.exceptions.Status437AccountBlockedOtherException;
import lithium.exceptions.Status446AccountFrozenCRUKSSelfExcludedException;
import lithium.exceptions.Status447AccountFrozenException;
import lithium.exceptions.Status448AccountBlockedException;
import lithium.exceptions.Status449AccountFrozenCoolingOffException;
import lithium.exceptions.Status450AccountFrozenSelfExcludedException;
import lithium.exceptions.Status455AccountBlockedFraudException;
import lithium.exceptions.Status460LoginRestrictedException;
import lithium.exceptions.Status465DomainUnknownCountryException;
import lithium.exceptions.Status492ExcessiveFailedLoginBlockException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.server.security.services.TokenService;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.translate.client.objects.LoginError;
import lithium.service.user.client.UserClient;
import lithium.service.user.client.objects.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Order(1)
@Slf4j
public class SecurityConfig {

    @Autowired CachingDomainClientService cachingDomainClientService;
    @Autowired LithiumServiceClientFactory services;
    @Autowired HttpServletRequest request;
    @Autowired MessageSource messageSource;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                authorizeRequests.anyRequest().authenticated()
        )
                .formLogin(withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService users() {
        UserDetails user = org.springframework.security.core.userdetails.User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    private void checkUserServiceResponse(
            Response<User> response,
            String requestStr,
            String domainName
    ) throws
            Status401UnAuthorisedException,
            Status403AccessDeniedException,
            Status405UserDisabledException,
            Status407IpBlockedException,
            Status430UserUpgradeRequiredInEcosystemException,
            Status447AccountFrozenException,
            Status448AccountBlockedException,
            Status449AccountFrozenCoolingOffException,
            Status450AccountFrozenSelfExcludedException,
            Status428AccountFrozenGamstopSelfExcludedException,
            Status433AccountBlockedPlayerRequestException,
            Status434AccountBlockedResponsibleGamingException,
            Status435AccountBlockedAMLException,
            Status455AccountBlockedFraudException,
            Status436AccountBlockedDuplicatedAccountException,
            Status437AccountBlockedOtherException,
            Status460LoginRestrictedException,
            Status465DomainUnknownCountryException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status492ExcessiveFailedLoginBlockException,
            Status496PlayerCoolingOffException,
            Status500LimitInternalSystemClientException {
        try {
            if ((response != null) && (!response.isSuccessful())) {
                requestStr += " - "+response.getStatus()+" : "+response.getMessage();
                log.info(requestStr);
                if (response.getStatus().id().equals(Response.Status.FAILED_LOGIN_BLOCK.id())) {
                    throw new Status492ExcessiveFailedLoginBlockException(LoginError.EXCESSIVE_FAILED_LOGIN_BLOCK.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Response.Status.DISABLED.id())) {
                    throw new Status405UserDisabledException(LoginError.USER_DISABLED.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Response.Status.FORBIDDEN.id())) {
                    throw new Status403AccessDeniedException(LoginError.ACCESS_DENIED.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Response.Status.UNAUTHORIZED.id())) {
                    throw new Status401UnAuthorisedException(LoginError.UNAUTHORIZED.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Response.Status.IP_ADDRESS_BLOCKED.id())) {
                    throw new Status407IpBlockedException(LoginError.IP_BLOCKED.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Response.Status.LOGIN_RESTRICTED.id())) {
                    throw new Status460LoginRestrictedException(LoginError.RESTRICTED.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status447AccountFrozenException.ERROR_CODE)) {
                    throw new Status447AccountFrozenException(LoginError.ACCOUNT_FROZEN.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status448AccountBlockedException.ERROR_CODE)) {
                    throw new Status448AccountBlockedException(LoginError.ACCOUNT_BLOCKED.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status449AccountFrozenCoolingOffException.ERROR_CODE)) {
                    throw new Status449AccountFrozenCoolingOffException(LoginError.ACCOUNT_FROZEN_COOLING_OFF.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status450AccountFrozenSelfExcludedException.ERROR_CODE)) {
                    throw new Status450AccountFrozenSelfExcludedException(LoginError.ACCOUNT_FROZEN_SELF_EXCLUDED.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status428AccountFrozenGamstopSelfExcludedException.ERROR_CODE)) {
                    throw new Status428AccountFrozenGamstopSelfExcludedException(LoginError.ACCOUNT_FROZEN_GAMESTOP_SELF_EXCLUDED.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status433AccountBlockedPlayerRequestException.ERROR_CODE)) {
                    throw new Status433AccountBlockedPlayerRequestException(LoginError.ACCOUNT_BLOCKED_PLAYER_REQUEST.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status434AccountBlockedResponsibleGamingException.ERROR_CODE)) {
                    throw new Status434AccountBlockedResponsibleGamingException(LoginError.ACCOUNT_BLOCKED_RESPONSIBLE_GAMING.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status435AccountBlockedAMLException.ERROR_CODE)) {
                    throw new Status435AccountBlockedAMLException(LoginError.ACCOUNT_BLOCKED_AML.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status455AccountBlockedFraudException.ERROR_CODE)) {
                    throw new Status455AccountBlockedFraudException(LoginError.ACCOUNT_BLOCKED_FRAUD.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status436AccountBlockedDuplicatedAccountException.ERROR_CODE)) {
                    throw new Status436AccountBlockedDuplicatedAccountException(LoginError.ACCOUNT_BLOCKED_DUPLICATE_ACCOUNT.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status437AccountBlockedOtherException.ERROR_CODE)) {
                    throw new Status437AccountBlockedOtherException(LoginError.ACCOUNT_BLOCKED_OTHER.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status446AccountFrozenCRUKSSelfExcludedException.ERROR_CODE)) {
                    // FIXME: Quickfix to send GW/FE a generic SE error code for frozen account due to CRUKS SE.
                    //        These error codes will be cleaned up at some point.
                    throw new Status450AccountFrozenSelfExcludedException(LoginError.ACCOUNT_FROZEN_CRUKS_SELF_EXCLUSION.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status465DomainUnknownCountryException.ERROR_CODE)) {
                    throw new Status465DomainUnknownCountryException(LoginError.DOMAIN_UNKNOWN_COUNTRY.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status490SoftSelfExclusionException.ERROR_CODE)) {
                    throw new Status490SoftSelfExclusionException(LoginError.SOFT_SELF_EXCLUSION.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status491PermanentSelfExclusionException.ERROR_CODE)) {
                    throw new Status491PermanentSelfExclusionException(LoginError.PERMANENT_SELF_EXCLUSION.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status496PlayerCoolingOffException.ERROR_CODE)) {
                    throw new Status496PlayerCoolingOffException(LoginError.FLAGGED_AS_COOLING_OFF.getResponseMessageLocal(messageSource, domainName));
                }
                if (response.getStatus().id().equals(Status.ACCOUNT_UPGRADE_REQUIRED.id())) {
                    //This is a special response from service user to cater for ecosystem registration process flow
                    Status430UserUpgradeRequiredInEcosystemException exception = new Status430UserUpgradeRequiredInEcosystemException(LoginError.ADDITIONAL_INFORMATION.getResponseMessageLocal(messageSource, domainName));
                    if (response.getData() != null) {
                        exception.clearAdditionalInformation(); //Ensures we are not carrying over info added from previous requests
                        if (response.getData().getFirstName() != null) { exception.addAdditionalInformation("firstName", response.getData().getFirstName()); }
                        if (response.getData().getLastName() != null) { exception.addAdditionalInformation("lastName", response.getData().getLastName()); }
                        if (response.getData().getDobDay() != null) { exception.addAdditionalInformation("dobDay", response.getData().getDobDay().toString()); }
                        if (response.getData().getDobMonth() != null) { exception.addAdditionalInformation("dobMonth", response.getData().getDobMonth().toString()); }
                        if (response.getData().getDobYear() != null) { exception.addAdditionalInformation("dobYear", response.getData().getDobYear().toString()); }
                        exception.addAdditionalInformation("email", response.getData().getEmail());
                        exception.addAdditionalInformation("uuid", response.getData().getApiToken());
                        //TODO: Add when africa becomes implemented
                        //exception.addAdditionalInformation("cellphoneNumber", response.getData().getCellphoneNumber());
                        throw exception;
                    }
                }
                log.error("Authentication against service-user returned a non-standard response: " + response.toString());
                throw new RuntimeException(LoginError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, domainName));
            }

            User user = response.getData();
            Domain domain = null;

            try {
                domain = cachingDomainClientService.retrieveDomainFromDomainService(user.getDomain().getName());
            } catch (Exception e) {
                String msg = "Failed to retrieve domain while trying to authenticate " + user.guid();
                log.error(msg + " | " + e.getMessage(), e);
                throw new RuntimeException(LoginError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, domainName));
            }

            TokenService.storeInThread(TokenService.TL_DATA_USER, user);
            TokenService.storeInThread(TokenService.TL_DATA_DOMAIN, domain);
        } catch (
                Status401UnAuthorisedException |
                        Status403AccessDeniedException |
                        Status405UserDisabledException |
                        Status407IpBlockedException |
                        Status447AccountFrozenException |
                        Status448AccountBlockedException |
                        Status449AccountFrozenCoolingOffException |
                        Status450AccountFrozenSelfExcludedException |
                        Status428AccountFrozenGamstopSelfExcludedException |
                        Status433AccountBlockedPlayerRequestException |
                        Status434AccountBlockedResponsibleGamingException |
                        Status435AccountBlockedAMLException |
                        Status455AccountBlockedFraudException |
                        Status436AccountBlockedDuplicatedAccountException |
                        Status437AccountBlockedOtherException |
                        Status460LoginRestrictedException |
                        Status490SoftSelfExclusionException |
                        Status465DomainUnknownCountryException |
                        Status491PermanentSelfExclusionException |
                        Status492ExcessiveFailedLoginBlockException |
                        Status496PlayerCoolingOffException e
        ) {
            requestStr += " - "+e.getCode()+" : "+e.getMessage();
            log.info(requestStr);
            throw e;
        }
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @SneakyThrows
            @Override
            public UserDetails loadUserByUsername(String username) {
                String requestStr = "loadUserByUsername " + username;
                try {
                    String[] split = username.split("/");
                    String domain = (split.length == 2)? split[0]: "default";
                    String domainUsername = (split.length == 2)? split[1]: username;

                    UserClient userClient = services.target(UserClient.class, true);
                    Response<User> response = userClient.user(domain, domainUsername, Collections.emptyMap());

                    checkUserServiceResponse(response, requestStr, domain);

                    requestStr += " - "+response.getStatus()+" : "+response.getMessage();
                    log.info(requestStr);

                    return response.getData();
                } catch (ErrorCodeException e) {
                    requestStr += " - "+e.getMessage();
                    log.info(requestStr);
                    throw e;
                } catch (Exception e) {
                    requestStr += " - "+e.getMessage();
                    log.error(requestStr);
                    throw new Status500InternalServerErrorException("server-oauth2", e.getMessage(), e);
                }
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(UsernamePasswordAuthenticationToken.class);
            }

            @SneakyThrows
            @Override
            @SuppressWarnings("unchecked")
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String requestStr = "authenticate " + authentication.getName();
                try {
                    String username = authentication.getName();
                    String password = authentication.getCredentials().toString();
                    Object detailsObject = authentication.getDetails();
                    String domain = "";
                    String deviceId = ""; //Blackbox
                    String locale = "en_US";
                    Map<String, String> extraParameters = new HashMap<>();
                    if (detailsObject instanceof Map) {
                        Map<String, String> details = (Map<String, String>) detailsObject;
                        domain = details.get("domain");

                        deviceId = details.get("deviceId");
                        if (deviceId != null && !deviceId.isEmpty()) {
                            extraParameters.put("deviceId", deviceId);
                        }
                    }
                    //TODO: Add locale to details object if it is not there already
                    if (detailsObject instanceof Map) {
                        Map<String, String> details = (Map<String, String>) detailsObject;
                        String tmpLocale = details.get("locale");
                        if (tmpLocale != null && !tmpLocale.isEmpty()) locale = tmpLocale;
                        LocaleContextHolder.setLocale(Locale.forLanguageTag(locale));
                    }
                    if ((domain == null) || (domain.isEmpty())) {
                        String[] split = username.split("/");
                        domain = (split.length == 2) ? split[0] : "default";
                        username = (split.length == 2) ? split[1] : username;
                    }
                    String ipAddress = request.getRemoteAddr();
                    requestStr += " remoteAddr: "+ipAddress;
                    if (request.getHeader("X-Forwarded-For") != null) {
                        ipAddress = request.getHeader("X-Forwarded-For");
                        requestStr += " X-Forwarded-For: "+ipAddress;
                    } else {
                        requestStr += " X-Forwarded-For: null";
                    }
                    String userAgent = request.getHeader("User-Agent");
                    if (request.getHeader("User-Agent-Forwarded") != null) {
                        userAgent = request.getHeader("User-Agent-Forwarded");
                        requestStr += " User-Agent-Forwarded: "+userAgent;
                    } else {
                        requestStr += " User-Agent-Forwarded: null";
                    }
                    String base64AuthHeaderString = request.getHeader("Authorization");
                    String decodedAuthString = new String(Base64.getDecoder().decode(base64AuthHeaderString.replaceFirst("Basic ", "")));
                    if (decodedAuthString != null) {
                        log.debug("decodedAuthString added as parameter. :: " + decodedAuthString);
                        extraParameters.put("pac", decodedAuthString.split(":")[0]);
                    }
                    String externalToken = request.getHeader("external-token");
                    if (externalToken != null) {
                        log.info("Received external-token header, adding as parameter. :: " + externalToken);
                        extraParameters.put("external-token", externalToken);
                    }

                    UserClient userClient = services.target(UserClient.class, true);
                    Response<User> response = userClient.auth(domain, username, password, ipAddress, userAgent, extraParameters, locale);
                    log.debug("authenticate response " + response);

                    checkUserServiceResponse(response, requestStr, domain);

                    String roleValidatedEmail = response.getData() != null && response.getData().isEmailValidated()? ",ROLE_VALIDATED_EMAIL": "";
                    List<GrantedAuthority> grantedAuths = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"+roleValidatedEmail);
                    //					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(domain + "/" + domainUsername, password, grantedAuths);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(response.getData(), password, grantedAuths);

                    requestStr += " - "+response.getStatus()+" : "+response.getMessage();
                    log.info(requestStr);
                    return auth;
                } catch (ErrorCodeException e) {
                    requestStr += " - "+e.getMessage();
                    log.info(requestStr);
                    throw e;
                } catch (Exception e) {
                    requestStr += " - "+e.getMessage();
                    log.error(requestStr);
                    throw new Status500InternalServerErrorException("server-oauth2", e.getMessage(), e);
                }
            }
        };
    }

    //	@Bean
    //	public PasswordEncoder passwordEncoder(){
    //		PasswordEncoder encoder = new BCryptPasswordEncoder();
    //		return encoder;
    //	}
}
