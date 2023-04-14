package lithium.service.casino.provider.iforium.service.impl;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.IforiumModuleInfo;
import lithium.service.casino.provider.iforium.config.IforiumProviderConfig;
import lithium.service.casino.provider.iforium.config.ProviderConfigService;
import lithium.service.casino.provider.iforium.model.request.Channel;
import lithium.service.casino.provider.iforium.model.request.CreateSessionTokenRequest;
import lithium.service.casino.provider.iforium.service.SessionService;
import lithium.service.casino.provider.iforium.service.StartGameService;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static lithium.service.casino.provider.iforium.model.request.Channel.DESKTOP;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceGameServiceImpl implements StartGameService {

    private static final String DEVICE_CHANNEL_WEB = "web";

    private final TokenStore tokenStore;

    private final CachingDomainClientService cachingDomainClientService;

    private final LithiumConfigurationProperties lithiumConfigurationProperties;

    private final ProviderConfigService providerConfigService;

    private final IforiumModuleInfo iforiumModuleInfo;

    private final SessionService sessionService;

    private final UserApiInternalClientService userService;

    private final LimitInternalSystemService limitInternalSystemService;

    public String startGame(String token, String gameId, String domainName, boolean demoGame, String platform, String lang
    ) throws
            UnsupportedEncodingException,
            Status550ServiceDomainClientException,
            Status512ProviderNotConfiguredException,
            Status411UserNotFoundException,
            LithiumServiceClientFactoryException,
            UserClientServiceFactoryException,
            UserNotFoundException,
            Status483PlayerCasinoNotAllowedException {
        String sessionToken = demoGame ? null : getSessionToken(token, gameId);
        if(!demoGame){
            checkUserCasinoBlock(token);
        }
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        IforiumProviderConfig iforiumProviderConfig = providerConfigService.getIforiumConfig(domain.getName());
        String redirectUrl = buildRedirectUrl(iforiumProviderConfig, sessionToken, gameId, lang, demoGame, platform, domain);
        return buildGatewayPublicUrl(lithiumConfigurationProperties, iforiumModuleInfo, redirectUrl);
    }
    private void checkUserCasinoBlock(String token) {
        LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, token).build();
        String userGuid = tokenUtil.getJwtUser().getGuid();
        limitInternalSystemService.checkPlayerCasinoAllowed(userGuid);
    }

    private String getSessionToken(String token, String gameId
    ) throws
            UserClientServiceFactoryException,
            UserNotFoundException,
            Status411UserNotFoundException,
            LithiumServiceClientFactoryException {
        LithiumTokenUtil lithiumTokenUtil = LithiumTokenUtil.builder(tokenStore, token).build();
        User user = userService.getUserByGuid(lithiumTokenUtil.guid());
        return sessionService.createToken(new CreateSessionTokenRequest(user.getGuid(), gameId)).getResult().getSessionToken();
    }

    private static String buildGatewayPublicUrl(LithiumConfigurationProperties lithiumConfigurationProperties,
                                                IforiumModuleInfo iforiumModuleInfo, String redirectUrl) {
        return UriComponentsBuilder.fromUriString(lithiumConfigurationProperties.getGatewayPublicUrl())
                                   .path(iforiumModuleInfo.getModuleName())
                                   .path("/#!")
                                   .queryParam("url", redirectUrl)
                                   .build().toUriString();
    }

    private static String buildRedirectUrl(IforiumProviderConfig iforiumProviderConfig, String sessionToken, String gameId,
                                           String languageCode, boolean isDemo, String platform, Domain domain
    ) throws UnsupportedEncodingException {
        UriComponentsBuilder redirectUrl = UriComponentsBuilder.fromUriString(iforiumProviderConfig.getStartGameUrl())
                                                               .queryParam("casinoid", iforiumProviderConfig.getCasinoId())
                                                               .queryParam("sessiontoken", sessionToken)
                                                               .queryParam("gameid", gameId)
                                                               .queryParam("languagecode", languageCode)
                                                               .queryParam("playmode", isDemo ? "demo" : "real")
                                                               .queryParam("channelid", Channel.valueOf(platform, DESKTOP).getChannelId())
                                                               .queryParam("devicechannel", DEVICE_CHANNEL_WEB)
                                                               .queryParam("lobbyurl", iforiumProviderConfig.getLobbyUrl())
                                                               .queryParam("currencycode", domain.getCurrency())
                                                               .queryParam("regulationsenabled",
                                                                           iforiumProviderConfig.isRegulationsEnabled());

        if (iforiumProviderConfig.isRegulationsEnabled()) {
            redirectUrl.queryParam("reg_override_rts13mode", iforiumProviderConfig.getRegulationOverrideRts13Mode())
                       .queryParam("reg_override_cmamode", iforiumProviderConfig.getRegulationOverrideCmaMode())
                       .queryParam("reg_sessionduration", iforiumProviderConfig.getRegulationSessionDuration())
                       .queryParam("reg_interval", iforiumProviderConfig.getRegulationInterval())
                       .queryParam("reg_gamehistoryurl", iforiumProviderConfig.getRegulationGameHistoryUrl())
                       .queryParam("reg_bonusurl", iforiumProviderConfig.getRegulationBonusUrl());
        }

        log.info("start game request gameId " + gameId + " redirecting to url " + redirectUrl);

        return URLEncoder.encode(redirectUrl.build().toUriString(), StandardCharsets.UTF_8.name());
    }
}
