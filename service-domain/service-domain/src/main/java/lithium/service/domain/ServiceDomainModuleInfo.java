package lithium.service.domain;

import lithium.menu.MenuItem;
import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class ServiceDomainModuleInfo extends ModuleInfoAdapter {

	public ServiceDomainModuleInfo() {
//		addMenuItem(
//			MenuItem.builder()
//			.nameKey("Administration")
//			.nameDefault("Administration")
//			.location("/admin/")
//			.icon("gear")
//			.roles("USER,ADMIN,DOMAIN_ADMIN".split(","))
//			.order(1000)
//			.build()
//			.addChild(
//				MenuItem.builder()
//				.nameKey("Domains")
//				.nameDefault("Domains")
//				.location("/admin/domains")
//				.icon("globe")
//				.roles("USER,ADMIN,DOMAIN_ADMIN".split(","))
//				.order(200)
//				.build()
//			)
//		);

		addMenuItem(
			MenuItem.builder()
			.nameKey("menu.name.domain")
			.nameDefault("Domains")
			.location("dashboard.domains.list")
			.icon("cloud")
			.roles("DOMAIN_VIEW,DOMAIN_LIST,DOMAIN_EDIT".split(","))
			.order(2)
			.build()
		);
//		Category dashboardCategory = Category.builder().name("Dashboard").description("A dashboard overview of your domain.").build();

		Category category = Category.builder().name("Domain Operations").description("These are all the roles relevant to managing domains.").build();
		addRole(Role.builder().category(category).name("Dashboard").role("DASHBOARD").description("An overview of the general performance of the network or domain.").build());
//		addRole(Role.builder().category(category).name("Domain List").role("DOMAIN_LIST").description("View list of all domains.").build());
//		addRole(Role.builder().category(category).name("Domain View").role("DOMAIN_VIEW").description("View a domain").build());
		addRole(Role.builder().category(category).name("Domain Edit").role("DOMAIN_EDIT").description("Modify domain details.").build());
		addRole(Role.builder().category(category).name("Domain Create").role("DOMAIN_ADD").description("Create new domains.").build());
//		addRole(Role.builder().category(category).name("Domain Enable").role("DOMAIN_ENABLE").description("Enable / Disable Domains").build()); Still needs to be implemented.
//		addRole(Role.builder().category(category).name("Domain Users View").role("DOMAIN_USERS_VIEW").description("View Domain User Details").build());
//		addRole(Role.builder().category(category).name("Domain Users Edit").role("DOMAIN_USERS_EDIT").description("Edit Domain User Details").build());
//		addRole(Role.builder().category(category).name("Domain Users Add").role("DOMAIN_USERS_ADD").description("Add new users to a domain").build());
//		addRole(Role.builder().category(category).name("Domain Users Remove").role("DOMAIN_USERS_REMOVE").description("Remove users from a domain").build());

//		addRole(Role.builder().category(category).name("Domain Groups View").role("DOMAIN_GROUP_LIST").description("View Domain Groups").build());
//		addRole(Role.builder().category(category).name("Domain Groups View").role("DOMAIN_GROUP_VIEW").description("View Domain Group Details").build());
//		addRole(Role.builder().category(category).name("Domain Groups Edit").role("DOMAIN_GROUP_EDIT").description("Edit Domain Group Details").build());
//		addRole(Role.builder().category(category).name("Domain Groups Add").role("DOMAIN_GROUP_ADD").description("Add new groups to this domain.").build());
//		addRole(Role.builder().category(category).name("Domain Groups Add").role("DOMAIN_GROUP_ENABLE").description("Enable/Disable groups on this domain.").build());
//		addRole(Role.builder().category(category).name("Domain Groups Remove").role("DOMAIN_GROUP_DELETE").description("Remove groups from this domain.").build());
//		addRole(Role.builder().category(category).name("Domain Group Roles View").role("DOMAIN_GROUP_ROLES_VIEW").description("View Group Role Details").build());
//		addRole(Role.builder().category(category).name("Domain Group Roles Edit").role("DOMAIN_GROUP_ROLES_EDIT").description("Edit Group Role Details").build());
//		addRole(Role.builder().category(category).name("Domain Group Roles Add").role("DOMAIN_GROUP_ROLES_ADD").description("Add new roles to this group.").build());
//		addRole(Role.builder().category(category).name("Domain Group Roles Remove").role("DOMAIN_GROUP_ROLES_REMOVE").description("Remove roles from this group.").build());
//		addRole(Role.builder().category(category).name("Domain Group Users View").role("DOMAIN_GROUP_USERS_VIEW").description("View Group User Details").build());
//		addRole(Role.builder().category(category).name("Domain Group Users Edit").role("DOMAIN_GROUP_USERS_EDIT").description("Edit Group User Details").build());
//		addRole(Role.builder().category(category).name("Domain Group Users Add").role("DOMAIN_GROUP_USERS_ADD").description("Add new users to this group").build());
//		addRole(Role.builder().category(category).name("Domain Group Users Remove").role("DOMAIN_GROUP_USERS_REMOVE").description("Remove users from this group").build());
//		addRole(Role.builder().category(category).name("Domain Default Roles List").role("DOMAIN_DEFAULT_ROLES_LIST").description("List Domain Default Roles").build());
//		addRole(Role.builder().category(category).name("Domain Default Roles Enable").role("DOMAIN_DEFAULT_ROLES_ENABLE").description("Enable/disable Domain Default Roles").build());
//		addRole(Role.builder().category(category).name("Domain Default Roles Add").role("DOMAIN_DEFAULT_ROLES_ADD").description("Add new default roles to this domain.").build());
//		addRole(Role.builder().category(category).name("Domain Default Roles Remove").role("DOMAIN_DEFAULT_ROLES_REMOVE").description("Remove default roles from this domain.").build());
		addRole(Role.builder().category(category).name("Domain Email Templates View").role("DOMAIN_EMAIL_TEMPLATES_VIEW").description("View Domain Email Templates").build());
		addRole(Role.builder().category(category).name("Domain Email Templates Edit").role("DOMAIN_EMAIL_TEMPLATES_EDIT").description("Edit Domain Email Templates").build());
		addRole(Role.builder().category(category).name("Domain Email Templates Add").role("DOMAIN_EMAIL_TEMPLATES_ADD").description("Add new email templates to this domain.").build());
		addRole(Role.builder().category(category).name("Domain Email Templates Remove").role("DOMAIN_EMAIL_TEMPLATES_REMOVE").description("Remove email templates from this domain.").build());

		Category providerCategory = Category.builder().name("Provider Operations").description("These are all the roles relevant to managing providers.").build();
		addRole(Role.builder().category(providerCategory).name("Providers List").role("PROVIDERS_LIST").description("View list of all providers.").build());
		addRole(Role.builder().category(providerCategory).name("Provider View").role("PROVIDER_VIEW").description("View a provider.").build());
		addRole(Role.builder().category(providerCategory).name("Provider Edit").role("PROVIDER_EDIT").description("Edit a provider.").build());
		addRole(Role.builder().category(providerCategory).name("Provider Add").role("PROVIDER_ADD").description("Add a provider.").build());
		addRole(Role.builder().category(providerCategory).name("Provider Add Link").role("PROVIDER_ADD_LINK").description("Add a provider link to another provider.").build());
		addRole(Role.builder().category(providerCategory).name("Provider Edit Link").role("PROVIDER_EDIT_LINK").description("Edit a provider link to another provider.").build());

		Category templateCategory = Category.builder().name("Template Operations").description("These are all the roles relevant to managing templates.").build();
		addRole(Role.builder().category(templateCategory).name("Manage Templates").role("TEMPLATES_MANAGE").description("Manage templates.").build());
		addRole(Role.builder().category(templateCategory).name("Templates View").role("TEMPLATES_VIEW").description("View templates.").build());
		addRole(Role.builder().category(templateCategory).name("Templates Add").role("TEMPLATES_ADD").description("Add templates.").build());
		addRole(Role.builder().category(templateCategory).name("Templates Edit").role("TEMPLATES_EDIT").description("Edit templates.").build());
		addRole(Role.builder().category(templateCategory).name("Templates Publish").role("TEMPLATES_PUBLISH").description("Publish templates.").build());
    addRole(Role.builder().category(templateCategory).name("Templates Delete").role("TEMPLATES_DELETE").description("Delete templates.").build());

		Category settingsCategory = Category.builder().name("Domain Settings Operations").description("These are all the roles relevant to managing domain settings.").build();
		addRole(Role.builder().category(settingsCategory).name("View Domain Settings").role("DOMAIN_SETTINGS_VIEW").description("View domain settings.").build());
		addRole(Role.builder().category(settingsCategory).name("Edit Domain Settings").role("DOMAIN_SETTINGS_EDIT").description("Edit domain settings.").build());

		Category clientsCategory = Category.builder().name("Domain Provider Auth Operations").description("These are all the roles relevant to managing domain clients.").build();
		addRole(Role.builder().category(clientsCategory).name("View Domain Provider Auth Clients").role("DOMAIN_PROVIDERAUTH_VIEW").description("View domain clients.").build());
		addRole(Role.builder().category(clientsCategory).name("Edit Domain Provider Auth Clients").role("DOMAIN_PROVIDERAUTH_EDIT").description("Edit domain clients.").build());
  }

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/domain/{domainName}/providers/listbytype").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'TEMPLATES_VIEW', 'TEMPLATES_ADD', 'TEMPLATES_EDIT', 'TEMPLATES_MANAGE', 'WEB_ASSET_MANAGE', 'BANNER_IMAGE_MANAGE','GAME_TILE_MANAGE')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/*/providers/auth").permitAll();

//		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domains/findByName").access("@lithiumSecurity.hasRole(authentication, 'DOMAIN_ADD')"); ???
		http.authorizeRequests().antMatchers("/domain/{domainName}/setting/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_EDIT', 'DOMAIN_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domains").access("@lithiumSecurity.hasRole(authentication, 'DOMAIN_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domains/findAll").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domains/*").access("@lithiumSecurity.hasRoleInTree(authentication, 'DOMAIN_ADD', 'DOMAIN_EDIT', 'USER_*', 'PLAYER_*')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/**").access("@lithiumSecurity.hasRoleInTree(authentication, #domainName, 'DOMAIN_ADD', 'DOMAIN_EDIT', 'USER_*', 'PLAYER_*')");

//		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/children").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_VIEW', 'DOMAIN_EDIT', 'DOMAIN_LIST', 'DOMAIN_ADD')");
//		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/ancestors").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_VIEW', 'DOMAIN_EDIT', 'DOMAIN_LIST', 'DOMAIN_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_VIEW', 'DOMAIN_EDIT', 'DOMAIN_LIST', 'DOMAIN_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/bettingenabled/toggle").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/saveaddress").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/savebankingdetails").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/updateCurrency").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/roles/add/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_DEFAULT_ROLES_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/{entityId}/changelogs").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, #entityId, 'DOMAIN_VIEW', 'DOMAIN_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/*/enabled/*").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_DEFAULT_ROLES_ENABLE')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/*/delete").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_DEFAULT_ROLES_REMOVE')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/roles").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ADMIN')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/*/providers/listbydomainandtype").access("@lithiumSecurity.authenticatedSystem(authentication)");
//		http.authorizeRequests().antMatchers("/**").hasAnyAuthority("ROLE_SYSTEM", "ROLE_USER");
//		http.authorizeRequests()
//			.antMatchers(HttpMethod.GET, "/domain/{domainName}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_VIEW')");

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/providers").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDERS_LIST','PROVIDER_VIEW','PROVIDER_EDIT', 'PROVIDER_EDIT_LINK')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/providers/linksList").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDERS_LIST','PROVIDER_VIEW','PROVIDER_EDIT', 'PROVIDER_EDIT_LINK')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/providers/listbydomainandtype").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/domain/provider/listAllProvidersByType").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/domain/provider/propertiesByProviderUrlAndDomainName").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/providers/add").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDER_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/providers/addLink").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDER_ADD','PROVIDER_ADD_LINK')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/provider/*/view").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDERS_LIST','PROVIDER_VIEW','PROVIDER_EDIT','PROVIDER_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/provider/*/edit").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/provider/*/linksListByProviderId").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDERS_LIST','PROVIDER_VIEW','PROVIDER_EDIT','PROVIDER_ADD','PROVIDER_EDIT_LINK','PROVIDER_ADD_LINK')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/providers/availableProviderLinksList").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDER_EDIT_LINK','PROVIDER_ADD_LINK','PROVIDERS_LIST','PROVIDER_VIEW','PROVIDER_EDIT','PROVIDER_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/providers/viewLink").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDER_ADD','PROVIDER_ADD_LINK','PROVIDER_EDIT_LINK', 'PROVIDER_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/{domainName}/providers/findOwnerLink").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDER_ADD','PROVIDER_ADD_LINK','PROVIDER_EDIT_LINK', 'PROVIDER_EDIT','PROVIDERS_LIST','PROVIDER_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/{domainName}/providers/editLink").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PROVIDER_ADD','PROVIDER_ADD_LINK','PROVIDER_EDIT_LINK', 'PROVIDER_EDIT')");

		http.authorizeRequests().antMatchers("/domain/{domainId}/image/{name}").access("@lithiumSecurity.hasRole(authentication, 'DOMAIN_EDIT')");

		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");

		http.authorizeRequests().antMatchers("/{domainName}/templates/findByNameAndLangAndDomainName").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/{domainName}/templates/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'TEMPLATES_MANAGE', 'TEMPLATES_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/{domainName}/templates/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'TEMPLATES_MANAGE', 'TEMPLATES_ADD')");
		http.authorizeRequests().antMatchers(HttpMethod.GET,"/{domainName}/template/{id}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'TEMPLATES_MANAGE', 'TEMPLATES_VIEW')");
		http.authorizeRequests().antMatchers(HttpMethod.POST,"/{domainName}/template/{id}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'TEMPLATES_MANAGE', 'TEMPLATES_EDIT')");
    http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/{domainName}/template/{id}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'TEMPLATES_MANAGE', 'TEMPLATES_DELETE')");

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/settings/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_SETTINGS_VIEW', 'DOMAIN_SETTINGS_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/settings/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_SETTINGS_EDIT')");

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/system/domain/providerauthclient/{domainName}/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/domain/providerauthclient/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_PROVIDERAUTH_VIEW', 'DOMAIN_PROVIDERAUTH_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/domain/providerauthclient/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_PROVIDERAUTH_EDIT')");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/domain/providerauthclient/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'DOMAIN_PROVIDERAUTH_EDIT')");

    http.authorizeRequests().antMatchers(HttpMethod.GET, "/backoffice/ecosystem/**").access("@lithiumSecurity.hasRole(authentication, 'DOMAIN_ADD', 'ECOSYSTEMS_VIEW', 'PLAYER_VIEW')");
    http.authorizeRequests().antMatchers(HttpMethod.POST, "/backoffice/ecosystem/**").access("@lithiumSecurity.hasRole(authentication, 'DOMAIN_ADD', 'ECOSYSTEMS_ADD', 'ECOSYSTEMS_EDIT')");;

    http.authorizeRequests().antMatchers(HttpMethod.GET,"/backoffice/{domainName}/asset/templates/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'TEMPLATES_MANAGE', 'TEMPLATES_ADD')");
    http.authorizeRequests().antMatchers(HttpMethod.POST,"/backoffice/{domainName}/asset/templates/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'TEMPLATES_MANAGE', 'TEMPLATES_ADD')");
    http.authorizeRequests().antMatchers("/backoffice/{domainName}/asset/templates/{id}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'TEMPLATES_MANAGE', 'TEMPLATES_ADD')");
    http.authorizeRequests().antMatchers(HttpMethod.GET, "/domains/find-all-player-domains").access("@lithiumSecurity.hasRoleInTree(authentication, 'CASHIER_BANK_ACCOUNT_LOOKUP')");
    http.authorizeRequests().antMatchers("/complete-placeholders/**").access("@lithiumSecurity.authenticatedSystem(authentication)");

  }
}
