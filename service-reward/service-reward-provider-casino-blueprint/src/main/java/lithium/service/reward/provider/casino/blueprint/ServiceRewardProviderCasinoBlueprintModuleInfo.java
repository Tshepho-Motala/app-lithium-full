package lithium.service.reward.provider.casino.blueprint;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.reward.provider.casino.blueprint.config.ProviderConfigProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ServiceRewardProviderCasinoBlueprintModuleInfo extends ModuleInfoAdapter {

  public ServiceRewardProviderCasinoBlueprintModuleInfo() {
    super();

    List<ProviderConfigProperty> properties = new ArrayList<>();
    properties.add(ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.REWARDS_BASE_URL.value())
            .required(true)
            .tooltip("Blueprint Base URL")
            .dataType(String.class)
            .build()
    );

    properties.add(ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.REWARDS_API_TOKEN.value())
            .required(true)
            .tooltip("The API token that is used for authentication/authorization on blueprint")
            .dataType(String.class)
            .build()
    );

    properties.add(ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.JURISDICTION.value())
            .required(true)
            .tooltip("Jurisdiction")
            .dataType(String.class)
            .build()
    );

    properties.add(ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.BRAND_ID.value())
            .required(true)
            .tooltip("The brand that is configured on blueprint for the domain")
            .dataType(String.class)
            .build()
    );

    properties.add(ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.COUNTRY_CODE.value())
            .required(true)
            .tooltip("The country code that is used by the domain")
            .dataType(String.class)
            .build()
    );

    properties.add(ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.IFORIUM_PLATFORM_KEY.value())
            .required(true)
            .tooltip("The platform given to lithium by iforium")
            .dataType(String.class)
            .build()
    );

    //TODO: uncomment this when it is time to use the offsets, I am leaving as is because we will be changing iforium wallets on every deploy

//    properties.add(ProviderConfigProperty.builder()
//            .name(ProviderConfigProperties.PLAYER_OFFSET.value())
//            .required(true)
//            .tooltip("The offset that will be applied to the player guid")
//            .dataType(Long.class)
//            .build()
//    );


    properties.add(ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.PLAYER_GUID_PREFIX.value())
            .required(true)
            .tooltip("The prefix that will be applied to the player guid, e.g. LVS will result in LVS-livescore_uk/123")
            .dataType(String.class)
            .build()
    );

    ProviderConfig providerConfig = ProviderConfig.builder()
            .name(getModuleName())
            .type(ProviderType.REWARD)
            .properties(properties)
            .build();

    addProvider(providerConfig);
    //		Category category = Category.builder().name("Settlement Operations").description("These are all the roles relevant to managing settlements.").build();
    //		addRole(Role.builder().category(category).name("Settlements Manage").role("SETTLEMENTS_MANAGE").description("Manage settlements.").build());
  }

  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    super.configureHttpSecurity(http);
    // @formatter:off
    http.authorizeRequests().antMatchers( "/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    // @formatter:on
  }
}