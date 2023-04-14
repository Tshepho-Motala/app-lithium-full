package lithium.service.user;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import lithium.menu.MenuItem;
import lithium.modules.ModuleInfoAdapter;

public class UserModuleInfo extends ModuleInfoAdapter {

	public UserModuleInfo() {
		addMenuItem(
			new MenuItem("Administration", "Administration", "/admin/", "gear", "ADMIN,TRANSLATE_ADMIN", 1000).addChild(
				new MenuItem("Languages", "Languages", "/admin/translations", "globe", "ADMIN,TRANSLATE_ADMIN", 200)
			)
		);
//		addRole(new Role("Translator", "TRANSLATE_ADMIN", "Provide and maintain translations for the applciation"));
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
//		http.authorizeRequests().antMatchers("/admin/users/**").hasAnyAuthority("ADMIN", "USER_ADMIN");
		http.authorizeRequests().antMatchers("/admin/users/**").access("@lithiumSecurity.hasRole(authentication, 'ADMIN', 'USER_ADMIN')");
	}
}