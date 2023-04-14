package lithium.service.casino.provider.iforium.config;

import com.hazelcast.com.google.common.collect.ImmutableList;
import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.iforium.IforiumModuleInfo;
import lithium.service.casino.provider.iforium.exception.PropertyNotConfiguredException;
import lithium.service.casino.provider.iforium.util.DecryptUtils;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderConfigService {

    private final LithiumServiceClientFactory services;
    private final DecryptUtils decryptUtils;
    private final IforiumModuleInfo iforiumModuleInfo;

    private ProviderClient getProviderClientService(String domainName) throws Status512ProviderNotConfiguredException {
        try {
            return services.target(ProviderClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.debug(e.getMessage(), e);
            throw new Status512ProviderNotConfiguredException(domainName);
        }
    }

    private void checkIfProviderConfigured(ProviderClient providerClient,
                                           String domainName) throws Status512ProviderNotConfiguredException {
        Response<Provider> provider = providerClient.findByUrlAndDomainName(iforiumModuleInfo.getModuleName(), domainName);

        if (!provider.isSuccessful() || provider.getData() == null || !provider.getData().getEnabled()) {
            throw new Status512ProviderNotConfiguredException(domainName);
        }
    }

    private List<ProviderProperty> getProviderProperties(ProviderClient providerClient,
                                                         String domainName) throws Status512ProviderNotConfiguredException {
        Response<Iterable<ProviderProperty>> providerProperties = providerClient
                .propertiesByProviderUrlAndDomainName(iforiumModuleInfo.getModuleName(), domainName);

        if (!providerProperties.isSuccessful() || providerProperties.getData() == null) {
            throw new Status512ProviderNotConfiguredException(domainName);
        }

        return (List<ProviderProperty>) providerProperties.getData();
    }

    public IforiumProviderConfig getIforiumConfig(String domainName) throws Status512ProviderNotConfiguredException {
        ProviderClient providerClient = getProviderClientService(domainName);
        checkIfProviderConfigured(providerClient, domainName);
        List<ProviderProperty> providerProperties = getProviderProperties(providerClient, domainName);

        return fillProviderConfig(providerProperties);
    }

    private IforiumProviderConfig fillProviderConfig(List<ProviderProperty> providerProperties) {
        return IforiumProviderConfig.builder()
                                    .whitelistIPs(getWhitelistedIp(providerProperties))
                                    .secureUserName(
                                            decryptUtils.decrypt(getValue(ProviderConfigProperties.SECURITY_USER_NAME, providerProperties)))
                                    .secureUserPassword(decryptUtils.decrypt(
                                            getValue(ProviderConfigProperties.SECURITY_USER_PASSWORD, providerProperties)))
                                    .lobbyUrl(getValue(ProviderConfigProperties.LOBBY_URL, providerProperties))
                                    .casinoId(getValue(ProviderConfigProperties.CASINO_ID, providerProperties))
                                    .startGameUrl(getValue(ProviderConfigProperties.STARTGAME_BASE_URL, providerProperties))
                                    .listGameUrl(getValue(ProviderConfigProperties.LIST_GAME_URL, providerProperties))
                                    .regulationsEnabled(Boolean.parseBoolean(
                                            getValue(ProviderConfigProperties.REGULATIONS_ENABLED, providerProperties)))
                                    .regulationSessionDuration(Integer.parseInt(
                                            getValue(ProviderConfigProperties.REGULATION_SESSION_DURATION, providerProperties)))
                                    .regulationInterval(
                                            (Integer.parseInt(getValue(ProviderConfigProperties.REGULATION_INTERVAL, providerProperties))))
                                    .regulationGameHistoryUrl(
                                            getValue(ProviderConfigProperties.REGULATION_GAME_HISTORY_URL, providerProperties))
                                    .regulationBonusUrl(getValue(ProviderConfigProperties.REGULATION_BONUS_URL, providerProperties))
                                    .regulationOverrideRts13Mode(
                                            getValue(ProviderConfigProperties.REGULATION_OVERRIDE_RTS_13_MODE, providerProperties))
                                    .regulationOverrideCmaMode(
                                            getValue(ProviderConfigProperties.REGULATION_OVERRIDE_CMA_MODE, providerProperties))
                                    .blueprintProgressiveJackpotFeedUrl(
                                            getValue(ProviderConfigProperties.BLUEPRINT_JACKPOT_URL, providerProperties))
                                    .build();
    }

    public String getBlueprintJackpotUrl(String domainName) {
        ProviderClient providerClient = getProviderClientService(domainName);
        checkIfProviderConfigured(providerClient, domainName);
        List<ProviderProperty> providerProperties = getProviderProperties(providerClient, domainName);

        return getValue(ProviderConfigProperties.BLUEPRINT_JACKPOT_URL, providerProperties);
    }

    private static List<String> getWhitelistedIp(List<ProviderProperty> providerProperties) {
        return providerProperties.stream()
                                 .filter(pp -> ProviderConfigProperties.WHITELIST_IP.getName().equalsIgnoreCase(pp.getName()))
                                 .flatMap(pp -> Arrays.stream(pp.getValue().split(",")))
                                 .collect(ImmutableList.toImmutableList());
    }

    private static String getValue(ProviderConfigProperties property, List<ProviderProperty> providerProperties) {
        return providerProperties.stream()
                                 .filter(pp -> property.getName().equalsIgnoreCase(pp.getName()))
                                 .findFirst()
                                 .orElseThrow(() -> new PropertyNotConfiguredException(format("Property=%s is not configured in BO",
                                                                                              property.getName())))
                                 .getValue();
    }
}
