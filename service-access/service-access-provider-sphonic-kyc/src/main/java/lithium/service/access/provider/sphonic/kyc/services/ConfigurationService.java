package lithium.service.access.provider.sphonic.kyc.services;

import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.access.provider.sphonic.kyc.ServiceAccessProviderSphonicKYCModuleInfo;
import lithium.service.access.provider.sphonic.kyc.config.Configuration;
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
	@Autowired private ServiceAccessProviderSphonicKYCModuleInfo moduleInfo;
	@Autowired private ProviderClientService providerClientService;

	public Configuration getDomainConfiguration(String domainName) throws Status512ProviderNotConfiguredException,
			Status550ServiceDomainClientException {
		Iterable<ProviderProperty> properties = providerClientService.getProviderProperties(moduleInfo.getModuleName(),
				domainName);

		Configuration configuration = new Configuration();
		for (ProviderProperty property: properties) {
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.AUTHENTICATION_URL.getName())) {
				configuration.setAuthenticationUrl(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.USERNAME.getName())) {
				configuration.setUsername(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.PASSWORD.getName())) {
				configuration.setPassword(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.MERCHANT_ID.getName())) {
				configuration.setMerchantId(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.KYC_URL.getName())) {
				configuration.setKycUrl(property.getValue());
			}
			if (property.getName().contentEquals(
				ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.PARTIAL_VERIFICATION.getName())) {
				configuration.setPartialVerification(Boolean.parseBoolean(property.getValue()));
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.KYC_WORKFLOW_NAME.getName())) {
				configuration.setKycWorkflowName(property.getValue());
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.CONNECT_TIMEOUT.getName())  && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue())) {
				configuration.setConnectTimeout(Integer.parseInt(property.getValue()));
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getName())  && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue())) {
				configuration.setConnectionRequestTimeout(Integer.parseInt(property.getValue()));
			}
			if (property.getName().contentEquals(
					ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.SOCKET_TIMEOUT.getName())  && !StringUtil.isEmpty(property.getValue()) && StringUtil.isNumeric(property.getValue())) {
				configuration.setSocketTimeout(Integer.parseInt(property.getValue()));
			}
			if (property.getName().contentEquals(
				ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.SKIP_ON_ADDRESS_VERIFIED.getName())) {
				configuration.setSkipOnAddressVerified(Boolean.parseBoolean(property.getValue()));
			}
		}

		List<String> missingProperties = new ArrayList<>();
		if (configuration.getAuthenticationUrl() == null || configuration.getAuthenticationUrl().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.AUTHENTICATION_URL
					.getName());
		}
		if (configuration.getUsername() == null || configuration.getUsername().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.USERNAME.getName());
		}
		if (configuration.getPassword() == null || configuration.getPassword().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.PASSWORD.getName());
		}
		if (configuration.getMerchantId() == null || configuration.getMerchantId().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.MERCHANT_ID.getName());
		}
		if (configuration.getKycUrl() == null || configuration.getKycUrl().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties.KYC_URL.getName());
		}
		if (configuration.getKycWorkflowName() == null || configuration.getKycWorkflowName().trim().isEmpty()) {
			missingProperties.add(ServiceAccessProviderSphonicKYCModuleInfo.ConfigProperties
					.KYC_WORKFLOW_NAME.getName());
		}
		if (!missingProperties.isEmpty()) {
			String missingPropertiesStr = missingProperties.stream().collect(Collectors.joining(", "));
			throw new IllegalArgumentException("One or more required configuration properties not set."
					+ " ["+missingPropertiesStr+"]");
		}

		return configuration;
	}
}
