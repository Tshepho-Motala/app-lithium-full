package lithium.service.user.search;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceUserSearchModuleInfo extends ModuleInfoAdapter {

  public ServiceUserSearchModuleInfo() {
  }

  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    // @formatter:off
//    commented out as Spring Security can't continue to configure antMatchers (in ResourceServerConfig) after anyRequest
//    http.authorizeRequests().anyRequest().permitAll()
    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, "/backoffice/players/table").access("@lithiumSecurity.hasRole(authentication, 'PLAYER_VIEW', 'PLAYER_EDIT')")
        .antMatchers(HttpMethod.GET, "/data-migration-job/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    ;
    // @formatter:on
  }
}
