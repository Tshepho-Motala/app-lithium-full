package lithium.service.access.provider.sphonic.cruks.services;

import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.access.provider.sphonic.cruks.ServiceAccessProviderSphonicCRUKSModuleInfo;
import lithium.service.access.provider.sphonic.cruks.config.Configuration;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConfigurationService {
	@Autowired private ServiceAccessProviderSphonicCRUKSModuleInfo moduleInfo;
	@Autowired private ProviderClientService providerClientService;

	public Configuration getDomainConfiguration(String domainName) throws Status512ProviderNotConfiguredException,
			Status550ServiceDomainClientException {
		Iterable<ProviderProperty> properties = providerClientService.getProviderProperties(moduleInfo.getModuleName(),
				domainName);

		Configuration configuration = new Configuration();
		for (ProviderProperty property: properties) {
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.AUTHENTICATION_URL.getName())) {
				configuration.setAuthenticationUrl(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.USERNAME.getName())) {
				configuration.setUsername(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.PASSWORD.getName())) {
				configuration.setPassword(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.MERCHANT_ID.getName())) {
				configuration.setMerchantId(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.CRUKS_URL.getName())) {
				configuration.setCruksUrl(property.getValue());
			}
			if (property.getName().contentEquals(ServiceAccessProviderSphonicCRUKSModuleInfo
					.ConfigProperties.CRUKS_REGISTRATION_WORKFLOW_NAME.getName())) {
				configuration.setCruksRegistrationWorkflowName(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.CRUKS_LOGIN_WORKFLOW_NAME.getName())) {
				configuration.setCruksLoginWorkflowName(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.CRUKS_MODE.getName())) {
				configuration.setCruksMode(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.CONNECT_TIMEOUT.getName()) && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue())) {
				configuration.setConnectTimeout(Integer.parseInt(property.getValue()));
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getName()) && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue())) {
				configuration.setConnectionRequestTimeout(Integer.parseInt(property.getValue()));
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.SOCKET_TIMEOUT.getName()) && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue())) {
				configuration.setSocketTimeout(Integer.parseInt(property.getValue()));
			}
		}

		List<String> missingProperties = new ArrayList<>();
		if (configuration.getAuthenticationUrl() == null || configuration.getAuthenticationUrl().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.AUTHENTICATION_URL
					.getName());
		}
		if (configuration.getUsername() == null || configuration.getUsername().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.USERNAME.getName());
		}
		if (configuration.getPassword() == null || configuration.getPassword().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.PASSWORD.getName());
		}
		if (configuration.getMerchantId() == null || configuration.getMerchantId().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.MERCHANT_ID.getName());
		}
		if (configuration.getCruksUrl() == null || configuration.getCruksUrl().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.CRUKS_URL.getName());
		}
		if (configuration.getCruksRegistrationWorkflowName() == null ||
				configuration.getCruksRegistrationWorkflowName().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties
					.CRUKS_REGISTRATION_WORKFLOW_NAME.getName());
		}
		if (configuration.getCruksLoginWorkflowName() == null ||
				configuration.getCruksLoginWorkflowName().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties
					.CRUKS_LOGIN_WORKFLOW_NAME.getName());
		}
		if (configuration.getCruksMode() == null || configuration.getCruksMode().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicCRUKSModuleInfo.ConfigProperties.CRUKS_MODE.getName());
		}
		if (!missingProperties.isEmpty()) {
			String missingPropertiesStr = String.join(", ", missingProperties);
			throw new IllegalArgumentException("One or more required configuration properties not set."
					+ " ["+missingPropertiesStr+"]");
		}

		return configuration;
	}
}
