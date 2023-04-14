package lithium.service.access.provider.transunion;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;


@RestController
public class KycTransUnionModuleInfo extends ModuleInfoAdapter {

    KycTransUnionModuleInfo() {
        super();
        //Arraylist containing all the relevant properties for the provider
        ArrayList<ProviderConfigProperty> properties = new ArrayList<ProviderConfigProperty>();

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.COMPANY.getName())
                        .required(true)
                        .tooltip("Credentials field \"Account Company\"")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.USERNAME.getName())
                        .required(true)
                        .tooltip("Credentials field \"Account Username\"")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.PASSWORD.getName())
                        .required(true)
                        .tooltip("Credentials field \"Account Password\"")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.BASE_URL.getName())
                        .required(true)
                        .tooltip("Basic url of external service api")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.APPLICATION.getName())
                        .required(true)
                        .tooltip("Credentials field \"Application\"")
                        .dataType(String.class)
                        .version(1)
                        .build());

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.TIMEOUT_READ.getName())
                        .dataType(String.class)
                        .required(true)
                        .tooltip("Set the socket read timeout for the underlying HttpClient. A value of 0 means never timeout.")
                        .version(1)
                        .build()
        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.TIMEOUT_CONNECTION.getName())
                        .dataType(String.class)
                        .required(true)
                        .tooltip("Sets the timeout until a connection is established. A value of 0 means never timeout.")
                        .version(1)
                        .build()
        );
        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.PASSWORD_AUTO_UPDATE.getName())
                        .dataType(Boolean.class)
                        .required(true)
                        .tooltip("Check for turn on password update job")
                        .version(1)
                        .build()
        );

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.PASSWORD_UPDATE_URL.getName())
                        .dataType(Integer.class)
                        .required(true)
                        .tooltip("URL Used to update password")
                        .version(1)
                        .build()
        );

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.PASSWORD_UPDATE_DELAY.getName())
                        .dataType(Integer.class)
                        .required(true)
                        .tooltip("Delay for password update in Days")
                        .version(1)
                        .build()
        );

        properties.add(
                ProviderConfigProperty.builder()
                        .name(ConfigProperties.PASSWORD_LAST_UPDATE_DATE.getName())
                        .dataType(String.class)
                        .required(false)
                        .disabled(true)
                        .tooltip("For internal use, autofilled")
                        .version(1)
                        .build()
        );

        //Add the provider to moduleinfo
        addProvider(
                ProviderConfig.builder()
                        .name(getModuleName())
                        .type(ProviderType.ACCESS)
                        .properties(properties)
                        .build()
        );
    }

    public static enum ConfigProperties {
        USERNAME("User Name"),
        PASSWORD("Password"),
        COMPANY("Company"),
        BASE_URL("Base api Url"),
        APPLICATION("Application"),
        TIMEOUT_READ("Timeout Read"),
        TIMEOUT_CONNECTION("Timeout connection"),
        PASSWORD_AUTO_UPDATE("Turn ON Password update job?"),
        PASSWORD_UPDATE_URL("Password update URL"),
        PASSWORD_UPDATE_DELAY("Password update time interval (in days)"),
        PASSWORD_LAST_UPDATE_DATE("Password last update Date");

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
