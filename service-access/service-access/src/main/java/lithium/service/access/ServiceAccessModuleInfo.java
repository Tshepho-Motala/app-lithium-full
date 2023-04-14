package lithium.service.access;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class ServiceAccessModuleInfo extends ModuleInfoAdapter {
	public ServiceAccessModuleInfo() {
		Category accessControlCategory = Category.builder().name("Access List Operations").description("Operations related to access lists.").build();
		addRole(Role.builder().category(accessControlCategory).name("Access List View").role("ACCESSCONTROL_VIEW").description("View Access Control List Details").build());
		addRole(Role.builder().category(accessControlCategory).name("Access List Edit").role("ACCESSCONTROL_EDIT").description("Edit Access Control List Details").build());
		addRole(Role.builder().category(accessControlCategory).name("Access List Create").role("ACCESSCONTROL_ADD").description("Create Access Control Lists").build());
		
		Category accessRulesCategory = Category.builder().name("Access Ruleset Operations").description("Operations related to access rulesets.").build();
		addRole(Role.builder().category(accessRulesCategory).name("Access Ruleset View").role("ACCESSRULES_VIEW").description("View Access Ruleset Details").build());
		addRole(Role.builder().category(accessRulesCategory).name("Access Ruleset Edit").role("ACCESSRULES_EDIT").description("Edit Access Ruleset Details").build());
		addRole(Role.builder().category(accessRulesCategory).name("Access Ruleset Create").role("ACCESSRULES_ADD").description("Create Access Ruleset").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests()
      .antMatchers(HttpMethod.POST, "/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)")

			.antMatchers(HttpMethod.GET, "/listTypes/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'ACCESSCONTROL_ADD')")

      .antMatchers(HttpMethod.GET, "/lists/find/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ACCESSCONTROL_VIEW','ACCESSCONTROL_EDIT','ACCESSCONTROL_ADD','ACCESSRULES_VIEW','ACCESSRULES_EDIT','ACCESSRULES_ADD')")
      .antMatchers(HttpMethod.GET, "/lists/findByName/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ACCESSCONTROL_VIEW','ACCESSCONTROL_EDIT','ACCESSCONTROL_ADD','ACCESSRULES_VIEW','ACCESSRULES_EDIT','ACCESSRULES_ADD')")
			.antMatchers(HttpMethod.GET, "/lists/table").access("@lithiumSecurity.hasRoleInTree(authentication, 'ACCESSCONTROL_VIEW','ACCESSCONTROL_EDIT','ACCESSCONTROL_ADD','ACCESSRULES_VIEW','ACCESSRULES_EDIT','ACCESSRULES_ADD')")
			.antMatchers(HttpMethod.POST, "/lists/create").access("@lithiumSecurity.hasRoleInTree(authentication, 'ACCESSCONTROL_ADD')")
			
			.antMatchers(HttpMethod.GET, "/list/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'ACCESSCONTROL_VIEW','ACCESSCONTROL_EDIT','ACCESSCONTROL_ADD','ACCESSRULES_VIEW','ACCESSRULES_EDIT','ACCESSRULES_ADD')")
			.antMatchers(HttpMethod.POST, "/list/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'ACCESSCONTROL_ADD','ACCESSCONTROL_EDIT')")

      .antMatchers(HttpMethod.GET, "/accessrules/findByDomain/{domainName}").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ACCESSRULES_VIEW','ACCESSRULES_EDIT','ACCESSRULES_ADD')")
			.antMatchers(HttpMethod.GET, "/accessrules/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'ACCESSRULES_VIEW','ACCESSRULES_EDIT','ACCESSRULES_ADD')")
			.antMatchers(HttpMethod.POST, "/accessrules/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'ACCESSRULES_EDIT','ACCESSRULES_ADD')")
			
			.antMatchers(HttpMethod.GET, "/backoffice/ruleset/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'ACCESSRULES_VIEW','ACCESSRULES_EDIT','ACCESSRULES_ADD')")
			.antMatchers(HttpMethod.POST, "/backoffice/ruleset/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'ACCESSRULES_EDIT','ACCESSRULES_ADD')")

			.antMatchers(HttpMethod.POST, "/authorization/{domainName}/{accessRuleName}/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'ACCESSRULES_VIEW','ACCESSRULES_EDIT','ACCESSRULES_ADD')")

      .antMatchers(HttpMethod.POST, "/external/authorization/{domainName}/{accessRuleName}/check-authorization").permitAll()

			.antMatchers(HttpMethod.GET, "/frontend/helper/**").permitAll();
	}
}
