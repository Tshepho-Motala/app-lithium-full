package lithium.service.user.provider.threshold.config;

import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.mail.client.exceptions.Status500ProviderNotConfiguredException;
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
        if(cl==null){
            throw new Status512ProviderNotConfiguredException(domainName);
        }
        Response<Iterable<ProviderProperty>> pp =
                cl.propertiesByProviderUrlAndDomainName(providerName, domainName);

        if (!pp.isSuccessful() || pp.getData() == null) {
            throw new Status500ProviderNotConfiguredException();
        }

        ProviderConfig config = new ProviderConfig(); //external system id = providerId as stored in domain config
        for(ProviderProperty p: pp.getData()) {
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.EXTREME_PUSH_API_URL.getValue())) config.setExtremePushApiUrl(p.getValue());
            if(p.getName().equalsIgnoreCase(ProviderConfigProperties.EXTREME_PUSH_APP_TOKEN.getValue())) config.setExtremePushAppToken(p.getValue());
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
