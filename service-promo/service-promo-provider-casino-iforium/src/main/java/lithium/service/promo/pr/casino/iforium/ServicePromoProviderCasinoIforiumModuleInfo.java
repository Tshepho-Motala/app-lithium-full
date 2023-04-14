package lithium.service.promo.pr.casino.iforium;

import lithium.modules.ModuleInfoAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServicePromoProviderCasinoIforiumModuleInfo extends ModuleInfoAdapter {

  public ServicePromoProviderCasinoIforiumModuleInfo() {
    //		CategoryDto category = CategoryDto.builder().name("Settlement Operations").description("These are all the roles relevant to managing settlements.").build();
    //		addRole(Role.builder().category(category).name("Settlements Manage").role("SETTLEMENTS_MANAGE").description("Manage settlements.").build());
  }

  @Override
  public void configureHttpSecurity(HttpSecurity http) throws Exception {
    // @formatter:off
    http.authorizeRequests().antMatchers( "/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
    // @formatter:on
  }
}