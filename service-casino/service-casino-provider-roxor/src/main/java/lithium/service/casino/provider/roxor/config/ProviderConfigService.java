package lithium.service.casino.provider.roxor.config;

import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProviderConfigService {
    @Autowired
    LithiumServiceClientFactory services;

    public ProviderConfig getConfig(
            String providerName,
            String domainName
    ) throws
            Status512ProviderNotConfiguredException
    {
        ProviderClient cl = getProviderService();
        if (cl == null) {
            throw new Status512ProviderNotConfiguredException(domainName);
        }

        Response<Provider> provider = cl.findByUrlAndDomainName(providerName, domainName);
        if (!provider.isSuccessful() || provider.getData() == null) {
            throw new Status512ProviderNotConfiguredException(domainName);
        }

        if (!provider.getData().getEnabled()) {
            throw new Status512ProviderNotConfiguredException(domainName);
        }

        Response<Iterable<ProviderProperty>> pp =
                cl.propertiesByProviderUrlAndDomainName(providerName, domainName);

        if (!pp.isSuccessful() || pp.getData() == null) {
            throw new Status512ProviderNotConfiguredException(domainName);
        }

        ProviderConfig config = new ProviderConfig();
        for (ProviderProperty p : pp.getData()) {
            if (p.getName().equalsIgnoreCase(ProviderConfigProperties.LAUNCH_URL.getValue()))
                config.setLaunchURL(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.SOUND.getValue()))
                config.setSound(getBooleanValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.HOME_BUTTON.getValue()))
                config.setHomeButton(getBooleanValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.HOME_POS.getValue()))
                config.setHomePos(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BALANCE_POS.getValue()))
                config.setBalancePos(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.CHAT_HOST.getValue()))
                config.setChatHost(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.CHAT_CONTEXT.getValue()))
                config.setChatContext(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.SESSION_REMINDER_INTERVAL.getValue()))
                config.setSessionReminderInterval(getIntegerValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.SESSION_ELAPSED.getValue()))
                config.setSessionElapsed(getIntegerValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.HOME_PAGE_URL.getValue()))
                config.setHomePageURL(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.DEPOSIT_PAGE_URL.getValue()))
                config.setDepositPageURL(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.LOBBY_PAGE_URL.getValue()))
                config.setLobbyPageURL(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.TRANSACTION_URL.getValue()))
                config.setTransactionURL(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.LOGOUT_URL.getValue()))
                config.setLogoutURL(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.LOGIN_PAGE_URL.getValue()))
                config.setLoginPageURL(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.GAME_API_URL.getValue()))
                config.setGameApiUrl(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.WEBSITE.getValue()))
                config.setWebsite(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.IP_WHITE_LIST.getValue()))
                config.setIpWhiteList(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.GAME_LIST_URL.getValue()))
                config.setGameListUrl(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.REWARDS_URL.getValue()))
                config.setRewardsUrl(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.REWARDS_DEFAULT_DURATION_IN_HOURS.getValue()))
                config.setRewardsDefaultDurationInHours(getIntegerValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.PROGRESSIVE_URL.getValue()))
                config.setProgressiveUrl(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.ADD_CLOCK.getValue()))
                config.setAddClock(getBooleanValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BET_HISTORY_ROUND_DETAIL_URL.getValue()))
                config.setBetHistoryRoundDetailUrl(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BET_HISTORY_ROUND_DETAIL_PROVIDER_ID.getValue()))
                config.setBetHistoryRoundDetailPid(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.USE_PLAYER_API_TOKEN.getValue()))
                config.setUsePlayerApiToken(getBooleanValueFromPropertyString(p.getValue()));
            else if(p.getName().equalsIgnoreCase(ProviderConfigProperties.USE_PLAYER_ID_FROM_GUID.getValue()))
                config.setUsePlayerIdFromGuid(getBooleanValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.BET_ROUND_REPLAY_URL.getValue()))
                config.setBetRoundReplayUrl(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.FREE_GAMES_URL.getValue()))
                config.setFreeGamesUrl(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.EVOLUTION_DIRECT_GAME_LAUNCH_API_URL.getValue()))
                config.setEvolutionDirectGameLaunchApiUrl(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.EVOLUTION_DIRECT_GAME_LAUNCH_API_CASINO_ID.getValue()))
                config.setEvolutionDirectGameLaunchApiCasinoId(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.EVOLUTION_DIRECT_GAME_LAUNCH_API_USERNAME.getValue()))
                config.setEvolutionDirectGameLaunchApiUsername(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.EVOLUTION_DIRECT_GAME_LAUNCH_API_PASSWORD.getValue()))
                config.setEvolutionDirectGameLaunchApiPassword(getStringValueFromPropertyString(p.getValue()));
            else if (p.getName().equalsIgnoreCase(ProviderConfigProperties.MICRO_GAMING_PROGRESSIVE_JACKPOT_FEED_URL.getValue()))
                config.setMicroGamingProgressiveJackpotFeedUrl(getStringValueFromPropertyString(p.getValue()));
        }
        config.setCurrency(provider.getData().getDomain().getCurrency());
        config.setCountry(provider.getData().getDomain().getDefaultCountry());
        config.setLanguage(provider.getData().getDomain().getDefaultLocale().split("-")[0]);
        return config;
    }

    public EvolutionDirectGameLaunchApiProviderConfig getEvolutionDirectGameLaunchApiProviderConfigs(
            String providerName,
            String domainName
    ) throws
            Status512ProviderNotConfiguredException {
        ProviderConfig config = getConfig(providerName, domainName);
        if (config.getEvolutionDirectGameLaunchApiUrl() == null
                && config.getEvolutionDirectGameLaunchApiCasinoId() == null
                && config.getEvolutionDirectGameLaunchApiUsername() == null
                && config.getEvolutionDirectGameLaunchApiUsername() == null) {
            return null;
        }
        return EvolutionDirectGameLaunchApiProviderConfig.builder()
                .url(config.getEvolutionDirectGameLaunchApiUrl())
                .casinoId(config.getEvolutionDirectGameLaunchApiCasinoId())
                .username(config.getEvolutionDirectGameLaunchApiUsername())
                .password(config.getEvolutionDirectGameLaunchApiPassword())
                .build();
    }

    private ProviderClient getProviderService() {
        ProviderClient cl = null;
        try {
            cl = services.target(ProviderClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting provider properties", e);
        }
        return cl;
    }

    private String getStringValueFromPropertyString(String stringValue) {
        if (stringValue != null && !stringValue.trim().isEmpty()) {
            return stringValue;
        }

        return null;
    }

    private Boolean getBooleanValueFromPropertyString(String stringValue) {
        if (stringValue != null && !stringValue.trim().isEmpty()) {
            return Boolean.parseBoolean(stringValue);
        }

        return Boolean.FALSE;
    }

    private Integer getIntegerValueFromPropertyString(String stringValue) {
        if (stringValue != null && !stringValue.trim().isEmpty()) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }


}
