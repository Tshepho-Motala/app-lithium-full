package lithium.service.casino.provider.roxor.services;

import lithium.config.LithiumConfigurationProperties;
import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.config.ProviderConfig;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.entities.User;
import lithium.service.casino.provider.roxor.storage.repositories.DomainRepository;
import lithium.service.casino.provider.roxor.storage.repositories.UserRepository;
import lithium.service.casino.provider.roxor.util.CountryService;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.service.LoginEventClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class StartGameService {
    @Autowired ValidationHelper validationHelper;
    @Autowired LithiumConfigurationProperties lithiumConfigurationProperties;
    @Autowired ProviderConfigService providerConfigService;
    @Autowired ModuleInfo moduleInfo;
    @Autowired LithiumServiceClientFactory services;
    @Autowired LimitInternalSystemService limits;
    @Autowired TokenStore tokenStore;
    @Autowired CountryService countryService;
    @Autowired UserRepository userRepository;
    @Autowired DomainRepository domainRepository;
    @Autowired LoginEventClientService loginEventHelperService;

    public String startGame(
            String domainName, String token, String gameId, String lang,
            String currency, Boolean demoMode, String platform
    ) throws
            Status500LimitInternalSystemClientException,
            Status500RuntimeException,
            Status512ProviderNotConfiguredException,
            Status401NotLoggedInException,
            UnsupportedEncodingException
    {
        //Check for casino allowed
        if (!demoMode) {
            validateUserToken(token);
        }

        ProviderConfig pc = providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);
        String gatewayPublicUrl = lithiumConfigurationProperties.getGatewayPublicUrl().endsWith("/") ?
                lithiumConfigurationProperties.getGatewayPublicUrl() : lithiumConfigurationProperties.getGatewayPublicUrl() + "/";

        String returnUrl = gatewayPublicUrl + moduleInfo.getModuleName() + "/#!?url=";
        String redirectURL = "";
        redirectURL += pc.getLaunchURL();
        redirectURL += "?website=" + pc.getWebsite();

        StringBuilder sb = new StringBuilder();
        appendParam(sb, "sound", pc.getSound());
        appendParam(sb, "homeButton", pc.getHomeButton());
        appendParam(sb, "homePos", pc.getHomePos());
        appendParam(sb, "homePageURL", pc.getHomePageURL());
        appendParam(sb, "balancePos", pc.getBalancePos());
        appendParam(sb, "chatHost", pc.getChatHost());
        appendParam(sb, "chatContext", pc.getChatContext());
        appendParam(sb, "sessionReminderInterval", pc.getSessionReminderInterval());
        appendParam(sb, "sessionElapsed", pc.getSessionElapsed());
        appendParam(sb, "transactionURL", pc.getTransactionURL());
        appendParam(sb, "logoutURL", pc.getLogoutURL());
        appendParam(sb, "depositPageURL", pc.getDepositPageURL());
        appendParam(sb, "lobbyPageURL", pc.getLobbyPageURL());
        appendParam(sb, "loginPageURL", pc.getLoginPageURL());
        appendParam(sb, "gameKey", gameId);
        appendParam(sb, "language", lang);
        appendParam(sb, "clock", pc.getAddClock() != null ? pc.getAddClock() : true);

        Domain domain = validationHelper.getDomain(new GamePlayContext(), domainName);
        lithium.service.casino.provider.roxor.storage.entities.Domain domainEntity =
                    domainRepository.findOrCreateByName(domainName, () ->
                        new lithium.service.casino.provider.roxor.storage.entities.Domain());

        if (domain != null) {
            appendParam(sb, "currency", domain.getCurrency());
            appendParam(sb, "country", countryService.iso3CountryCodeToIso2CountryCode(domain.getDefaultCountry()));
        }

        if (demoMode) {
            appendParam(sb, "playMode", "GUEST");
        } else {
            String userGuid = this.getUserGuidFromAuthToken(token);
            String userApiToken = this.getUserApiTokenFromAuthToken(token);
            Long userId = this.getUserIdFromAuthToken(token);
            Boolean usePlayerApiToken = pc.getUsePlayerApiToken() != null ? pc.getUsePlayerApiToken() : Boolean.FALSE;

            if (usePlayerApiToken) {
                User user = userRepository.findOrCreateByGuid(userGuid, () ->
                        User.builder()
                                .guid(userGuid)
                                .apiToken(userApiToken)
                                .domain(domainEntity)
                                .build());

                if (!user.getApiToken().equalsIgnoreCase(userApiToken)) {
                    user.setApiToken(userApiToken);
                    userRepository.save(user);
                }
                appendParam(sb, "playerId", userApiToken);
            } else {
                User user = userRepository.findOrCreateByGuid(userGuid, () ->
                        User.builder()
                                .guid(userGuid)
                                .apiToken(userId.toString())
                                .domain(domainEntity)
                                .build());

                if (!user.getApiToken().equalsIgnoreCase(userId.toString())) {
                    user.setApiToken(userId.toString());
                    userRepository.save(user);
                }
                appendParam(sb, "playerId", userId.toString());
            }
            appendParam(sb, "playerName", getUserDisplayNameFromToken(token));
            appendParam(sb, "playMode", "CASH");
            appendParam(sb, "sessionKey", getUserSessionKeyFromAuthToken(token));
        }

        if (pc.getChatHost() != null && pc.getChatContext() != null) {
            appendParam(sb, "chat", true);
        }

        appendParam(sb, "platform", platform);
        redirectURL += sb.toString();
        redirectURL = URLEncoder.encode(redirectURL, StandardCharsets.UTF_8.name());

        log.info("start game request gameId " + gameId + " redirecting to url " + returnUrl);
        return returnUrl+redirectURL;
    }

    private String getUserGuidFromAuthToken(String authToken) {
        LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
        String userGuid = util.getJwtUser().getGuid();
        return userGuid;
    }

    private Long getUserIdFromAuthToken(String authToken) {
        LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
        Long userId = util.getJwtUser().getId();
        return userId;
    }

    private String getUserApiTokenFromAuthToken(String authToken) {
        LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
        String apiToken = util.getJwtUser().getApiToken();
        return apiToken;
    }

    private String getUserSessionKeyFromAuthToken(String authToken) {
        LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
        String sessionKey = util.getAccessToken().getAdditionalInformation().get("sessionKey").toString();
        return sessionKey;
    }

    private String getUserDisplayNameFromToken(String authToken) {
        LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
        String displayName = util.getJwtUser().getUsername();
        return displayName;
    }

    private Date getSessionExpiryDateFromToken(String authToken) {
        LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
        Date sessionExpiryDate = util.getAccessToken().getExpiration();
        return sessionExpiryDate;
    }

    private void appendParam(StringBuilder sb, String key, String value) throws UnsupportedEncodingException {
        if (value != null) {
            sb.append("&").append(key).append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
        }
    }

    private void appendParam(StringBuilder sb, String key, Boolean value) {
        if (value != null) {
            sb.append("&").append(key).append("=").append(value);
        }
    }

    private void appendParam(StringBuilder sb, String key, Integer value) {
        if (value != null) {
            sb.append("&").append(key).append("=").append(value);
        }
    }

    private void validateUserToken(
            String token
    ) throws
            Status401NotLoggedInException,
            Status500RuntimeException
    {
        Date sessionExpiryDateTime = getSessionExpiryDateFromToken(token);
        if (sessionExpiryDateTime != null
                && sessionExpiryDateTime.before(Calendar.getInstance().getTime())
        ) {
            throw new Status401NotLoggedInException();
        }

        LoginEvent loginEvent = validationHelper.findLastLoginEventForSessionKey(
                new GamePlayContext(),
                getUserSessionKeyFromAuthToken(token)
        );

        if (loginEvent == null) {
            throw new Status401NotLoggedInException();
        }
    }
}
