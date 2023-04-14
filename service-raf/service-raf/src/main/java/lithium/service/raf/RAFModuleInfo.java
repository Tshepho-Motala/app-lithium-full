package lithium.service.raf;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class RAFModuleInfo extends ModuleInfoAdapter {

	public RAFModuleInfo() {
		Category rafCategory = Category.builder().name("Refer a Friend Operations").description("These are all the roles relevant to managing refer a friend.").build();
		addRole(Role.builder().category(rafCategory).name("Referral Configuration").role("RAF_CONFIG").description("Manage referral configuration.").build());
		addRole(Role.builder().category(rafCategory).name("Clicks").role("RAF_CLICKS").description("View list of all referral clicks.").build());
		addRole(Role.builder().category(rafCategory).name("Referrals").role("RAF_REFERRAL").description("View all referral signups and conversions.").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests()
			.antMatchers("/click/**").permitAll()
			.antMatchers("/referrals/**").authenticated()
			.antMatchers("/referral/**").access("@lithiumSecurity.authenticatedSystem(authentication)")
			.antMatchers("/admin/click/**").access("@lithiumSecurity.hasRole(authentication, 'RAF_CLICKS', 'PLAYER_REFERRALS_VIEW')")
			.antMatchers(HttpMethod.GET,"/admin/referral/table/**").access("@lithiumSecurity.hasRole(authentication, 'RAF_REFERRAL', 'PLAYER_REFERRALS_VIEW')")
			.antMatchers(HttpMethod.GET,"/admin/referral/findByPlayerGuid/{domainName}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_VIEW', 'RAF_REFERRAL')")
			.antMatchers("/admin/{domainName}/configuration/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'RAF_CONFIG', 'PLAYER_REFERRALS_VIEW')")
 			;
		// @formatter:on
	}
}
