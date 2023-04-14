package lithium.service.casino.search;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceCasinoSearchModuleInfo extends ModuleInfoAdapter {
  public ServiceCasinoSearchModuleInfo() {
    roles();
  }

  private void roles() {
    Category casinoCategory = Category.builder().name("Casino Search Operations").description("Operations related to searching of casino bets.").build();
    addRole(Role.builder().category(casinoCategory).name("Player Casino History View").role("PLAYER_CASINO_HISTORY_VIEW").description("View Player Casino History").build());
  }

  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    // @formatter:off
    http.authorizeRequests()
        .antMatchers("/backoffice/bethistory/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'PLAYER_CASINO_HISTORY_VIEW')")
        .antMatchers("/system/bethistory/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
        .antMatchers("/frontend/**").authenticated()
    ;
    // @formatter:on
  }
}
