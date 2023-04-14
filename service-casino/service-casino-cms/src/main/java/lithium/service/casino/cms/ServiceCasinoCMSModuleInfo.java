package lithium.service.casino.cms;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceCasinoCMSModuleInfo extends ModuleInfoAdapter {
	public ServiceCasinoCMSModuleInfo() {
		super();
		roles();
	}

	private void roles() {
		Role.Category casinoLobbiesConfigCategory = Role.Category.builder()
			.name("Casino Lobbies Configuration Operations")
			.description("Operations related to casino lobby configuration management.")
			.build();
		addRole(Role.builder()
			.category(casinoLobbiesConfigCategory)
			.name("Lobby Config View")
			.role("CASINO_LOBBIES_VIEW")
			.description("View casino lobby configurations")
			.build()
		);
		addRole(Role.builder()
			.category(casinoLobbiesConfigCategory)
			.name("Lobby Config Add")
			.role("CASINO_LOBBIES_ADD")
			.description("Add casino lobby configurations")
			.build()
		);
		addRole(Role.builder()
			.category(casinoLobbiesConfigCategory)
			.name("Lobby Config Edit")
			.role("CASINO_LOBBIES_EDIT")
			.description("Edit casino lobby configurations")
			.build()
		);

		Role.Category casinoBannersCategory = Role.Category.builder()
				.name("Casino Banners Operations")
				.description("Operations related to casino banners management.")
				.build();
		addRole(Role.builder()
				.category(casinoBannersCategory)
				.name("Banners View")
				.role("CASINO_BANNERS_VIEW")
				.description("View casino banners")
				.build()
		);
		addRole(Role.builder()
				.category(casinoBannersCategory)
				.name("Banners Add")
				.role("CASINO_BANNERS_ADD")
				.description("Add casino banners")
				.build()
		);
		addRole(Role.builder()
				.category(casinoBannersCategory)
				.name("Banners Edit")
				.role("CASINO_BANNERS_EDIT")
				.description("Edit casino banners")
				.build()
		);
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/lobbies/lobby-exists")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/lobbies/table")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/{domainName}/lobbies/add")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/v1/lobbies")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_VIEW')");
//				.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_VIEW')");

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/lobby/{id}")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_VIEW')");
		http.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/backoffice/{domainName}/lobby/{id}/revision/{lobbyRevisionId}")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/lobby/{id}/revisions")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/{domainName}/lobby/{id}/modify")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/{domainName}/lobby/{id}/modify")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_EDIT')");
		http.authorizeRequests()
			.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/lobby/{id}/modifyAndSaveCurrent")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_EDIT')");
		http.authorizeRequests()
			.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/lobby/{id}/banners/{bannerId}/add-page-banner")
			.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_EDIT')");
		http.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/lobby/{id}/banners/get-page-banners")
				.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_VIEW')");
		http.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/lobby/{id}/banners/update-positions")
				.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_EDIT')");
		http.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/lobby/{id}/banners/remove-from-page/{pageBannerId}")
				.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_LOBBIES_EDIT')");

		http.authorizeRequests()
				.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/banners/{id}/get")
				.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_BANNERS_VIEW', 'CASINO_BANNERS_ADD', 'CASINO_BANNERS_EDIT')")
				.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/banners/create")
				.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_BANNERS_ADD')")
				.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/banners/{id}/update")
				.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_BANNERS_EDIT')")
				.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/banners/find-all")
				.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_BANNERS_VIEW', 'CASINO_BANNERS_ADD', 'CASINO_BANNERS_EDIT')")
				.antMatchers(HttpMethod.POST, "/backoffice/{domainName}/banners/{id}/remove")
				.access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'CASINO_BANNERS_EDIT')");


		http.authorizeRequests().antMatchers("/frontend/lobby/load").permitAll();
	}
}
