package lithium.service.user.provider.sphonic.idin;

import java.util.ArrayList;
import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceUserProviderSphonicIdinModuleInfo extends ModuleInfoAdapter {
    ServiceUserProviderSphonicIdinModuleInfo() {
        super();

        ArrayList<ProviderConfigProperty> properties = new ArrayList<>();
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.AUTHENTICATION_URL.getName())
                        .required(true)
                        .tooltip("Authentication Url")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.USERNAME.getName())
                        .required(true)
                        .tooltip("Credentials field \"IDIN Username \"")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.PASSWORD.getName())
                        .required(true)
                        .tooltip("Credentials field \" IDIN Password \"")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.MERCHANT_ID.getName())
                        .required(true)
                        .tooltip("Credentials field \"IDIN Merchant ID\"")
                        .dataType(String.class)
                        .version(1)
                        .build());
        properties.add(
            ProviderConfigProperty.builder()
                .name(ConfigProperties.EXPIRATION_DELAY.getName())
                .required(false)
                .tooltip("Number of seconds the Sphonic access_token expiration time will be decreased. Default is 120 sec if not specified.")
                .dataType(Integer.class)
                .version(1)
                .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.IDIN_URL.getName())
                        .tooltip("IDIN Url")
                        .required(true)
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.APPLICANT_REFERENCE_OFFSET.getName())
                        .tooltip("Applicant Reference Offset")
                        .required(true)
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.IDIN_START_WORKFLOW_NAME.getName())
                        .required(true)
                        .tooltip("IDIN Start Workflow Name")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.IDIN_RETRIEVE_WORKFLOW_NAME.getName())
                        .required(true)
                        .tooltip("IDIN Retrieve Workflow Name")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getName())
                        .tooltip("Connection Request Timeout")
                        .dataType(Integer.class)
                        .build());

        properties.add(
          ProviderConfigProperty.builder()
                  .name(ConfigProperties.CONNECTION_TIMEOUT.getName())
                  .dataType(Integer.class)
                  .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.SOCKET_TIMEOUT.getName())
                        .dataType(Integer.class)
                        .build());
        properties.add(
            ProviderConfigProperty.builder()
                .name(ConfigProperties.APPLICANT_HASH_KEY.getName())
                .required(true)
                .tooltip("IDIN Applicant Hash Key to be used as input/output on registration flows")
                .dataType(String.class)
                .version(1)
                .build());

        addProvider(
                ProviderConfig.builder()
                        .name(getModuleName())
                        .type(ProviderConfig.ProviderType.REGISTER)
                        .properties(properties)
                        .build());
    }

    public static enum ConfigProperties {
        AUTHENTICATION_URL("AuthenticationUrl"),
        USERNAME("Username"),
        PASSWORD("Password"),
        MERCHANT_ID("MerchantID"),
        IDIN_URL("iDinUrl"),
        APPLICANT_REFERENCE_OFFSET("applicantIdReferenceOffset"),
        IDIN_START_WORKFLOW_NAME("iDinStartWorkflowName"),
        IDIN_RETRIEVE_WORKFLOW_NAME("iDinRetrieveWorkflowName"),
        CONNECTION_REQUEST_TIMEOUT("connectionRequestTimeOut"),
        CONNECTION_TIMEOUT("connectionTimeOut"),
        SOCKET_TIMEOUT("socketTimeOut"),
        EXPIRATION_DELAY("ExpirationDelay"),
        APPLICANT_HASH_KEY("applicantHashKey");

        @Getter
        private final String name;

        ConfigProperties(String valueParam) {
            this.name = valueParam;
        }

    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests()
                .antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");;
    }
}
