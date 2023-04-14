package lithium.service.casino.provider.sportsbook.config;

import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProviderConfigService {

    @Autowired
    private LithiumServiceClientFactory services;

    public ProviderConfig getConfig(String providerName, String domainName) throws Status512ProviderNotConfiguredException {
        ProviderClient cl = getProviderService();
        Response<Iterable<ProviderProperty>> pp =
                cl.propertiesByProviderUrlAndDomainName(providerName, domainName);

        if (!pp.isSuccessful() || pp.getData() == null) {
            throw new Status512ProviderNotConfiguredException(domainName);
        }

        ProviderConfig config = new ProviderConfig(); //external system id = providerId as stored in domain config
        for(ProviderProperty p: pp.getData()) {
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.HASH_PASSWORD.getValue())) config.setHashPassword(p.getValue());
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.EXTERNAL_TRANSACTION_INFO_URL.getValue())) config.setExternalTransactionInfoUrl(p.getValue());
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.PLAYER_OFFSET.getValue())) config.setPlayerOffset(p.getValue());
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.BETSEARCH_URL.getValue())) config.setBetSearchUrl(p.getValue());
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.BETSEARCH_KEY.getValue())) config.setBetSearchKey(p.getValue());
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.BETSEARCH_BRAND.getValue())) config.setBetSearchBrand(p.getValue());
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.SPORTS_FREE_BETS_URL.getValue())) config.setSportsFreeBetsUrl(p.getValue());
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.BONUS_RESTRICTION_URL.getValue())) config.setBonusRestrictionUrl(p.getValue());
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.BONUS_RESTRICTION_KEY.getValue())) config.setBonusRestrictionKey(p.getValue());
        }

        // While not having an info url is detrimental to backoffice, it is not critical for player activities
        // and should thus not break betting.
        if (config.getExternalTransactionInfoUrl() == null) {
            log.warn("providerconfig invalid externaltransactioninfourl " + config + " " + pp);
        }

        if (config.getHashPassword() == null) {
            log.warn("providerconfig invalid hash password " + config + " " + pp);
            throw new Status512ProviderNotConfiguredException(domainName);
        }

        //IF we have configured a bet search URL but not provided a key, error out
        if (config.getBetSearchUrl() != null && config.getBetSearchKey() == null) {
            log.warn("providerconfig invalid bet key password " + config + " " + pp);
            throw new Status512ProviderNotConfiguredException(domainName);
        }

        return config;
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

}
