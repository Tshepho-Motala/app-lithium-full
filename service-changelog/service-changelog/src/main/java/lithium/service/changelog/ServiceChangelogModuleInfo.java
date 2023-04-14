package lithium.service.changelog;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceChangelogModuleInfo extends ModuleInfoAdapter {
	ServiceChangelogModuleInfo() {
		super();
		roles();
	}

	private void roles() {
		Role.Category gclCat = Role.Category.builder().name("Global Changelog Operations").description("Operations related to global changelogs.").build();
		addRole(Role.builder().category(gclCat).name("Global Changelogs View").role("CHANGELOGS_GLOBAL_VIEW").description("View Global Changelogs").build());
		addRole(Role.builder().category(gclCat).name("Global Changelogs Edit").role("CHANGELOGS_GLOBAL_EDIT").description("Edit Global Changelogs").build());
		addRole(Role.builder().category(gclCat).name("Global Changelogs Delete").role("CHANGELOGS_GLOBAL_DELETE").description("Delete Global Changelogs").build());
		addRole(Role.builder().category(gclCat).name("Global Changelogs Restore").role("CHANGELOGS_GLOBAL_RESTORE").description("Restore Global Changelogs").build());
		addRole(Role.builder().category(gclCat).name("Global Changelogs Pin").role("CHANGELOGS_GLOBAL_PIN").description("Pin Global Changelogs").build());
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/apiv1/**").access("@lithiumSecurity.authenticatedSystem(authentication)")

				.antMatchers("/backoffice/changelogs/global/entry/{id}/priority/{priority}").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_EDIT')")
				.antMatchers("/backoffice/changelogs/global/entry/{id}/pinned/{pinned}").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_PIN')")
				.antMatchers("/backoffice/changelogs/global/entry/{id}/deleted/true").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_DELETE')")
				.antMatchers("/backoffice/changelogs/global/entry/{id}/deleted/false").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_RESTORE')")

				.antMatchers("/backoffice/changelogs/global/categories").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_VIEW', 'CHANGELOGS_GLOBAL_EDIT','PLAYER_NOTE_ADD', 'MASS_PLAYER_UPDATE_VIEW')")
				.antMatchers("/backoffice/changelogs/global/entities").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_VIEW', 'CHANGELOGS_GLOBAL_EDIT', 'PLAYER_NOTES_VIEW')")
				.antMatchers("/backoffice/changelogs/global/subcategories").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_VIEW', 'CHANGELOGS_GLOBAL_EDIT', 'PLAYER_NOTES_VIEW','PLAYER_NOTE_ADD', 'MASS_PLAYER_UPDATE_VIEW')")
				.antMatchers("/backoffice/changelogs/global/types").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_VIEW', 'CHANGELOGS_GLOBAL_EDIT', 'PLAYER_NOTES_VIEW')")

				.antMatchers("/backoffice/changelogs/global/table").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_VIEW', 'CHANGELOGS_GLOBAL_EDIT', 'PLAYER_NOTES_VIEW')")

				.antMatchers("/backoffice/changelogs/global/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'CHANGELOGS_GLOBAL_VIEW', 'CHANGELOGS_GLOBAL_EDIT', 'PLAYER_NOTES_VIEW')")

				.antMatchers("/system/changelogs/user/add-note").access("@lithiumSecurity.authenticatedSystem(authentication)")
				.antMatchers("/system/changelogs/add-domain").access("@lithiumSecurity.authenticatedSystem(authentication)")

                .antMatchers(HttpMethod.GET, "/data-migration-job/**").access("@lithiumSecurity.hasRole(authentication, 'ADMIN')");
	}
}
