package lithium.server.security.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.exceptions.Status407IpBlockedException;
import lithium.exceptions.Status460LoginRestrictedException;
import lithium.exceptions.Status492ExcessiveFailedLoginBlockException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.Granularity;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.LimitType;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.PlayerLimitFE;
import lithium.service.limit.client.objects.PlayerLimitSummary;
import lithium.service.limit.client.objects.PlayerLimitSummaryFE;
import lithium.service.user.client.objects.TermsAndConditionsVersion;
import lithium.service.user.client.objects.User;
import lithium.service.limit.client.objects.PlayTimeLimitFE;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.service.user.client.service.UserPlayTimeLimitsClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class TokenService {
    @Autowired TokenEndpoint tokenEndpoint;
    @Autowired LimitInternalSystemService limits;
    @Autowired AuthenticationManager authenticationManager;
    @Autowired ObjectMapper mapper;
    @Autowired MessageSource messageSource;
    @Autowired UserApiInternalClientService userApiInternalClientService;
    @Autowired UserPlayTimeLimitsClientService userPlayTimeLimitsClientService;


    public static final String TOKEN_CLAIM_SESSION_ID = "sessionId";
    public static final String TOKEN_CLAIM_DOMAIN_NAME = "domainName";

    private static final ThreadLocal<Map<String, Object>> threadLocalStorage = ThreadLocal.withInitial(LinkedHashMap::new);
    public static final String TL_DATA_DOMAIN = "domain";
    public static final String TL_DATA_USER = "user";

    public static void storeInThread(String key, Object obj) {
        threadLocalStorage.get().put(key, obj);
    }

    public static Object getFromThread(String key) {
        return threadLocalStorage.get().get(key);
    }

    public Authentication convertToAuth(String auth) {
        String decodedAuthString = new String(Base64.getDecoder().decode(auth.replaceFirst("Basic ", "")));
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(decodedAuthString.split(":")[0], decodedAuthString.split(":")[1], Collections.emptyList());
        return authReq;
    }

    public ResponseEntity<OAuth2AccessToken> tokenResponseEntity(
            Principal principal,
            Map<String, String> parameters
    ) throws HttpRequestMethodNotSupportedException {
        return tokenEndpoint.postAccessToken(principal, parameters);
    }
    public OAuth2AccessToken accessToken(
            Principal principal,
            Map<String, String> parameters
    ) throws
            Status401UnAuthorisedException,
            Status403AccessDeniedException,
            Status405UserDisabledException,
            Status407IpBlockedException,
            Status460LoginRestrictedException,
            Status490SoftSelfExclusionException,
            Status491PermanentSelfExclusionException,
            Status492ExcessiveFailedLoginBlockException,
            Status496PlayerCoolingOffException,
            Status500LimitInternalSystemClientException,
            Status500InternalServerErrorException
    {
        try {
            return tokenEndpoint.postAccessToken(principal, parameters).getBody();
        } catch (HttpRequestMethodNotSupportedException e) {
            throw new Status500InternalServerErrorException(e.getMessage(), e);
        }
    }

    public DefaultOAuth2AccessToken enhancedAccessToken(
            OAuth2AccessToken token
    ) throws Status500LimitInternalSystemClientException {
        DefaultOAuth2AccessToken newToken = new DefaultOAuth2AccessToken(token);
        newToken.setAdditionalInformation(responseObjEnhancements());
        return newToken;
    }

    /**
     * The enhancements done here will affect the response object.
     * For enhancements to the access_token, see lithium.server.oauth2.LithiumTokenEnhancer#enhance(org.springframework.security.oauth2.common.OAuth2AccessToken, org.springframework.security.oauth2.provider.OAuth2Authentication).
     * For enhancements to both, you will need to include your key/value on both maps.
     *
     * @return
     */
    private Map<String, Object> responseObjEnhancements() throws Status500LimitInternalSystemClientException {
        Map<String, Object> data = new LinkedHashMap<>();

        User user = (User) getFromThread(TL_DATA_USER);
        Domain domain = (Domain) getFromThread(TL_DATA_DOMAIN);
        log.debug("User :: " + user);
        log.debug("Domain :: " + domain);

        doUserEnhancements(user, data);
        doDomainEnhancements(user, domain, data);
        if (domain.getPlayers()) doLimitRestrictionsEnhancements(user, data);
        if (domain.getPlayers()) doAccessEnhancements(user.guid(), data);
        doCheckForResetUserPlayTime(user.guid());
        return data;
    }

    private void doLimitRestrictionsEnhancements(User user, Map<String, Object> data) {

        if (!ObjectUtils.isEmpty(user)) {
            PlayerLimitSummaryFE summaryBlock;
            try {
                summaryBlock = limits.getPlayerLimitSummary(user.guid());
                if (!ObjectUtils.isEmpty(summaryBlock)){
                    PlayerLimitSummary playerLimitSummary = PlayerLimitSummary.builder().build();
                    getBalanceLimit(summaryBlock.getBalanceLimits(), LimitType.TYPE_BALANCE_LIMIT)
                            .ifPresent(playerLimitFE -> playerLimitSummary.setBalanceLimit(playerLimitFE.getAmount()));
                    getDepositLimit(summaryBlock.getDepositLimits(), Granularity.GRANULARITY_DAY)
                            .ifPresent(playerLimitFE -> playerLimitSummary.setDepositLimitDay(playerLimitFE.getAmount()));
                    getDepositLimit(summaryBlock.getDepositLimits(), Granularity.GRANULARITY_WEEK)
                            .ifPresent(playerLimitFE -> playerLimitSummary.setDepositLimitWeek(playerLimitFE.getAmount()));
                    getDepositLimit(summaryBlock.getDepositLimits(), Granularity.GRANULARITY_MONTH)
                            .ifPresent(playerLimitFE -> playerLimitSummary.setDepositLimitMonth(playerLimitFE.getAmount()));
                    getPlayTimeLimit(summaryBlock.getPlayTimeLimits(), Granularity.GRANULARITY_DAY)
                            .ifPresent(playerLimitFE -> playerLimitSummary.setPlayTimeLimitInMinutes(playerLimitFE.getPlayTimeLimit()));
                    getPlayTimeLimit(summaryBlock.getPlayTimeLimits(), Granularity.GRANULARITY_DAY)
                            .ifPresent(playerLimitFE -> playerLimitSummary.setPlayTimeLimitGranularity(playerLimitFE.getGranularity()));
                    if (!ObjectUtils.isEmpty(summaryBlock.getTimeSlotLimit())){
                        if (!ObjectUtils.isEmpty(summaryBlock.getTimeSlotLimit().getFromTimestampUTC())){
                            playerLimitSummary.setTimeSlotLimitStart(summaryBlock.getTimeSlotLimit().getFromTimestampUTC());
                        }
                        if (!ObjectUtils.isEmpty(summaryBlock.getTimeSlotLimit().getToTimestampUTC())){
                            playerLimitSummary.setTimeSlotLimitEnd(summaryBlock.getTimeSlotLimit().getToTimestampUTC());
                        }
                    }
                    data.put("limitSummary", playerLimitSummary);
                }
            } catch (Status500LimitInternalSystemClientException e) {
                log.trace("Status500LimitInternalSystemClientException: message = " + e.getMessage());
            }
        }

    }

    private Optional<PlayerLimitFE> getDepositLimit(List<PlayerLimitFE> depositLimits, Granularity granularity) {
        if (!ObjectUtils.isEmpty(depositLimits)) {
            return depositLimits.stream().filter(playerLimit -> granularity.type().equals(playerLimit.getGranularity())).findAny();
        }
        return Optional.empty();
    }

    private Optional<PlayerLimitFE> getBalanceLimit(List<PlayerLimitFE> balanceLimits, LimitType limitType) {
        if (!ObjectUtils.isEmpty(balanceLimits)) {
            return balanceLimits.stream().filter(playerLimit -> limitType.name().equals(playerLimit.getType())).findAny();
        }
        return Optional.empty();
    }

    private Optional<PlayTimeLimitFE> getPlayTimeLimit(List<PlayTimeLimitFE> playTimeLimits, Granularity granularity) {
        if (!ObjectUtils.isEmpty(playTimeLimits)) {
            return playTimeLimits.stream().filter(playerLimit -> granularity.type().equals(playerLimit.getGranularity())).findAny();
        }
        return Optional.empty();
    }

    private void doUserEnhancements(User user, Map<String, Object> data) throws Status500LimitInternalSystemClientException {
        // Most user data is duplicated here (as well as lithium.server.oauth2.LithiumTokenEnhancer.enhance) so that it appears
        // both in the access_token as well as the response object.
        if (user != null) {
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("userGuid", user.guid());
            data.put("shortGuid", user.getShortGuid());
            data.put("firstName", user.getFirstName());
            data.put("lastName", user.getLastName());
            data.put("registrationDate", user.getCreatedDate());
            log.trace("user.getSession(): " + user.getSession());
            log.trace("user.getLastLogin(): " + user.getLastLogin());
            if (user.getSession() != null) {
                data.put("sessionId", user.getSession().getId());
                if (user.getLastLogin() != null) {
                    data.put("lastLogin", user.getLastLogin().getDate());
                    data.put("lastIP", user.getLastLogin().getIpAddress());
                }
            }
            if (user.getSession() == null) {
                data.put("sessionId", user.getLastLogin().getId());
            }
            data.put("optOutEmail", user.getEmailOptOut());
            data.put("optOutSms", user.getSmsOptOut());
            data.put("optOutCall", user.getCallOptOut());
            data.put("optOutPush", user.getPushOptOut());
            data.put("optOutPost", user.getPostOptOut());
            data.put("verificationLevel", limits.getVerificationStatusLevel(user.getVerificationStatus()));
            data.put("registrationDate", user.getCreatedDate());
            data.put("commsOptInComplete", user.getCommsOptInComplete());
        }
    }

    private void doAccessEnhancements(String userGuid, Map<String, Object> data) throws Status500LimitInternalSystemClientException {
        Access access = limits.checkAccess(userGuid);
        log.debug("Access :: " + access);
        data.put("restrictions", access);
    }

    private void doDomainEnhancements(User user, Domain domain, Map<String, Object> data) {
        if (domain != null) {
            String domainTcVersion = null;
            Optional<String> domainTcVersionSetting = domain.findDomainSettingByName(
                    DomainSettings.TERMS_AND_CONDITIONS_VERSION.name());
            if (domainTcVersionSetting.isPresent()) {
                domainTcVersion = domainTcVersionSetting.get();
            } else {
                domainTcVersion = DomainSettings.TERMS_AND_CONDITIONS_VERSION.defaultValue();
            }

            data.put("termsAndConditionsVersion", TermsAndConditionsVersion.builder().acceptedUserVersion(
                    user.getTermsAndConditionsVersion()).currentDomainVersion(domainTcVersion).build());
        }
    }

    public void validateAndUpdateSession(String token) throws Exception {
        try {
            Integer sessionId = (Integer) getClaimFromToken(token, TOKEN_CLAIM_SESSION_ID);
            String domainName = (String) getClaimFromToken(token, TOKEN_CLAIM_DOMAIN_NAME);
            log.trace("sessionId " + sessionId + " domainName " + domainName);
            if (sessionId == null || domainName == null) {
                throw new Exception("Unable to retrieve sessionId from token");
            }
            userApiInternalClientService.validateAndUpdateSession(domainName, sessionId.longValue());
        } catch (Status401UnAuthorisedException e) {
            // Invalid session. User logged out.
            throw e;
        } catch (Exception e) {
            log.error("validateRefreshToken failed | " + e.getMessage(), e);
            // General message returned to caller
            throw new Exception("Refresh token validation failed");
        }
    }

    private Object getClaimFromToken(String token, String key) throws IOException {
        JwtHelper jwtHelper = new JwtHelper();
        Jwt jwt = jwtHelper.decode(token);
        Map<String, String> map = mapper.readValue(jwt.getClaims(), Map.class);
        return map.getOrDefault(key, null);
    }

    private void doCheckForResetUserPlayTime(String userGuid) throws Status500LimitInternalSystemClientException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            userPlayTimeLimitsClientService.resetTimeUsedForUser(userGuid,timestamp);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status500LimitInternalSystemClientException(e);
        }
    }
}
