package lithium.service.user.provider.sphonic.idin.services;

import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.user.provider.sphonic.idin.ServiceUserProviderSphonicIdinModuleInfo;
import lithium.service.user.provider.sphonic.idin.ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties;
import lithium.service.user.provider.sphonic.idin.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class IDINConfigurationService {

    @Autowired private ServiceUserProviderSphonicIdinModuleInfo sphonicProviderIdinModuleInfo;
    @Autowired private ProviderClientService providerClientService;
    private final String defaultConnection = "60000";

   public Configuration getDomainConfiguration(String domainName) throws Status512ProviderNotConfiguredException, Status550ServiceDomainClientException {
            Iterable<ProviderProperty> properties = providerClientService.getProviderProperties(sphonicProviderIdinModuleInfo.getModuleName(), domainName);
            Configuration.ConfigurationBuilder configuration = Configuration.builder();

            for(ProviderProperty property : properties) {
                if(property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.AUTHENTICATION_URL.getName())) {
                    configuration.authenticationUrl(property.getValue());
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.USERNAME.getName())) {
                    configuration.username(property.getValue());
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.PASSWORD.getName())) {
                    configuration.password(property.getValue());
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.MERCHANT_ID.getName())) {
                    configuration.merchantId(property.getValue());
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.IDIN_URL.getName())) {
                    configuration.iDinUrl(property.getValue());
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.IDIN_RETRIEVE_WORKFLOW_NAME.getName())) {
                    configuration.iDinRetrieveWorkflowName(property.getValue());
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.IDIN_START_WORKFLOW_NAME.getName())) {
                    configuration.iDinStartWorkflowName(property.getValue());
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.APPLICANT_REFERENCE_OFFSET.getName())) {
                    configuration.applicantReferenceOffset(property.getValue());
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getName())) {
                    configuration.connectionRequestTimeout(Integer.parseInt(!ObjectUtils.isEmpty(property.getValue()) ? property.getValue() : defaultConnection));
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.CONNECTION_TIMEOUT.getName())) {
                    configuration.connectionTimeout(Integer.parseInt(!ObjectUtils.isEmpty(property.getValue()) ? property.getValue() : defaultConnection));
                } else if (property.getName().contentEquals(ServiceUserProviderSphonicIdinModuleInfo.ConfigProperties.SOCKET_TIMEOUT.getName())) {
                    configuration.socketTimeout(Integer.parseInt(!ObjectUtils.isEmpty(property.getValue()) ? property.getValue() : defaultConnection));
                } else if (property.getName().contentEquals(ConfigProperties.APPLICANT_HASH_KEY.getName())) {
                  configuration.applicantHashKey(property.getValue());
                }
            }

            return configuration.build();
        }
}
