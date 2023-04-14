package lithium.service.migration;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class VbMigrationModuleInfo extends ModuleInfoAdapter {
  public VbMigrationModuleInfo() {
    super();
  }

  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    super.configureHttpSecurity(http);

    // @formatter:off
    http.authorizeRequests()
        .antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
        .antMatchers("/progress/**").permitAll();
  }
}
