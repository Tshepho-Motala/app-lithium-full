package lithium.service.datafeed.provider.google;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
public class ServiceDataFeedProviderGoogleModuleInfo extends ModuleInfoAdapter {

    ServiceDataFeedProviderGoogleModuleInfo() {
        super();
        ArrayList<ProviderConfigProperty> properties= new ArrayList<ProviderConfigProperty>();
        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.PROJECT_ID.getName())
                .required(true)
                .tooltip("project_id")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.ACCOUNT_CHANGES_TOPIC_NAME.getName())
                .required(true)
                .tooltip("Account changes topic name")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.WALLET_TRANSACTION_TOPIC_NAME.getName())
                .required(true)
                .tooltip("Wallet transaction topic name")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.VIRTUALS_TOPIC_NAME.getName())
                .required(true)
                .tooltip("Virtuals topic name")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.CASINO_TOPIC_NAME.getName())
                .required(true)
                .tooltip("Casino topic name")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.SPORTSBOOK_TOPIC_NAME.getName())
                .required(true)
                .tooltip("Sportsbook topic name")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.ACCOUNT_LINK_TOPIC_NAME.getName())
                .required(true)
                .tooltip("Account Link topic name")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.MARKETING_PREFERENCES_TOPIC_NAME.getName())
                .required(true)
                .tooltip("Marketing Preferences topic name")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.CLIENT_ID.getName())
                .required(true)
                .tooltip("client_id")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.CLIENT_EMAIL.getName())
                .required(true)
                .tooltip("client_email")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.PRIVATE_KEY_ID.getName())
                .required(true)
                .tooltip("private_key_id")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.PRIVATE_KEY.getName())
                .required(true)
                .tooltip("private_key")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.ACCOUNT_CHANGE_CHANNEL_ACTIVE.getName())
                .required(true)
                .tooltip("Activate the channel to send account changes type of messages (true/false)")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.WALLET_TRANSACTION_CHANNEL_ACTIVE.getName())
                .required(true)
                .tooltip("Activate the channel to send wallet transaction type of messages (true/false)")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.VIRTUALS_CHANNEL_ACTIVE.getName())
                .required(true)
                .tooltip("Activate the channel to send virtuals type of messages (true/false)")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.CASINO_CHANNEL_ACTIVE.getName())
                .required(true)
                .tooltip("Activate the channel to send casino type of messages (true/false)")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.SPORTSBOOK_CHANNEL_ACTIVE.getName())
                .required(true)
                .tooltip("Activate the channel to send sportsbook type of messages (true/false)")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.ACCOUNT_LINK_CHANNEL_ACTIVE.getName())
                .required(true)
                .tooltip("Activate the channel to send Account Link type of messages (true/false)")
                .dataType(String.class)
                .version(1)
                .build());

        properties.add(ProviderConfigProperty.builder()
                .name(ConfigProperties.MARKETING_PREFS_CHANNEL_ACTIVE.getName())
                .required(true)
                .tooltip("Activate the channel to send marketing preferences messages (true/false)")
                .dataType(String.class)
                .version(1)
                .build());


        addProvider(
                ProviderConfig.builder()
                        .name(getModuleName())
                        .type(ProviderConfig.ProviderType.PUB_SUB)
                        .properties(properties)
                        .build()
        );
    }

    public static enum ConfigProperties {
        PROJECT_ID ("project_id"),
        ACCOUNT_CHANGES_TOPIC_NAME("Account changes topic name"),
        WALLET_TRANSACTION_TOPIC_NAME("Wallet transaction topic name"),
        SPORTSBOOK_TOPIC_NAME("Sportsbook topic name"),
        VIRTUALS_TOPIC_NAME("Virtuals topic name"),
        CASINO_TOPIC_NAME("Casino topic name"),
        ACCOUNT_LINK_TOPIC_NAME("Account Link topic name"),
        MARKETING_PREFERENCES_TOPIC_NAME("Marketing preferences name"),
        CLIENT_ID ("client_id"),
        CLIENT_EMAIL("client_email"),
        PRIVATE_KEY_ID ("private_key_id"),
        PRIVATE_KEY ("private_key"),
        ACCOUNT_CHANGE_CHANNEL_ACTIVE("Account Changes channel active"),
        WALLET_TRANSACTION_CHANNEL_ACTIVE("Wallet transactions channel active"),
        VIRTUALS_CHANNEL_ACTIVE("Virtuals channel active"),
        CASINO_CHANNEL_ACTIVE("Casino channel active"),
        SPORTSBOOK_CHANNEL_ACTIVE("Sportsbook channel active"),
        ACCOUNT_LINK_CHANNEL_ACTIVE("AccountLink channel active"),
        MARKETING_PREFS_CHANNEL_ACTIVE("Marketing preferences channel active");


        @Getter
        private final String name;

        ConfigProperties(String valueParam) {
            name = valueParam;
        }
    }


    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests()
                .antMatchers("/system/isPubSubActivated/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    }
}