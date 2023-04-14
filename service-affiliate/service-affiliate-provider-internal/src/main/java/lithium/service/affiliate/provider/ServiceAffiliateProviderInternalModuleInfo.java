package lithium.service.affiliate.provider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class ServiceAffiliateProviderInternalModuleInfo extends ModuleInfoAdapter {
	public ServiceAffiliateProviderInternalModuleInfo() {
		super();
//		Category category = Category.builder().name("Affiliate Operations").description("These are all the roles relevant to managing affiliates.").build();
//		addRole(Role.builder().category(category).name("Affiliate Modify").role("AFFILIATE_MODIFY").description("Add/Edit an affiliate").build());
//		addRole(Role.builder().category(category).name("Affiliate View").role("AFFILIATE_VIEW").description("View/List affiliate(s)").build());
//		addRole(Role.builder().category(category).name("Affiliate Player Modify").role("AFFILIATE_PLAYER_MODIFY").description("Add/Edit an affiliate player").build());
//		addRole(Role.builder().category(category).name("Affiliate Player View").role("AFFILIATE_PLAYER_VIEW").description("View/List affiliate player(s)").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
//		http.authorizeRequests().antMatchers("/affiliate/player/**").access("@lithiumSecurity.hasRole(authentication, 'AFFILIATE_PLAYER_MODIFY, AFFILIATE_PLAYER_VIEW')");
//		http.authorizeRequests().antMatchers("/affiliates/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, {domainName}, 'AFFILIATE_MODIFY, AFFILIATE_VIEW')");
//		http.authorizeRequests().antMatchers("/casino/bonus/manual/**").access("@lithiumSecurity.hasRole(authentication, 'MANUAL_BONUS_ALLOCATION')");
//		http.authorizeRequests().antMatchers("/casino/bonus/**").authenticated();
//		http.authorizeRequests().antMatchers("/casino/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
//		http.authorizeRequests().anyRequest().permitAll();
	}
}
