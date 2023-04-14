package lithium.service.casino.provider.iforium;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.casino.provider.iforium.config.ProviderConfigProperties;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.EqualsAndHashCode;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EqualsAndHashCode(callSuper = true)
public class IforiumModuleInfo extends ModuleInfoAdapter {

    @Override
    public void configureHttpSecurity(HttpSecurity http) throws Exception {
        super.configureHttpSecurity(http);
        http.authorizeRequests().antMatchers("/casino/mock/**").permitAll()
        .antMatchers("/", "/index", "/launchGame", "/actions").permitAll()
        .antMatchers("/games/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
        .antMatchers("/v1.0/**").permitAll()
        .antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    }

    public IforiumModuleInfo() {
        ProviderConfig providerConfig = ProviderConfig.builder()
                                                      .name(getModuleName())
                                                      .type(ProviderConfig.ProviderType.CASINO)
                                                      .properties(providerAuthConfigProperties())
                                                      .build();

        addProvider(providerConfig);
    }

    private List<ProviderConfigProperty> providerAuthConfigProperties() {
        List<ProviderConfigProperty> properties = new ArrayList<>();

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.CASINO_ID.getName())
                                             .tooltip(ProviderConfigProperties.CASINO_ID.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.LOBBY_URL.getName())
                                             .tooltip(ProviderConfigProperties.LOBBY_URL.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.STARTGAME_BASE_URL.getName())
                                             .tooltip(ProviderConfigProperties.STARTGAME_BASE_URL.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.WHITELIST_IP.getName())
                                             .tooltip(ProviderConfigProperties.WHITELIST_IP.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.SECURITY_USER_PASSWORD.getName())
                                             .tooltip(ProviderConfigProperties.SECURITY_USER_PASSWORD.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.SECURITY_USER_NAME.getName())
                                             .tooltip(ProviderConfigProperties.SECURITY_USER_NAME.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.LIST_GAME_URL.getName())
                                             .tooltip(ProviderConfigProperties.LIST_GAME_URL.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.REGULATIONS_ENABLED.getName())
                                             .tooltip(ProviderConfigProperties.REGULATIONS_ENABLED.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.REGULATION_SESSION_DURATION.getName())
                                             .tooltip(ProviderConfigProperties.REGULATION_SESSION_DURATION.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.REGULATION_INTERVAL.getName())
                                             .tooltip(ProviderConfigProperties.REGULATION_INTERVAL.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.REGULATION_GAME_HISTORY_URL.getName())
                                             .tooltip(ProviderConfigProperties.REGULATION_GAME_HISTORY_URL.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.REGULATION_BONUS_URL.getName())
                                             .tooltip(ProviderConfigProperties.REGULATION_BONUS_URL.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.REGULATION_OVERRIDE_RTS_13_MODE.getName())
                                             .tooltip(ProviderConfigProperties.REGULATION_OVERRIDE_RTS_13_MODE.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                                             .name(ProviderConfigProperties.REGULATION_OVERRIDE_CMA_MODE.getName())
                                             .tooltip(ProviderConfigProperties.REGULATION_OVERRIDE_CMA_MODE.getTooltip())
                                             .required(true)
                                             .dataType(String.class)
                                             .version(1)
                                             .build()
        );

        properties.add(ProviderConfigProperty.builder()
                .name(ProviderConfigProperties.BLUEPRINT_JACKPOT_URL.getName())
                .tooltip(ProviderConfigProperties.BLUEPRINT_JACKPOT_URL.getTooltip())
                .required(true)
                .dataType(String.class)
                .version(1)
                .build()
        );

        return properties;
    }
}
