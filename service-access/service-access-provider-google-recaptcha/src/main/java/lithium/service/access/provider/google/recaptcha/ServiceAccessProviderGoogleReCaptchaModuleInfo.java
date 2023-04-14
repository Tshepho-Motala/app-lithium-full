package lithium.service.access.provider.google.recaptcha;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.access.provider.google.recaptcha.config.ProviderConfigProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;

@RestController
public class ServiceAccessProviderGoogleReCaptchaModuleInfo extends ModuleInfoAdapter {

    public ServiceAccessProviderGoogleReCaptchaModuleInfo() {
        super();
        //Arraylist containing all the relevant properties for the provider
        ArrayList<ProviderConfigProperty> properties= new ArrayList<>();

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.SITE_KEY.getValue())
                .required(true)
                .tooltip("")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.SCORE.getValue())
                .required(true)
                .tooltip("This is between 1 - 10 depending on how high the scrutiny on verification should be")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.SECRET_KEY.getValue())
                .required(true)
                .tooltip("The shared key between your site and reCAPTCHA.")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.RECAPTCHA_SERVICE_URL.getValue())
                .required(true)
                .tooltip("")
                .dataType(String.class)
                .version(1)
                .build());

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

        //Add the provider to module info
        addProvider(ProviderConfig.builder()
                .name(getModuleName())
                .type(ProviderType.ACCESS)
                .properties(properties)
                .build());

    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    }
}
