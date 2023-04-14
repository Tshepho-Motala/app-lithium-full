package lithium.service.xp;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class XPModuleInfo extends ModuleInfoAdapter {
	public XPModuleInfo() {
		super();
		
		roles();
	}
	
	private void roles() {
		Category xpCategory = Category.builder().name("XP Scheme Operations").description("Operations related to XP schemes.").build();
		addRole(Role.builder().category(xpCategory).name("XP Schemes View").role("XP_SCHEMES_VIEW").description("View XP schemes").build());
		addRole(Role.builder().category(xpCategory).name("XP Levels View").role("XP_LEVELS_VIEW").description("View XP levels").build());
		addRole(Role.builder().category(xpCategory).name("XP Schemes Edit").role("XP_SCHEMES_EDIT").description("Edit XP schemes").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);

		http.authorizeRequests().antMatchers("/scheme/**").authenticated();
		http.authorizeRequests().antMatchers("/xp/level").access("@lithiumSecurity.hasRole(authentication, 'XP_LEVELS_VIEW')");

		http.authorizeRequests().antMatchers("/admin/status/**").access("@lithiumSecurity.hasRole(authentication, 'XP_SCHEMES_VIEW', 'XP_SCHEMES_EDIT')");
		
		http.authorizeRequests().antMatchers("/admin/scheme/table").access("@lithiumSecurity.hasRole(authentication, 'XP_SCHEMES_VIEW', 'XP_SCHEMES_EDIT')");
		http.authorizeRequests().antMatchers("/admin/scheme/{domainName}/getActiveScheme").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'XP_SCHEMES_VIEW', 'XP_SCHEMES_EDIT')");
		http.authorizeRequests().antMatchers("/admin/scheme/{id}/get").access("@lithiumSecurity.hasRole(authentication, 'XP_SCHEMES_VIEW', 'XP_SCHEMES_EDIT')");
		http.authorizeRequests().antMatchers("/admin/scheme/create").access("@lithiumSecurity.hasRole(authentication, 'XP_SCHEMES_EDIT')");
		http.authorizeRequests().antMatchers("/admin/scheme/{id}/edit").access("@lithiumSecurity.hasRole(authentication, 'XP_SCHEMES_EDIT')");
		http.authorizeRequests().antMatchers("/admin/scheme/{id}/edit/addLevel").access("@lithiumSecurity.hasRole(authentication, 'XP_SCHEMES_EDIT')");
		http.authorizeRequests().antMatchers("/admin/scheme/{id}/edit/{levelId}/removeLevel").access("@lithiumSecurity.hasRole(authentication, 'XP_SCHEMES_EDIT')");
		http.authorizeRequests().antMatchers("/admin/scheme/{id}/edit/{levelId}/modifyLevel").access("@lithiumSecurity.hasRole(authentication, 'XP_SCHEMES_EDIT')");
	}
}