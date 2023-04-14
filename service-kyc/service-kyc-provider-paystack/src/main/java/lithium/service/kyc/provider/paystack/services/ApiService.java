package lithium.service.kyc.provider.paystack.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.objects.VerificationMethodType;
import lithium.service.kyc.provider.paystack.ServiceKycProviderPaystackModuleInfo;
import lithium.service.kyc.provider.paystack.config.APIAuthentication;
import lithium.service.kyc.provider.paystack.config.BrandsConfigurationBrand;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveResponse;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiService {

	@Autowired
	protected LithiumServiceClientFactory services;

	private BrandsConfigurationBrand getBrandConfiguration(String providerUrl, String domainName) throws Status512ProviderNotConfiguredException {
		ProviderClient cl = getProviderService();
		BrandsConfigurationBrand brandConfiguration = new BrandsConfigurationBrand();//external system id = providerId as stored in domain config
		if (cl != null) {
			Response<Provider> providerResponse = cl.findByUrlAndDomainName(providerUrl, domainName);
			if (providerResponse == null || providerResponse.getData() == null) {
				throw new Status512ProviderNotConfiguredException("paystack");
			}
			if (!providerResponse.getData().getEnabled()) {
				throw new Status512ProviderNotConfiguredException("paystack");
			}
			Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerUrl, domainName);
			for (ProviderProperty p: pp.getData()) {
				if (p.getName().equalsIgnoreCase(ServiceKycProviderPaystackModuleInfo.ConfigProperties.PLATFORM_URL.getValue())) brandConfiguration.setPlatformUrl(p.getValue());
				if (p.getName().equalsIgnoreCase(ServiceKycProviderPaystackModuleInfo.ConfigProperties.API_KEY.getValue())) brandConfiguration.setApiKey(p.getValue());
				if (p.getName().equalsIgnoreCase(ServiceKycProviderPaystackModuleInfo.ConfigProperties.BVN_LENGTH.getValue())) brandConfiguration.setBvnLength(Integer.valueOf(p.getValue()));
				if (p.getName().equalsIgnoreCase(VerificationMethodType.METHOD_BVN.getValue())) brandConfiguration.setBvnMethod(p.getValue());
				if (p.getName().equalsIgnoreCase(ServiceKycProviderPaystackModuleInfo.ConfigProperties.CONNECT_TIMEOUT.getValue())
						&& !StringUtil.isEmpty(p.getValue()) && StringUtil.isNumeric(p.getValue())) {
					brandConfiguration.setConnectTimeout(Integer.parseInt(p.getValue()));
				}
				if (p.getName().equalsIgnoreCase(ServiceKycProviderPaystackModuleInfo.ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getValue())
						&& !StringUtil.isEmpty(p.getValue()) && StringUtil.isNumeric(p.getValue())) {
					brandConfiguration.setConnectionRequestTimeout(Integer.parseInt(p.getValue()));
				}
				if (p.getName().equalsIgnoreCase(ServiceKycProviderPaystackModuleInfo.ConfigProperties.SOCKET_TIMEOUT.getValue())
						&& !StringUtil.isEmpty(p.getValue()) && StringUtil.isNumeric(p.getValue())) {
					brandConfiguration.setSocketTimeout(Integer.parseInt(p.getValue()));
				}
			}
		}
		return brandConfiguration;
	}

	private APIAuthentication getAPIAuthentication(String providerUrl, String domainName) throws Status512ProviderNotConfiguredException {
		BrandsConfigurationBrand brandConfiguration = getBrandConfiguration(providerUrl, domainName);
		log.debug("Retrieved API authentication from url - providerName "+providerUrl+" brandId "+domainName);

		if (StringUtils.isBlank(brandConfiguration.getApiKey())) {
			throw new Status512ProviderNotConfiguredException("paystack");
		}

		if (StringUtils.isBlank(brandConfiguration.getPlatformUrl())) {
			throw new Status512ProviderNotConfiguredException("paystack");
		}

		if (brandConfiguration.getBvnLength() == null) {
			throw new Status512ProviderNotConfiguredException("paystack");
		}

		if (brandConfiguration.getBvnMethod() == null) {
			throw new Status512ProviderNotConfiguredException("paystack");
		}

		return new APIAuthentication(providerUrl, domainName, brandConfiguration);
	}
	
	public User getUser(String playerguid) {
		UserApiInternalClient cl = getUserService();
		Response<User> response = cl.getUser(playerguid);
		if (response.isSuccessful()) return response.getData();
		return null;
	}

	public User updateVerificationStatus(UserVerificationStatusUpdate statusUpdate) throws UserClientServiceFactoryException {
		UserApiInternalClient cl = getUserService();
		Response<User> response = cl.editUserVerificationStatus(statusUpdate);
		if (response.isSuccessful()) return response.getData();
		return null;
	}
	
	private UserApiInternalClient getUserService() {
		UserApiInternalClient cl = null;
		try {
			cl = services.target(UserApiInternalClient.class, "service-user", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties: " + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
		}
		return cl;
	}
	
	
	private ProviderClient getProviderService() {
		ProviderClient cl = null;
		try {
			cl = services.target(ProviderClient.class, "service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting provider properties: " + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
		}
		return cl;
	}

	/**
	 * Utility function to populate raw transaction data into a pre-initilized data object.
	 * The requestData and responseData parameters can be null and will then be ignored for the serialization operation.
	 * @param rawAuthorizationData (output)
	 * @param requestData (input)
	 * @param responseData (input)
	 */
	private void populateRawData(RawAuthorizationData rawAuthorizationData, final Object requestData, final Object responseData) {
		if (rawAuthorizationData == null) {
			log.error("Unable to produce raw transaction data since rawAuthorizationData object is not initioalized");
		}
		if (requestData != null) {
			try {
				rawAuthorizationData.setRawRequestToProvider(new ObjectMapper().writeValueAsString(requestData));
			} catch (JsonProcessingException e) {
                log.warn("Unable to map raw transaction request for auth request: " + requestData + ". "  + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
			}
		}
		if (responseData != null) {
			try {
				rawAuthorizationData.setRawRequestToProvider(new ObjectMapper().writeValueAsString(requestData));
				if (responseData != null) {
					rawAuthorizationData.setRawResponseFromProvider(new ObjectMapper().writeValueAsString(responseData));
				}
			} catch(JsonProcessingException e){
                log.warn("Unable to map raw transaction response for auth request: " + responseData + ". "  + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
			}
		}
	}

	public String bvnResponseString(BvnResolveResponse bvnResolveResponse) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Status: %s, ", bvnResolveResponse != null ? bvnResolveResponse.getStatus() : ""));
		sb.append(String.format("Message: %s, ", bvnResolveResponse != null ? bvnResolveResponse.getMessage() : ""));
		if (bvnResolveResponse != null && bvnResolveResponse.getData() != null) {
			sb.append(String.format("First Name: %s, ", bvnResolveResponse.getData().getFirstName() != null ? bvnResolveResponse.getData().getFirstName() : ""));
			sb.append(String.format("Last Name: %s, ", bvnResolveResponse.getData().getLastName() != null ? bvnResolveResponse.getData().getLastName() : ""));
			sb.append(String.format("Dob: %s, ", bvnResolveResponse.getData().getDob() != null ? bvnResolveResponse.getData().getDob(): ""));
			sb.append(String.format("Mobile: %s, ", bvnResolveResponse.getData().getMobile() != null ? bvnResolveResponse.getData().getMobile() : ""));
			sb.append(String.format("Formatted dob: %s, ", bvnResolveResponse.getData().getFormattedDob() != null ? bvnResolveResponse.getData().getFormattedDob()  : ""));
		}
		if (bvnResolveResponse != null && bvnResolveResponse.getMeta() != null) {
			sb.append(String.format("Calls This Month: %s, ", bvnResolveResponse.getMeta().getCallsThisMonth() != null ? bvnResolveResponse.getMeta().getCallsThisMonth()  : ""));
			sb.append(String.format("Free Calls Left: %s, ", bvnResolveResponse.getMeta().getFreeCallsLeft() != null ? bvnResolveResponse.getMeta().getFreeCallsLeft()  : ""));
		}
		return sb.toString();
	}

	public BrandsConfigurationBrand getBrandsConfigurationBrand(String userGuid) throws Status512ProviderNotConfiguredException {
		String [] userDomain = userGuid.split("/");
		String domainName = userDomain[0];
		APIAuthentication apiAuthentication = getAPIAuthentication("service-kyc-provider-paystack", domainName);
		if (apiAuthentication == null || apiAuthentication.getBrandConfiguration() == null) {
			log.error("Paystack not properly configured for this domain {}", domainName);
			throw new Status512ProviderNotConfiguredException("paystack");
		}
		return apiAuthentication.getBrandConfiguration();
	}

}
