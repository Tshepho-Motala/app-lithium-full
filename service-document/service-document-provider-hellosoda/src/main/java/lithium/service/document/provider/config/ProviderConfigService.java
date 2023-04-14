package lithium.service.document.provider.config;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.document.provider.api.exceptions.Status540ProviderNotConfiguredException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProviderConfigService {

    @Autowired
    private LithiumServiceClientFactory services;

    public ProviderConfig getConfig(String providerName, String domainName) throws Status540ProviderNotConfiguredException {
        ProviderClient providerService = getProviderService();

        Response<Provider> providerResponse = providerService.findByUrlAndDomainName(providerName, domainName);
        if (!providerResponse.isSuccessful() || !providerResponse.getData().getEnabled()) {
            log.warn("The provider disabled for this domain (" + providerName + "," + domainName + ")");
            throw new Status540ProviderNotConfiguredException("The provider disabled for this domain");
        }

        List<ProviderProperty> providerProperties = providerResponse.getData().getProperties();

        if (providerProperties == null || providerProperties.isEmpty()) {
            log.warn("Missing provider properties for this domain (" + providerName + "," + domainName + ")");
            throw new Status540ProviderNotConfiguredException("The provider is not configured for this domain");
        }

        ProviderConfig config = new ProviderConfig();

        for (ProviderProperty providerProperty : providerProperties) {
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.PROFILE_BEARER.getValue()))
                config.setProfileBearer(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.PRODUCT_ID.getValue()))
                config.setProductId(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.PROFILE_API_URL.getValue()))
                config.setProfileApiUrl(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.I_DOCUFY_API_URL.getValue()))
                config.setIDocufyApiUrl(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.PROFILE_API_V1_URL.getValue()))
                config.setProfileApiV1Url(providerProperty.getValue());
        }

        if (config.getIDocufyApiUrl() == null || config.getProductId() == null
                || config.getProfileApiUrl() == null || config.getProfileBearer() == null) {
            log.warn("The provider is not configured for this domain (" + providerName + "," + domainName + ")");
            throw new Status540ProviderNotConfiguredException("The provider is not configured for this domain");
        }

        return config;
    }

    private ProviderClient getProviderService() throws Status540ProviderNotConfiguredException {
        ProviderClient cl = null;
        try {
            cl = services.target(ProviderClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting provider properties", e);
            throw new Status540ProviderNotConfiguredException("The provider is not configured for this domain");
        }
        return cl;
    }

}
