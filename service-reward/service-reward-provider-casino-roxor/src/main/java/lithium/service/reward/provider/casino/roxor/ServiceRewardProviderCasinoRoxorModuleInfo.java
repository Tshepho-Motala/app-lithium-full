package lithium.service.reward.provider.casino.roxor;

import java.util.ArrayList;
import java.util.List;
import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.reward.provider.casino.roxor.config.ProviderConfigProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceRewardProviderCasinoRoxorModuleInfo extends ModuleInfoAdapter {

  public ServiceRewardProviderCasinoRoxorModuleInfo() {
    super();
    setupProviderProperties();
    //		Category category = Category.builder().name("Settlement Operations").description("These are all the roles relevant to managing settlements.").build();
    //		addRole(Role.builder().category(category).name("Settlements Manage").role("SETTLEMENTS_MANAGE").description("Manage settlements.").build());
  }

  private void setupProviderProperties() {
    List<ProviderConfigProperty> properties = new ArrayList<>();

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.REWARDS_URL.value())
            .required(false)
            .tooltip("Roxor RGP-Rewards System Host URL.")
            .dataType(String.class)
            .build()
    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.REWARDS_DEFAULT_DURATION_IN_HOURS.value())
            .required(false)
            .tooltip("Roxor Default duration for rewards in hours e.g. 30 days x 24 hours = 720.")
            .dataType(String.class)
            .build()
    );

    properties.add(
            ProviderConfigProperty.builder()
                    .name(ProviderConfigProperties.WEBSITE.value())
                    .required(false)
                    .tooltip("Website of a player’s rewards e.g. website=jackpotjoy")
                    .dataType(String.class)
                    .build()
    );

    properties.add(
            ProviderConfigProperty.builder()
                    .name(ProviderConfigProperties.USE_PLAYER_API_TOKEN.value())
                    .required(false)
                    .tooltip("determines whether we should use a player token for identification")
                    .dataType(String.class)
                    .build()
    );

    ProviderConfig providerConfig = ProviderConfig.builder()
        .name(getModuleName())
        .type(ProviderType.REWARD)
        .properties(properties)
        .build();
    //Add the provider to moduleinfo
    addProvider(providerConfig);

    roles();
  }

  private void roles() {
  }

  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    super.configureHttpSecurity(http);
    // @formatter:off
    http.authorizeRequests().antMatchers( "/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    // @formatter:on
  }
}