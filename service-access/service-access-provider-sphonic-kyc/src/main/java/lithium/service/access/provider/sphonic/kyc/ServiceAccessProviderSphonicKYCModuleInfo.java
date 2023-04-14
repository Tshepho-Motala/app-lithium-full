package lithium.service.access.provider.sphonic.kyc;

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
public class ServiceAccessProviderSphonicKYCModuleInfo extends ModuleInfoAdapter {

    ServiceAccessProviderSphonicKYCModuleInfo() {
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
                        .name(ConfigProperties.KYC_URL.getName())
                        .required(true)
                        .tooltip("KYC URL")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.KYC_WORKFLOW_NAME.getName())
                        .required(true)
                        .tooltip("KYC Workflow Name")
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

        properties.add(
            ProviderConfigProperty.builder()
                .name(ConfigProperties.PARTIAL_VERIFICATION.getName())
                .required(false)
                .tooltip("Enables partial verification")
                .dataType(Boolean.class)
                .version(1)
                .build());

        properties.add(
            ProviderConfigProperty.builder()
                .name(ConfigProperties.SKIP_ON_ADDRESS_VERIFIED.getName())
                .required(false)
                .tooltip("Do not call Shponic in case address is already verified")
                .dataType(Boolean.class)
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
        Role.Category kycCategory = Role.Category.builder().name("KYC Operations").description("Operations related to kyc.").build();
        addRole(Role.builder().category(kycCategory).name("KYC View").role("KYC_VIEW").description("View KYC Details").build());
        addRole(Role.builder().category(kycCategory).name("KYC Edit").role("KYC_EDIT").description("Edit KYC Details").build());
    }

    public enum ConfigProperties {
        AUTHENTICATION_URL("AuthenticationUrl"),
        USERNAME("Username"),
        PASSWORD("Password"),
        MERCHANT_ID("MerchantID"),
        KYC_URL("KYCUrl"),
        KYC_WORKFLOW_NAME("KYCWorkflowName"),
        PARTIAL_VERIFICATION("Partial verification"),
        EXPIRATION_DELAY("ExpirationDelay"),
        CONNECTION_REQUEST_TIMEOUT("connectionRequestTimeout"),
        CONNECT_TIMEOUT("connectTimeout"),
        SOCKET_TIMEOUT("socketTimeout"),
        SKIP_ON_ADDRESS_VERIFIED("skipOnAddressVerified");

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
