package lithium.service.casino.provider.incentive.config;

import lithium.service.Response;
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

    public ProviderConfig getConfig(String providerName, String domainName) throws Status500ProviderNotConfiguredException {
        ProviderClient cl = getProviderService();
        Response<Iterable<ProviderProperty>> pp =
                cl.propertiesByProviderUrlAndDomainName(providerName, domainName);

        if (!pp.isSuccessful() || pp.getData() == null) {
            throw new Status500ProviderNotConfiguredException();
        }

        ProviderConfig config = new ProviderConfig(); //external system id = providerId as stored in domain config
        for(ProviderProperty p: pp.getData()) {
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.HASH_PASSWORD.getValue())) config.setHashPassword(p.getValue());
        }

        if (config.getHashPassword() == null) {
            throw new Status500ProviderNotConfiguredException();
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
