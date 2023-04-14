package lithium.service.avatar;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class AvatarModuleInfo extends ModuleInfoAdapter {
	public AvatarModuleInfo() {
		super();
		Category avatarCategory = Category.builder().name("Avatar Operations").description("These are all the roles relevant to managing avatars.").build();
		addRole(Role.builder().category(avatarCategory).name("View Avatars").role("AVATARS_VIEW").description("View avatar configurations.").build());
		addRole(Role.builder().category(avatarCategory).name("Edit Avatars").role("AVATARS_EDIT").description("Edit avatar configurations.").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests()
			.antMatchers("/admin/avatar/{domainName}/table").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AVATARS_VIEW', 'AVATARS_EDIT')")
			.antMatchers("/admin/avatar/{domainName}/add").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AVATARS_EDIT')")
			.antMatchers("/admin/avatar/{domainName}/view/{avatarId}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AVATARS_VIEW', 'AVATARS_EDIT')")
			.antMatchers("/admin/avatar/{domainName}/delete/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AVATARS_EDIT')")
			.antMatchers("/admin/avatar/{domainName}/toggleEnable/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AVATARS_EDIT')")
			.antMatchers("/admin/avatar/{domainName}/setAsDefault/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'AVATARS_EDIT')")
			
			.antMatchers("/avatar/{domainName}/getImage/{avatarId}").permitAll()
			.antMatchers("/avatar/{domainName}/**").authenticated()
			.antMatchers("/useravatar/{domainName}/{userName}/**").permitAll()
 			;
		// @formatter:on
	}
}