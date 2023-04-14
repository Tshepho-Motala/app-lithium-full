package lithium.service.access.provider.sphonic.iban.services;

import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.access.provider.sphonic.iban.ServiceAccessProviderSphonicIBANModuleInfo;
import lithium.service.access.provider.sphonic.iban.config.Configuration;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigurationService {
	@Autowired private ServiceAccessProviderSphonicIBANModuleInfo moduleInfo;
	@Autowired private ProviderClientService providerClientService;

	public Configuration getDomainConfiguration(String domainName) throws Status512ProviderNotConfiguredException,
			Status550ServiceDomainClientException {
		Iterable<ProviderProperty> properties = providerClientService.getProviderProperties(moduleInfo.getModuleName(),
				domainName);

		Configuration configuration = new Configuration();
        configuration.setProviderName(moduleInfo.getModuleName());
		for (ProviderProperty property: properties) {
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.AUTHENTICATION_URL.getName())) {
				configuration.setAuthenticationUrl(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.USERNAME.getName())) {
				configuration.setUsername(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.PASSWORD.getName())) {
				configuration.setPassword(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.MERCHANT_ID.getName())) {
				configuration.setMerchantId(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.IBAN_URL.getName())) {
				configuration.setIbanUrl(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.IBAN_WORKFLOW_NAME.getName())) {
				configuration.setIbanWorkflowName(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.IBAN_MODE.getName())) {
				configuration.setIbanMode(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.CONNECT_TIMEOUT.getName()) && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue())) {
				configuration.setConnectTimeout(Integer.parseInt(property.getValue()));
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getName()) && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue())) {
				configuration.setConnectionRequestTimeout(Integer.parseInt(property.getValue()));
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.SOCKET_TIMEOUT.getName()) && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue())) {
				configuration.setSocketTimeout(Integer.parseInt(property.getValue()));
			}
		}

		List<String> missingProperties = new ArrayList<>();
		if (configuration.getAuthenticationUrl() == null || configuration.getAuthenticationUrl().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.AUTHENTICATION_URL
					.getName());
		}
		if (configuration.getUsername() == null || configuration.getUsername().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.USERNAME.getName());
		}
		if (configuration.getPassword() == null || configuration.getPassword().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.PASSWORD.getName());
		}
		if (configuration.getMerchantId() == null || configuration.getMerchantId().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.MERCHANT_ID.getName());
		}
		if (configuration.getIbanUrl() == null || configuration.getIbanUrl().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.IBAN_URL.getName());
		}
		if (configuration.getIbanWorkflowName() == null || configuration.getIbanWorkflowName().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties
					.IBAN_WORKFLOW_NAME.getName());
		}
		if (configuration.getIbanMode() == null || configuration.getIbanMode().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicIBANModuleInfo.ConfigProperties.IBAN_MODE.getName());
		}
		if (!missingProperties.isEmpty()) {
			String missingPropertiesStr = missingProperties.stream().collect(Collectors.joining(", "));
			throw new IllegalArgumentException("One or more required configuration properties not set."
					+ " ["+missingPropertiesStr+"]");
		}

		return configuration;
	}
}
