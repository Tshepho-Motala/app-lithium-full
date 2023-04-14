package lithium.service.kyc.provider.smileindentity.config;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.objects.VerificationMethodType;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProviderConfigService {

    @Autowired
    private LithiumServiceClientFactory services;

    public ProviderConfig getConfig(String providerName, String domainName) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        ProviderClient providerService = getProviderService();
        Response<Iterable<ProviderProperty>> providerProperties =
		        providerService.propertiesByProviderUrlAndDomainName(providerName, domainName);

        if (!providerProperties.isSuccessful() || providerProperties.getData() == null) {
            log.error("Smile-Identity not properly configured for this domain {}", domainName);
            throw new Status512ProviderNotConfiguredException("Smile-Identity");
        }

        ProviderConfig config = new ProviderConfig();

        for (ProviderProperty providerProperty : providerProperties.getData()) {
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.VERIFY_API_URL.getValue()))
                config.setVerifyApiUrl(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.PARTNER_ID.getValue()))
                config.setPartnerId(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.API_KEY.getValue()))
                config.setApiKey(providerProperty.getValue());
	        if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.BANK_LIST.getValue()))
		        config.setBankListUrl(providerProperty.getValue());
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.COUNTRY.getValue()))
                config.setCountry(providerProperty.getValue());

            if (providerProperty.getName().equalsIgnoreCase(VerificationMethodType.METHOD_PASSPORT.getValue()))
                config.setPassportNumber(Boolean.valueOf(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(VerificationMethodType.METHOD_NATIONAL_ID.getValue()))
                config.setNationalId(Boolean.valueOf(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(VerificationMethodType.METHOD_NIN.getValue()))
                config.setNin(Boolean.valueOf(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(VerificationMethodType.METHOD_DRIVERS_LICENSE.getValue()))
                config.setDriversLicence(Boolean.valueOf(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(VerificationMethodType.METHOD_BANK_ACCOUNT.getValue()))
                config.setBankAccount(Boolean.valueOf(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(VerificationMethodType.METHOD_BVN.getValue()))
                config.setBvn(Boolean.valueOf(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(VerificationMethodType.METHOD_VOTER_ID.getValue()))
                config.setVoterId(Boolean.valueOf(providerProperty.getValue()));
	        if (providerProperty.getName().equalsIgnoreCase(VerificationMethodType.METHOD_NIN_PHONE_NUMBER.getValue()))
		        config.setNinPhoneNumber(Boolean.valueOf(providerProperty.getValue()));
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.CONNECTION_REQUEST_TIMEOUT.getValue())
                    && !StringUtil.isEmpty(providerProperty.getValue()) && StringUtil.isNumeric(providerProperty.getValue())) {
                config.setConnectionRequestTimeout(Integer.parseInt(providerProperty.getValue()));
            }
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.CONNECT_TIMEOUT.getValue())
                    && !StringUtil.isEmpty(providerProperty.getValue()) && StringUtil.isNumeric(providerProperty.getValue())) {
                config.setConnectTimeout(Integer.parseInt(providerProperty.getValue()));
            }
            if (providerProperty.getName().equalsIgnoreCase(ProviderConfigProperties.SOCKET_TIMEOUT.getValue())
                    && !StringUtil.isEmpty(providerProperty.getValue()) && StringUtil.isNumeric(providerProperty.getValue())) {
                config.setSocketTimeout(Integer.parseInt(providerProperty.getValue()));
            }
        }

        if (config.getVerifyApiUrl() == null || config.getPassportNumber() == null
                || config.getNationalId() == null || config.getDriversLicence() == null
                || config.getBvn() == null || config.getVoterId() == null
                || config.getNin() == null || config.getNinPhoneNumber() == null) {
            log.error("Smile-Identity not properly configured for this domain {}", domainName);
            throw new Status512ProviderNotConfiguredException("Smile-Identity");
        }

        return config;
    }

    private ProviderClient getProviderService() throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException {
        ProviderClient cl = null;
        try {
            cl = services.target(ProviderClient.class, "service-domain", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting provider properties: "  + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(e)));
            throw new Status500InternalServerErrorException("Can't get service-domain provider client");
        }
        return cl;
    }

}
