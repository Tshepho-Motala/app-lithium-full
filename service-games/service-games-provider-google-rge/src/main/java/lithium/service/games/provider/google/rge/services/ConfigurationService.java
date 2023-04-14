package lithium.service.games.provider.google.rge.services;

import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.games.provider.google.rge.ServiceGamesProviderGoogleRgeModuleInfo;
import lithium.service.games.provider.google.rge.configs.ProviderConfigProperties;
import lithium.service.games.provider.google.rge.data.objects.ProviderSettingAuth;
import lithium.service.games.provider.google.rge.data.objects.ProviderSettingPredict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConfigurationService {

    @Autowired
    private ServiceGamesProviderGoogleRgeModuleInfo moduleInfo;

    @Autowired private ProviderClientService providerClientService;

    public ProviderSettingAuth getAuthPropertiesByDomain(String domainName) throws Status512ProviderNotConfiguredException,
            Status550ServiceDomainClientException {
        Iterable<ProviderProperty> properties = providerClientService.getProviderProperties(moduleInfo.getModuleName(),
                domainName);

        ProviderSettingAuth providerSettingAuth = new ProviderSettingAuth();
        for (ProviderProperty property : properties) {
            if (property.getName().contentEquals(ProviderConfigProperties.TYPE.getName()))
                providerSettingAuth.setType(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.PROJECT_ID.getName()))
                providerSettingAuth.setProjectId(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.PRIVATE_KEY_ID.getName()))
                providerSettingAuth.setPrivateKeyId(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.PRIVATE_KEY.getName()))
                providerSettingAuth.setPrivateKey(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.CLIENT_EMAIL.getName()))
                providerSettingAuth.setClientEmail(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.CLIENT_ID.getName()))
                providerSettingAuth.setClientId(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.AUTH_URI.getName()))
                providerSettingAuth.setAuthUri(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.TOKEN_URI.getName()))
                providerSettingAuth.setTokenUri(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.AUTH_PROVIDER_X509_CERT_URL.getName()))
                providerSettingAuth.setAuthProviderX509CertUrl(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.CLIENT_X509_CERT_URL.getName()))
                providerSettingAuth.setClientX509CertUrl(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.BUCKET_NAME.getName()))
                providerSettingAuth.setBucketName(property.getValue());
        }

        validateGoogleCredentialsProperties(providerSettingAuth);

        return providerSettingAuth;
    }

    private void validateGoogleCredentialsProperties(ProviderSettingAuth providerSettingAuth) {
        List<String> missingProperties = new ArrayList<>();
        if (providerSettingAuth.getType() == null || providerSettingAuth.getType().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.TYPE.getName());
        }
        if (providerSettingAuth.getProjectId() == null || providerSettingAuth.getProjectId().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.PROJECT_ID.getName());
        }
        if (providerSettingAuth.getPrivateKeyId() == null || providerSettingAuth.getPrivateKeyId().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.PRIVATE_KEY_ID.getName());
        }
        if (providerSettingAuth.getPrivateKey() == null || providerSettingAuth.getPrivateKey().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.PRIVATE_KEY.getName());
        }
        if (providerSettingAuth.getClientEmail() == null || providerSettingAuth.getClientEmail().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.CLIENT_EMAIL.getName());
        }
        if (providerSettingAuth.getClientId() == null || providerSettingAuth.getClientId().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.CLIENT_ID.getName());
        }
        if (providerSettingAuth.getAuthUri() == null || providerSettingAuth.getAuthUri().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.AUTH_URI.getName());
        }
        if (providerSettingAuth.getTokenUri() == null || providerSettingAuth.getTokenUri().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.TOKEN_URI.getName());
        }
        if (providerSettingAuth.getAuthProviderX509CertUrl() == null || providerSettingAuth.getAuthProviderX509CertUrl().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.AUTH_PROVIDER_X509_CERT_URL.getName());
        }
        if (providerSettingAuth.getClientX509CertUrl() == null || providerSettingAuth.getClientX509CertUrl().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.CLIENT_X509_CERT_URL.getName());
        }

        if (!missingProperties.isEmpty()) {
            String missingPropertiesStr = String.join(", ", missingProperties);
            throw new IllegalArgumentException("One or more required configuration properties not set."
                    + " ["+missingPropertiesStr+"]");
        }

    }

    public ProviderSettingPredict getPredictConfigurationByDomain(String domainName) throws Status512ProviderNotConfiguredException,
            Status550ServiceDomainClientException {
        Iterable<ProviderProperty> properties = providerClientService.getProviderProperties(moduleInfo.getModuleName(),
                domainName);

        ProviderSettingPredict providerSettingPredict = new ProviderSettingPredict();
        for (ProviderProperty property : properties) {
            if (property.getName().contentEquals(ProviderConfigProperties.PREDICT_URL.getName()))
                providerSettingPredict.setPredictURL(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.PROJECT.getName()))
                providerSettingPredict.setProject(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.LOCATION.getName()))
                providerSettingPredict.setLocation(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.ENDPOINT.getName()))
                providerSettingPredict.setEndpoint(property.getValue());
            else if (property.getName().contentEquals(ProviderConfigProperties.PAGE_SIZE.getName()))
                providerSettingPredict.setPageSize(getIntegerValueFromPropertyString(property.getValue()));

        }

        validateGoogleRGEProperties(providerSettingPredict);

        return providerSettingPredict;
    }

    private void validateGoogleRGEProperties(ProviderSettingPredict providerSettingPredict) {
        List<String> missingProperties = new ArrayList<>();

        if (providerSettingPredict.getPredictURL() == null || providerSettingPredict.getPredictURL().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.PREDICT_URL.getName());
        }
        if (providerSettingPredict.getProject() == null || providerSettingPredict.getProject().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.PROJECT.getName());
        }
        if (providerSettingPredict.getLocation() == null || providerSettingPredict.getLocation().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.LOCATION.getName());
        }
        if (providerSettingPredict.getEndpoint() == null || providerSettingPredict.getEndpoint().trim().isEmpty()) {
            missingProperties.add(ProviderConfigProperties.ENDPOINT.getName());
        }

        if (!missingProperties.isEmpty()) {
            String missingPropertiesStr = String.join(", ", missingProperties);
            throw new IllegalArgumentException("One or more required configuration properties not set."
                    + " ["+missingPropertiesStr+"]");
        }


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
