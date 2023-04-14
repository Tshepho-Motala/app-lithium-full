package lithium.service.casino;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;

@Component
public class ServiceCasinoModuleInfo extends ModuleInfoAdapter {
	public ServiceCasinoModuleInfo() {
		super();
		Category category = Category.builder().name("Casino Operations").description("These are all the roles relevant to managing casinos.").build();
		addRole(Role.builder().category(category).name("Casino List").role("CASINO_LIST").description("View list of all casinos.").build());
		addRole(Role.builder().category(category).name("Bonus View").role("BONUS_VIEW").description("View all bonuses.").build());
		addRole(Role.builder().category(category).name("Bonus Edit").role("BONUS_EDIT").description("Edit bonuses.").build());
		addRole(Role.builder().category(category).name("Bonus Add").role("BONUS_ADD").description("Add bonuses.").build());
		addRole(Role.builder().category(category).name("Manual Bonus Allocation").role("MANUAL_BONUS_ALLOCATION").description("Manually allocate bonuses to players.").build());
		addRole(Role.builder().category(category).name("Grant Bonus Allocation").role("GRANT_BONUS_ALLOCATION").description("Grant bonuses to players.").build());
		addRole(Role.builder().category(category).name("Bonus Allocation").role("BONUS_ALLOCATION").description("Allocate bonuses to players").build());
		addRole(Role.builder().category(category).name("Mass Bonus Allocation View").role("MASS_BONUS_ALLOCATION_VIEW").description("Mass allocate bonuses to players.").build());
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/frontend/**").authenticated();
		http.authorizeRequests().antMatchers("/system/**").access("@lithiumSecurity.authenticatedSystem(authentication)");

		http.authorizeRequests().antMatchers("/backoffice/{domainName}/bonus/{bonusType}/find/active").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_BONUSES_VIEW', 'GRANT_BONUS_ALLOCATION', 'MASS_BONUS_ALLOCATION_VIEW')");
		http.authorizeRequests().antMatchers("/backoffice/{domainName}/bonus/{bonusType}/history").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'PLAYER_BONUSES_VIEW')");
		http.authorizeRequests().antMatchers("/backoffice/{domainName}/bonus/{bonusType}/manual/**").access("@lithiumSecurity.hasDomainRole(authentication, #domainName, 'GRANT_BONUS_ALLOCATION')");
		http.authorizeRequests().antMatchers("/casino/bonus/table/**").access("@lithiumSecurity.hasRoleInTree(authentication, 'BONUS_VIEW')");
		http.authorizeRequests().antMatchers("/casino/bonus/manual/**").access("@lithiumSecurity.hasRole(authentication, 'MANUAL_BONUS_ALLOCATION')");
		http.authorizeRequests().antMatchers("/casino/bonus/auto/**").access("@lithiumSecurity.hasRole(authentication, 'BONUS_ALLOCATION')");
		http.authorizeRequests().antMatchers("/casino/bonus/find/{domainName}/{type}/public/all").permitAll();
		http.authorizeRequests().antMatchers("/casino/bonus/find/{domainName}/{type}/public/all/v2").permitAll();
		http.authorizeRequests().antMatchers("/casino/bonus/find/{domainName}/{type}/{bonusCode}").permitAll();
		http.authorizeRequests().antMatchers("/casino/winner/list").permitAll();
		http.authorizeRequests().antMatchers("/casino/bonus/remove").access("@lithiumSecurity.hasRole(authentication, 'BONUS_EDIT')");
		http.authorizeRequests().antMatchers("/casino/bonus/**").authenticated();
		http.authorizeRequests().antMatchers("/casino/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/casino/winner/add").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().antMatchers("/casino/winner/v2/add").access("@lithiumSecurity.authenticatedSystem(authentication)");
	}
}
