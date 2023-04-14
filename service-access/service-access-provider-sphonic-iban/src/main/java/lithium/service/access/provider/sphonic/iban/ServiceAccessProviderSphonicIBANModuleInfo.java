package lithium.service.access.provider.sphonic.iban;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.role.client.objects.Role;
import lombok.Getter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;

@RestController
public class ServiceAccessProviderSphonicIBANModuleInfo extends ModuleInfoAdapter {

    ServiceAccessProviderSphonicIBANModuleInfo() {
        super();

        roles();

        //Arraylist containing all the relevant properties for the provider
        ArrayList<ProviderConfigProperty> properties = new ArrayList<ProviderConfigProperty>();

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.AUTHENTICATION_URL.getName())
                        .required(true)
                        .tooltip("Authentication URL")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.USERNAME.getName())
                        .required(true)
                        .tooltip("Credentials field \"Sphonic Username\"")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.PASSWORD.getName())
                        .required(true)
                        .tooltip("Credentials field \"Sphonic Password\"")
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
                        .name(ConfigProperties.MERCHANT_ID.getName())
                        .required(true)
                        .tooltip("Credentials field \"Sphonic Merchant ID\"")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.IBAN_URL.getName())
                        .required(true)
                        .tooltip("IBAN URL")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.IBAN_WORKFLOW_NAME.getName())
                        .required(true)
                        .tooltip("IBAN Workflow Name")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.IBAN_MODE.getName())
                        .required(true)
                        .tooltip("IBAN Mode: \"test_pass\", \"test_fail\" or \"live\". Live indicates a non mock mode,"
                                + " which could be a development, staging, or production environment based on the"
                                + " configured URL.")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getName())
                        .required(false)
                        .dataType(Integer.class)
                        .tooltip("The timeout in milliseconds used when requesting a connection from the connection manager. A timeout value of zero is interpreted as an infinite timeout.")
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.CONNECT_TIMEOUT.getName())
                        .required(false)
                        .dataType(Integer.class)
                        .tooltip("Determines the timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an infinite timeout.")
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.SOCKET_TIMEOUT.getName())
                        .required(false)
                        .dataType(Integer.class)
                        .tooltip("Defines the socket timeout in milliseconds, which is the timeout for waiting for data  or, put differently, the maximum period of inactivity between two consecutive data packets. A timeout value of zero is interpreted as an infinite timeout.")
                        .version(1)
                        .build());

        //Add the provider to moduleinfo
        addProvider(
                ProviderConfig.builder()
                        .name(getModuleName())
                        .type(ProviderType.ACCESS)
                        .properties(properties)
                        .build()
        );
    }

    private void roles() {
        Role.Category ibanCategory = Role.Category.builder().name("IBAN Operations").description("Operations related to iban.").build();
        addRole(Role.builder().category(ibanCategory).name("IBAN View").role("IBAN_VIEW").description("View IBAN Details").build());
        addRole(Role.builder().category(ibanCategory).name("IBAN Edit").role("IBAN_EDIT").description("Edit IBAN Details").build());
    }

    public static enum ConfigProperties {
        AUTHENTICATION_URL("AuthenticationUrl"),
        USERNAME("Username"),
        PASSWORD("Password"),
        MERCHANT_ID("MerchantID"),
        IBAN_URL("IBANUrl"),
        IBAN_WORKFLOW_NAME("IBANWorkflowName"),
        IBAN_MODE("IBANMode"),
        EXPIRATION_DELAY("ExpirationDelay"),
        CONNECTION_REQUEST_TIMEOUT("connectionRequestTimeout"),
        CONNECT_TIMEOUT("connectTimeout"),
        SOCKET_TIMEOUT("socketTimeout");

        @Getter
        private final String name;

        ConfigProperties(String valueParam) {
            name = valueParam;
        }
    }

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    }
}
