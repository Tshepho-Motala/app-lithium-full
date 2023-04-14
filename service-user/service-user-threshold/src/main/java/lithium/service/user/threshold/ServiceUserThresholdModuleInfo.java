package lithium.service.user.threshold;

import java.util.ArrayList;
import java.util.List;
import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.role.client.objects.Role;
import lithium.service.user.threshold.config.ProviderConfigProperties;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceUserThresholdModuleInfo extends ModuleInfoAdapter {

  public ServiceUserThresholdModuleInfo() {
    super();
    List<ProviderConfigProperty> properties = new ArrayList<>();

    properties.add(ProviderConfigProperty.builder()
        .name(ProviderConfigProperties.EXTREME_PUSH_API_URL.getValue())
        .required(false)
        .tooltip("Extreme Push API URL - When not provided calls to Extreme Push will be disabled")
        .dataType(String.class)
        .build());

    properties.add(ProviderConfigProperty.builder()
        .name(ProviderConfigProperties.EXTREME_PUSH_APP_TOKEN.getValue())
        .required(false)
        .tooltip("Extreme Push App Token - When not provided calls to Extreme Push will be disabled")
        .dataType(String.class)
        .build());

    ProviderConfig providerConfig = ProviderConfig.builder()
        .name(getModuleName())
        .type(ProviderConfig.ProviderType.THRESHOLD)
        .properties(properties)
        .build();

    addProvider(providerConfig);

    Role.Category userThresholdHistoryReportCategory = Role.Category.builder()
        .name("UserThresholdHistoryReport")
        .description("UserThresholdHistoryReport")
        .build();
    addRole(Role.builder()
        .category(userThresholdHistoryReportCategory)
        .name("UserThresholdHistoryReport")
        .role("USER_THRESHOLD_HISTORY_VIEW")
        .description("Download  UserThresholdHistoryReport")
        .build());

  }

  @Override
  public void configureHttpSecurity(HttpSecurity http)
  throws Exception
  {
    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, "/backoffice/threshold/warnings/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_THRESHOLD_HISTORY_VIEW','PLAYER_VIEW','PLAYER_RESPONSIBLE_GAMING_VIEW','PLAYER_LIMIT_EDIT','PLAYER_VIEW')")
        .antMatchers(HttpMethod.GET, "/backoffice/threshold/loss-limit/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_THRESHOLD_HISTORY_VIEW','PLAYER_VIEW','PLAYER_RESPONSIBLE_GAMING_VIEW','PLAYER_LIMIT_EDIT','PLAYER_VIEW')")
        .antMatchers(HttpMethod.POST, "/backoffice/threshold/loss-limit/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'USER_THRESHOLD_HISTORY_VIEW','PLAYER_VIEW','PLAYER_RESPONSIBLE_GAMING_VIEW','PLAYER_LIMIT_EDIT','PLAYER_VIEW')")
        .antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
        .antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
        .antMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
        .antMatchers(HttpMethod.GET, "/v3/**").permitAll();

  }
}
