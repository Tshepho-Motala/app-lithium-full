package lithium.service.user.provider.threshold;

import java.util.ArrayList;
import java.util.List;
import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.role.client.objects.Role;
import lithium.service.user.provider.threshold.config.ProviderConfigProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceUserProviderThresholdModuleInfo extends ModuleInfoAdapter {

  public ServiceUserProviderThresholdModuleInfo() {
    super();
    List<ProviderConfigProperty> properties = new ArrayList<>();

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.EXTREME_PUSH_API_URL.getValue())
            .required(false)
            .tooltip("Extreme Push API URL - When not provided calls to Extreme Push will be disabled")
            .dataType(String.class)
            .build()

    );

    properties.add(
        ProviderConfigProperty.builder()
            .name(ProviderConfigProperties.EXTREME_PUSH_APP_TOKEN.getValue())
            .required(false)
            .tooltip("Extreme Push App Token - When not provided calls to Extreme Push will be disabled")
            .dataType(String.class)
            .build()
    );

    ProviderConfig providerConfig = ProviderConfig.builder()
        .name(getModuleName())
        .type(ProviderConfig.ProviderType.THRESHOLD)
        .properties(properties)
        .build();

    addProvider(providerConfig);

    Role.Category userThresholdHistoryReportCategory = Role.Category.builder().name("UserThresholdHistoryReport").description("UserThresholdHistoryReport").build();
    addRole(Role.builder().category(userThresholdHistoryReportCategory).name("UserThresholdHistoryReport").role("USER_THRESHOLD_HISTORY_VIEW").description("Download  UserThresholdHistoryReport").build());

  }

  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    super.configureHttpSecurity(http);
    http.authorizeRequests().antMatchers("/backoffice/threshold-history/table").access("@lithiumSecurity.hasRoleInTree(authentication,'USER_THRESHOLD_HISTORY_VIEW')")
     .antMatchers("/backoffice/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'BRAND_CONFIG_VIEW', 'BRAND_CONFIG_SET', 'BRAND_CONFIG_EDIT','USER_THRESHOLD_HISTORY_VIEW')");

  }
}
