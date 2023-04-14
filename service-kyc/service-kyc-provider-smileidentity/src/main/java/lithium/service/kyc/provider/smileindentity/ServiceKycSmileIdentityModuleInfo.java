package lithium.service.kyc.provider.smileindentity;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.kyc.provider.config.KycTypeProviderProperty;
import lithium.service.kyc.provider.smileindentity.config.ProviderConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;

@Slf4j
@RestController
public class ServiceKycSmileIdentityModuleInfo extends ModuleInfoAdapter {
    public ServiceKycSmileIdentityModuleInfo() {
        super();
        ArrayList<ProviderConfigProperty> properties= new ArrayList<>();

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.VERIFY_API_URL.getValue())
                .required(true)
                .tooltip("Verify API URL used to verify an ID Number and retrieve a person's personal information")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.PARTNER_ID.getValue())
                .required(true)
                .tooltip("A unique number assigned to your account")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.API_KEY.getValue())
                .required(true)
                .tooltip("Half of a secret RSA key pair. Used to create signature token")
                .dataType(String.class)
                .version(1)
                .build());

	    properties.add(ProviderConfigProperty.builder()
			    .name(ProviderConfigProperties.BANK_LIST.getValue())
			    .required(true)
			    .tooltip("List of banks is available in these end point")
			    .dataType(String.class)
			    .version(1)
			    .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.COUNTRY.getValue())
                .required(true)
                .tooltip("Define country")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(KycTypeProviderProperty.PASSPORT_NUMBER);
        properties.add(KycTypeProviderProperty.BANK_ACCOUNT);
        properties.add(KycTypeProviderProperty.NATIONAL_ID);
        properties.add(KycTypeProviderProperty.NIN);
        properties.add(KycTypeProviderProperty.DRIVERS_LICENCE);
        properties.add(KycTypeProviderProperty.BVN);
        properties.add(KycTypeProviderProperty.VOTER_ID);
	    properties.add(KycTypeProviderProperty.NIN_PHONE_NUMBER);

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.CONNECTION_REQUEST_TIMEOUT.getValue())
                .required(false)
                .dataType(Integer.class)
                .tooltip("The timeout in milliseconds used when requesting a connection from the connection manager. A timeout value of zero is interpreted as an infinite timeout.")
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.CONNECT_TIMEOUT.getValue())
                .required(false)
                .dataType(Integer.class)
                .tooltip("Determines the timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an infinite timeout.")
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.SOCKET_TIMEOUT.getValue())
                .required(false)
                .dataType(Integer.class)
                .tooltip("Defines the socket timeout in milliseconds, which is the timeout for waiting for data  or, put differently, the maximum period of inactivity between two consecutive data packets. A timeout value of zero is interpreted as an infinite timeout.")
                .version(1)
                .build());

        //Add the provider to moduleinfo
        addProvider(ProviderConfig.builder()
                .name(getModuleName())
                .type(ProviderConfig.ProviderType.KYC)
                .properties(properties)
                .build());
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests()
                .antMatchers("/system/verify").access("@lithiumSecurity.authenticatedSystem(authentication)");
	    http.authorizeRequests()
			    .antMatchers("/system/banks").access("@lithiumSecurity.authenticatedSystem(authentication)");
    }
}
